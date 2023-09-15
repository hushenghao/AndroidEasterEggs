#!/usr/bin/python
# -*- coding: UTF-8 -*-

import os
import requests
import sys


def download(git_commit):
    font_dir = os.path.join(sys.path[0], "font")
    version_file = os.path.join(font_dir, "version")

    if os.path.exists(version_file):
        old_ver = open(version_file, "r", encoding="utf-8").readline()
        if old_ver == git_commit:
            print("Find cache files, version: {}".format(git_commit))
            return

    print("Not found cache files, version: {}".format(git_commit))

    download_files = [
        "MaterialIcons-Regular.ttf",
        "MaterialIcons-Regular.codepoints",

        "MaterialIconsOutlined-Regular.otf",
        "MaterialIconsOutlined-Regular.codepoints",

        "MaterialIconsRound-Regular.otf",
        "MaterialIconsRound-Regular.codepoints",
    ]

    # https://github.com/google/material-design-icons/raw/master/font/MaterialIcons-Regular.codepoints
    download_url = "https://github.com/google/material-design-icons/raw/{}/font/{}"

    os.makedirs(font_dir, exist_ok=True)
    for name in download_files:
        print("Download: {}".format(name))
        r = requests.get(url=download_url.format(git_commit, name))
        file_path = os.path.join(font_dir, name)
        open(file_path, "wb").write(r.content)

    print("Update version: {}".format(git_commit))
    open(version_file, "w", encoding="utf-8").write(git_commit)


if __name__ == '__main__':
    download("master")
