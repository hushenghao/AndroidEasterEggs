#!/usr/bin/python3
# -*- coding: UTF-8 -*-

import os
import re

# markdown path
CHANGELOG_MD_EN = "../../CHANGELOG.md"
CHANGELOG_MD_ZH = "../../CHANGELOG_zh.md"

# changelog output dir
OUTPUT_DIR_EN = "../../fastlane/metadata/android/en-US/changelogs"
OUTPUT_DIR_ZH = "../../fastlane/metadata/android/zh-CN/changelogs"

REGEX_VERSION_TITLE = r"^#+ v((\d|.)+(-[a-z]+\d+)?).*$"  # markdown version title regex, ### v1.2.3-beta01 (2024-01-01)
REGEX_CHANGELOG = r"^(- .+)+$"  # markdown changelog line regex
REGEX_LINK_SUB = r'\[#.+\]\(\S+\)'  # markdown sub link regex

# version script path
VERSION_SCRIPT = "../../app/build.gradle.kts"

REGEX_VERSION_NAME = r'versionName\s+=\s+"((\d|.)+(-[a-z]+\d+)?)"'  # versionName regex
REGEX_VERSION_CODE = r'versionCode\s+=\s+(\d+)'  # versionCode reges


def get_app_version_info(build_script: str) -> tuple[str, int]:
    """Get build script version info.
    Args:
        build_script: build.gradle file path.
    Returns:
        A tuple[str, int], (versionName, versionCode).
    """
    version_name = None
    version_code = None
    with open(build_script, mode="r", encoding="utf-8") as f:
        content = f.read()
        version_name = re.search(REGEX_VERSION_NAME, content).group(1)
        version_code = re.search(REGEX_VERSION_CODE, content).group(1)
    return (version_name, version_code)


def get_latest_changelog(markdown: str) -> tuple[str, str]:
    """Get changelog markdown latest changelog info.
    Args:
        markdown: changelog.md file path.
    Returns:
        A tuple[str, str], (versionName, changelogContent).
    """
    changelog = ""
    version_name = None
    with open(markdown, mode="r", encoding='utf-8') as f:
        finded = False
        while (True):
            line = f.readline()
            if line == None:
                break
            if (finded and re.match(REGEX_CHANGELOG, line)):
                changelog += re.sub(REGEX_LINK_SUB, "", line).strip() + "\n"
            version_title_match = re.search(REGEX_VERSION_TITLE, line)
            if (version_title_match != None):
                if finded:
                    break
                version_name = version_title_match.group(1)
                finded = True
        f.close()
    changelog = changelog.strip()
    return (version_name, changelog)


def output_changelog_txt(dir: str, version_code: int, changelog: str) -> str:
    """Output changelog.txt.
    Args:
        dir: Output dir.
        version_code: File name, version_code.txt.
        changelog: Changelog content.
    Returns:
        Output changelog.txt path.
    """
    file = os.path.join(dir, f"{version_code}.txt")
    with open(file, mode="w", encoding="utf-8") as f:
        f.write(changelog)
        f.close()
    return file


def getpath(path: str) -> str:
    return os.path.abspath(os.path.join(os.getcwd(), path))


def output_fastlane_changelog(changelog_md, output_dir):
    """Output fastlane changelog.txt.
    Args:
        changelog_md: changelog.md file path.
        output_dir: Output dir, example: fastlane/metadata/android/en-US/changelogs/.
    Raises:
        ValueError: App versionName != Changelog versionName, or Changelog content isEmpty.
    """
    version_info = get_app_version_info(getpath(VERSION_SCRIPT))
    print(f"Get app info: {version_info}")

    changelog_info = get_latest_changelog(getpath(changelog_md))
    print(f"Get changelog info: {changelog_info}")

    version_name = version_info[0]
    version_code = version_info[1]
    changelog_version_name = changelog_info[0]
    changelog = changelog_info[1]

    if version_name != changelog_version_name:
        raise ValueError(
            f"Not found App changelog, App version_name: {version_name}, Changelog version_name: {changelog_version_name}"
        )
    if changelog == None or changelog == "":
        raise ValueError("Changelog isEmpty!")

    output_path = output_changelog_txt(getpath(output_dir), version_code,
                                       changelog)
    print(f"Output changelog path: {output_path}")


def main():
    output_fastlane_changelog(CHANGELOG_MD_EN, OUTPUT_DIR_EN)
    output_fastlane_changelog(CHANGELOG_MD_ZH, OUTPUT_DIR_ZH)


if __name__ == "__main__":
    main()
