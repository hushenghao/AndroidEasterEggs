#!/usr/bin/python
# -*- coding: UTF-8 -*-

import os
import re

changelog_md_en = "../../CHANGELOG.md"  # markdown path
changelog_md_zh = "../../CHANGELOG_zh.md"  # markdown path

regex_version_title = r"^#+ v([\d\.]+).*$"  # markdown version title regex
regex_changelog = r"^(- .+)+$"  # markdown changelog line regex
regex_link_sub = r'\[#.+\]\(\S+\)'  # markdown sub link regex

version_script = "../../app/build.gradle.kts"  # version script path

regex_version_name = r'versionName\s+=\s+"([\d\.]+)"'  # versionName regex
regex_version_code = r'versionCode\s+=\s+(\d+)'  # versionCode reges

output_dir_en = "../../fastlane/metadata/android/en-US/changelogs"
output_dir_zh = "../../fastlane/metadata/android/zh-CN/changelogs"


def getAppVersionInfo(build_script: str):
    version_name = None
    version_code = None
    with open(build_script, mode="r", encoding="utf-8") as f:
        content = f.read()
        version_name = re.search(regex_version_name, content).group(1)
        version_code = re.search(regex_version_code, content).group(1)
    return {"version_name": version_name, "version_code": version_code}


def getLatestChangelog(markdown: str):
    changelog = ""
    version_name = None
    with open(markdown, mode="r", encoding='utf-8') as f:
        finded = False
        while (True):
            line = f.readline()
            if line == None:
                break
            if (finded and re.match(regex_changelog, line)):
                changelog += re.sub(regex_link_sub, "", line).strip() + "\n"
            version_title_match = re.search(regex_version_title, line)
            if (version_title_match != None):
                if finded:
                    break
                version_name = version_title_match.group(1)
                finded = True
        f.close()
    changelog = changelog.strip()
    return {"version_name": version_name, "changelog": changelog}


def outputChangelogTxt(dir: str, version_code: int, changelog: str):
    file = os.path.join(dir, "{}.txt".format(version_code))
    with open(file, mode="w", encoding="utf-8") as f:
        f.write(changelog)
        f.close()


def getpath(path: str):
    return os.path.join(os.getcwd(), path)


def fastlaneChangelog(changelog_md, output_dir):
    version_info = getAppVersionInfo(getpath(version_script))
    print("Get App Info: {}".format(version_info))

    changelog_info = getLatestChangelog(getpath(changelog_md))
    print("Get Changelog Info: {}".format(changelog_info))

    version_name = version_info["version_name"]
    version_code = version_info["version_code"]
    changelog_version_name = changelog_info["version_name"]
    changelog = changelog_info["changelog"]

    if version_name != changelog_version_name:
        raise Exception(
            "Not found App changelog, App version_name: {}, Changelog version_name: {}"
            .format(version_name, changelog_version_name))
    if changelog == None or changelog == "":
        raise Exception("Changelog isEmpty!")

    outputChangelogTxt(getpath(output_dir), version_code, changelog)


def main():
    fastlaneChangelog(changelog_md_en, output_dir_en)
    fastlaneChangelog(changelog_md_zh, output_dir_zh)


if __name__ == "__main__":
    main()
