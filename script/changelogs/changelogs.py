#!/usr/bin/python3
# -*- coding: UTF-8 -*-

import re
from pathlib import Path
from typing import Optional

# markdown path
CHANGELOG_MD_EN = "../../CHANGELOG.md"
CHANGELOG_MD_ZH = "../../CHANGELOG_zh.md"

# changelog output dir
OUTPUT_DIR_EN = "../../fastlane/metadata/android/en-US/changelogs"
OUTPUT_DIR_ZH = "../../fastlane/metadata/android/zh-CN/changelogs"

# markdown version title regex, e.g. ### v1.2.3 (2024-01-01)
REGEX_VERSION_TITLE = r"^#+ v([\d.]+)\s*(\([\d-]+\))?$"
# markdown changelog line regex, e.g. - Some description
REGEX_CHANGELOG = r"^- .+$"
# markdown sub link regex, e.g. [#883](https://...)
REGEX_LINK_SUB = r'\[([#@]?\S+)]\([^)]+\)'

# version script path
VERSION_SCRIPT = "../../app/build.gradle.kts"

# versionName = "5.3.1" or "5.3.1-beta1"
REGEX_VERSION_NAME = r'versionName\s+=\s+"([\d.]+(?:-[a-z]+\d+)?)"'
# versionCode = 80
REGEX_VERSION_CODE = r'versionCode\s+=\s+(\d+)'


def get_app_version_info(build_script: str) -> tuple[str, str]:
    """Get build script version info.

    Args:
        build_script: build.gradle file path.

    Returns:
        A tuple[str, str], (versionName, versionCode).
    """
    content = Path(build_script).read_text(encoding="utf-8")
    name_match = re.search(REGEX_VERSION_NAME, content)
    code_match = re.search(REGEX_VERSION_CODE, content)
    assert name_match is not None, f"versionName not found in {build_script}"
    assert code_match is not None, f"versionCode not found in {build_script}"
    return name_match.group(1), code_match.group(1)


def get_latest_changelog(markdown: str) -> tuple[Optional[str], str]:
    """Get changelog markdown latest changelog info.

    Args:
        markdown: changelog.md file path.

    Returns:
        A tuple[str | None, str], (versionName, changelogContent).
    """
    lines: list[str] = []
    version_name: Optional[str] = None
    found = False
    for line in Path(markdown).read_text(encoding="utf-8").splitlines():
        if found and re.match(REGEX_CHANGELOG, line):
            line = re.sub(REGEX_LINK_SUB, r"\g<1>", line).strip()
            lines.append(line)
        version_title_match = re.search(REGEX_VERSION_TITLE, line)
        if version_title_match is not None:
            if found:
                break
            version_name = version_title_match.group(1)
            found = True
    changelog = "\n".join(lines)
    return version_name, changelog


def output_changelog_txt(dir_path: str, version_code: str, changelog: str) -> str:
    """Output changelog.txt.

    Args:
        dir_path: Output dir.
        version_code: File name, version_code.txt.
        changelog: Changelog content.

    Returns:
        Output changelog.txt path.
    """
    file = Path(dir_path) / f"{version_code}.txt"
    file.write_text(changelog, encoding="utf-8")
    return str(file)


def getpath(path: str) -> str:
    script_dir = Path(__file__).resolve().parent
    return str(script_dir.joinpath(path).resolve())


def output_fastlane_changelog(changelog_md: str, output_dir: str) -> None:
    """Output fastlane changelog.txt.

    Args:
        changelog_md: changelog.md file path.
        output_dir: Output dir.

    Raises:
        ValueError: App versionName != Changelog versionName, or Changelog content isEmpty.
    """
    version_info = get_app_version_info(getpath(VERSION_SCRIPT))
    print(f"Get app info: {version_info}")

    changelog_info = get_latest_changelog(getpath(changelog_md))
    print(f"Get changelog info: {changelog_info}")

    version_name, version_code = version_info
    changelog_version_name, changelog = changelog_info

    if version_name != changelog_version_name:
        raise ValueError(
            f"Not found App changelog, App version_name: {version_name}, "
            f"Changelog version_name: {changelog_version_name}"
        )
    if not changelog:
        raise ValueError("Changelog isEmpty!")

    output_path = output_changelog_txt(getpath(output_dir), version_code, changelog)
    print(f"Output changelog path: {output_path}")


def main():
    output_fastlane_changelog(CHANGELOG_MD_EN, OUTPUT_DIR_EN)
    output_fastlane_changelog(CHANGELOG_MD_ZH, OUTPUT_DIR_ZH)


if __name__ == "__main__":
    main()
