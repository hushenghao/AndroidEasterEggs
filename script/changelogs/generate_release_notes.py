#!/usr/bin/env python3
"""Generate release notes from Conventional Commits between git refs."""

import argparse
import datetime
import re
import subprocess
import sys
from pathlib import Path
from typing import Optional


TYPE_LABELS: dict[str, str] = {
    'feat': '✨ New Features',
    'fix': '🐛 Bug Fixes',
    'perf': '⚡ Performance Improvements',
    'refactor': '♻️ Code Refactoring',
    'style': '🎨 Code Style',
    'doc': '📝 Documentation',
    'build': '🏗️ Build System',
    'chore': '🔧 Chores',
    'del': '🗑️ Removals',
}

TYPE_ORDER: list[str] = [
    'feat', 'fix', 'perf', 'refactor', 'style',
    'doc', 'build', 'chore', 'del',
]

EXCLUDED_TYPES: set[str] = {'ci'}


def run_git(*args: str) -> str:
    return subprocess.run(
        ('git', *args), capture_output=True, text=True, check=True,
    ).stdout.strip()


def get_previous_version_tag(current_tag: str) -> Optional[str]:
    tags = run_git('tag', '--sort=-version:refname').splitlines()
    pattern = re.compile(r'^v?\d+\.\d+\.\d+')
    version_tags = [t for t in tags if pattern.match(t)]
    try:
        idx = version_tags.index(current_tag)
        if idx + 1 < len(version_tags):
            return version_tags[idx + 1]
    except ValueError:
        pass
    try:
        idx = tags.index(current_tag)
        if idx + 1 < len(tags):
            return tags[idx + 1]
    except ValueError:
        pass
    return None


def get_commit_messages(from_ref: Optional[str], to_ref: str) -> list[str]:
    if from_ref:
        try:
            run_git('merge-base', '--is-ancestor', from_ref, to_ref)
        except subprocess.CalledProcessError:
            print(
                f'warning: {from_ref} is not an ancestor of {to_ref}, '
                f'falling back to full history',
                file=sys.stderr,
            )
            from_ref = None

    log_range = f'{from_ref}..{to_ref}' if from_ref else to_ref
    output = run_git(
        'log', '--oneline', '--no-merges', '--format=%s', log_range,
    )
    if not output:
        return []
    return output.splitlines()


_CONVENTIONAL_RE = re.compile(
    r'^(?P<type>feat|fix|chore|build|ci|del|style|doc|docs|refactor|perf|pref|test)'
    r':\s*(?P<desc>.*?)(?:\s*\(#(?P<pr>\d+)\))?\s*$'
)

_FIX_PREFIX_RE = re.compile(
    r'^(?i:fix(?:ed|es)?(?:\s+(?:the\s+)?(?:issue\s+)?(?:where\s+)?))'
)


def polish_description(desc: str, *, is_fix: bool) -> Optional[str]:
    d = desc.strip()
    if not d:
        return None
    d = d.rstrip('.')
    if is_fix:
        d = _FIX_PREFIX_RE.sub('', d).strip()
    if not d:
        return None
    if d[0].islower():
        d = d[0].upper() + d[1:]
    return d


def parse_conventional_commits(lines: list[str]) -> dict[str, list[str]]:
    grouped: dict[str, list[str]] = {t: [] for t in TYPE_ORDER}
    for line in lines:
        line = line.strip()
        if not line:
            continue
        m = _CONVENTIONAL_RE.match(line)
        if not m:
            continue
        raw_type = m.group('type')
        raw_desc = m.group('desc').strip()
        pr = m.group('pr')

        if raw_type == 'pref':
            raw_type = 'perf'
        if raw_type == 'docs':
            raw_type = 'doc'
        if raw_type == 'test':
            continue
        if raw_type in EXCLUDED_TYPES:
            continue
        if raw_type not in grouped:
            continue

        polished = polish_description(raw_desc, is_fix=(raw_type == 'fix'))
        if polished is None:
            continue
        if pr:
            grouped[raw_type].append(f'{polished} (#{pr})')
        else:
            grouped[raw_type].append(polished)
    return grouped


def format_release(grouped: dict[str, list[str]], tag: str) -> str:
    parts = [f'## {tag}', '']
    any_items = False
    for t in TYPE_ORDER:
        items = grouped[t]
        if not items:
            continue
        any_items = True
        parts.append(f'### {TYPE_LABELS.get(t, t.capitalize())}')
        parts.extend(f'- {i}' for i in items)
        parts.append('')
    if not any_items:
        parts.append('_No notable changes_')
    return '\n'.join(parts).strip() + '\n'


def format_draft(grouped: dict[str, list[str]], tag: str) -> str:
    today = datetime.date.today().strftime('(%Y-%m-%d)')
    parts = [f'### v{tag.lstrip("v")} {today}', '']
    any_items = False
    for t in TYPE_ORDER:
        items = grouped[t]
        if not items:
            continue
        any_items = True
        parts.extend(f'- {i}' for i in items)
    if not any_items:
        parts.append('- No significant changes')
    return '\n'.join(parts).strip() + '\n'


def main() -> None:
    parser = argparse.ArgumentParser(
        description='Generate release notes from Conventional Commits.',
    )
    parser.add_argument(
        '--from-ref',
        help='Starting git ref (tag/commit). Auto-detected if omitted.',
    )
    parser.add_argument(
        '--to-ref', default='HEAD',
        help='Ending git ref (default: HEAD).',
    )
    parser.add_argument(
        '--current-tag',
        help='Current version tag for display (e.g. v5.3.1). Defaults to --to-ref.',
    )
    parser.add_argument(
        '--output', '-o',
        help='Write output to file instead of stdout.',
    )
    parser.add_argument(
        '--draft', action='store_true',
        help='Output flat draft format for CHANGELOG.md (vs grouped release format).',
    )
    args = parser.parse_args()

    to_ref = args.to_ref
    current_tag = args.current_tag or to_ref

    from_ref = args.from_ref
    if not from_ref:
        from_ref = get_previous_version_tag(current_tag)

    commits = get_commit_messages(from_ref, to_ref)
    grouped = parse_conventional_commits(commits)

    if args.draft:
        output = format_draft(grouped, current_tag)
    else:
        output = format_release(grouped, current_tag)

    if args.output:
        Path(args.output).write_text(output)
        print(f'Release notes written to {args.output}', file=sys.stderr)
    else:
        print(output)


if __name__ == '__main__':
    main()
