#!/usr/bin/env python3
"""Build the MkDocs site source from the wiki submodule for GitHub Pages.

The wiki submodule stays read-only and keeps serving GitHub wiki. This script
copies the published pages into pages-docs/ with site-specific adjustments:

- English pages at the root, zh-CN pages under zh/.
- The Home pages lose the centered badge block (<div align="center">...</div>).
- Absolute GitHub wiki URLs are rewritten to relative .md links so MkDocs
  resolves them against use_directory_urls. Wiki pages that are not published
  here (e.g. the retired 隐私协议 placeholder) keep their GitHub URL.
"""

import posixpath
import re
import shutil
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
WIKI_DIR = REPO_ROOT / "wiki"
DOCS_DIR = Path(__file__).resolve().parent / "pages-docs"

WIKI_URL_PREFIX = "https://github.com/hushenghao/AndroidEasterEggs/wiki/"

# Wiki page name (case-insensitive) -> docs-relative .md target
PAGE_TARGETS = {
    "home": "index.md",
    "android-versions": "android-versions.md",
    "sponsor": "sponsor.md",
    "privacy-policy": "privacy-policy.md",
    "the-application-failed-to-uninstall": "the-application-failed-to-uninstall.md",
    "home-zh-cn": "zh/index.md",
    "android-versions-zh-cn": "zh/android-versions.md",
    "sponsor-zh-cn": "zh/sponsor.md",
    "privacy-policy-zh-cn": "zh/privacy-policy.md",
    "the-application-failed-to-uninstall-zh-cn": "zh/the-application-failed-to-uninstall.md",
}

# (wiki file, docs-relative target). The retired 隐私协议.md placeholder and
# the _Sidebar.md files are intentionally not published; navigation lives in
# mkdocs.yml instead.
SOURCES = [
    ("Home.md", "index.md"),
    ("Android-versions.md", "android-versions.md"),
    ("The-application-failed-to-uninstall.md", "the-application-failed-to-uninstall.md"),
    ("Sponsor.md", "sponsor.md"),
    ("Privacy-policy.md", "privacy-policy.md"),
    ("locale/zh-CN/Home-zh-CN.md", "zh/index.md"),
    ("locale/zh-CN/Android-versions-zh-CN.md", "zh/android-versions.md"),
    ("locale/zh-CN/The-application-failed-to-uninstall-zh-CN.md", "zh/the-application-failed-to-uninstall.md"),
    ("locale/zh-CN/Sponsor-zh-CN.md", "zh/sponsor.md"),
    ("locale/zh-CN/Privacy-policy-zh-CN.md", "zh/privacy-policy.md"),
]

BADGE_BLOCK = re.compile(r'<div align="center">.*?</div>\s*', re.DOTALL)


def strip_badges(text: str) -> str:
    return BADGE_BLOCK.sub("", text, count=1)


def rewrite_links(text: str, source_dir: str) -> str:
    def replace(match: re.Match) -> str:
        target = PAGE_TARGETS.get(match.group(1).lower())
        if target is None:
            return match.group(0)
        return posixpath.relpath(target, source_dir or ".")

    pattern = re.escape(WIKI_URL_PREFIX) + r"([A-Za-z0-9\-_%]+)"
    return re.sub(pattern, replace, text)


def main() -> None:
    if not (WIKI_DIR / "Home.md").is_file():
        sys.exit("wiki submodule is not checked out; run: git submodule update --init")
    if DOCS_DIR.exists():
        shutil.rmtree(DOCS_DIR)
    for source, target in SOURCES:
        source_path = WIKI_DIR / source
        if not source_path.is_file():
            sys.exit(f"missing wiki page: {source}")
        target_path = DOCS_DIR / target
        target_path.parent.mkdir(parents=True, exist_ok=True)
        text = source_path.read_text(encoding="utf-8")
        if target_path.name == "index.md":
            text = strip_badges(text)
        text = rewrite_links(text, posixpath.dirname(target))
        target_path.write_text(text, encoding="utf-8")
        print(f"  {source} -> {target}")


if __name__ == "__main__":
    main()
