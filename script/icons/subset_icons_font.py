#!/usr/bin/python
# -*- coding: UTF-8 -*-

import fontTools.merge
import fontTools.subset
import json
import os
import shutil
import sys
import time

from download_material_fonts import download

FORMAT_CLASS_ICONS = """package com.dede.android_eggs.ui

/** Generated automatically via **subset_icons_font.py**, do not modify this file. */
// %d 
object Icons {
%s
}
"""

FORMAT_TYPE_CLASS = """
    object %s {
        %s
    }
"""

FORMAT_PROPERTY = """
        /** %s */
        const val %s = "\\u%s"
"""

git_commit = "6745d95590b1a5593888b6c402401fc3db75fbdb"

material_fonts = {
    'filled': {
        'font': 'font/MaterialIcons-Regular.ttf',
        'codepoints': 'font/MaterialIcons-Regular.codepoints'
    },
    'rounded': {
        'font': 'font/MaterialIconsRound-Regular.otf',
        'codepoints': 'font/MaterialIconsRound-Regular.codepoints'
    },
    'outlined': {
        'font': 'font/MaterialIconsOutlined-Regular.otf',
        'codepoints': 'font/MaterialIconsOutlined-Regular.codepoints'
    }
}

root_path = sys.path[0]
font_dir = os.path.abspath(os.path.join(
    root_path, '../../app/src/main/res/font'))
icons_kt_dir = os.path.abspath(os.path.join(
    root_path, '../../app/src/main/java/com/dede/android_eggs/ui'))


def load_codepoints(codepoints):
    code_points = {}
    lines = []
    with open(codepoints, 'r') as f:
        lines = f.readlines()
    for l in lines:
        l = l.strip()
        if len(l) == 0:
            continue
        arr = l.split(' ')
        code_points[arr[0]] = arr[1]
    return code_points


def subset_icons_font(font, unicodes, output):
    fontTools.subset.main([
        font,
        '--output-file=%s' % output,
        '--unicodes=%s' % unicodes,
        '--drop-tables=meta',
        '--ignore-missing-unicodes',
        '--desubroutinize',
        '--recalc-timestamp',
        '--with-zopfli',
        '--no-hinting',
        '--verbose'
    ])


def merge_icons_fonts(fonts, output):
    options = [
        '--output-file=%s' % output,
        '--verbose'
    ]
    options.extend(fonts)
    fontTools.merge.main(options)


def generate_icons_kt(icons_group, output):
    all_icons = []
    for group in icons_group.items():
        type = group[0]
        icons = group[1].items()
        if len(icons) == 0:
            continue
        icons = sorted(icons, key=lambda icon: icon[0])
        icons = map(lambda icon: FORMAT_PROPERTY %
                                 (icon[0], icon[0], icon[1].upper()), icons)
        all_icons.append(
            FORMAT_TYPE_CLASS % (type.capitalize(), ''.join(icons))
        )

    _class = FORMAT_CLASS_ICONS % (time.time(), ''.join(all_icons))
    with open(output, 'w', encoding='utf-8') as f:
        f.write(_class)


def copy(src_file, dts_dir):
    name = os.path.split(src_file)[1]
    shutil.copyfile(src_file, os.path.join(dts_dir, name))


icons_name_group = {}
with open('unicodes.json', 'r', encoding='utf-8') as f:
    icons_name_group = json.loads(f.read())

# download material source files
download(git_commit)

icons_group = {}
subset_fonts = []
for group in icons_name_group.items():
    type = group[0]
    font_info = material_fonts[type]
    if font_info == None:
        continue
    icons_name = group[1]
    if len(icons_name) == 0:
        continue

    codepoints = load_codepoints(font_info['codepoints'])
    icons_dist = {}
    for name in icons_name:
        icons_dist[name] = codepoints[name]
    if len(icons_dist) == 0:
        continue
    icons_group[type] = icons_dist

    font = font_info['font']
    unicodes = ','.join(icons_dist.values())
    output = 'icons_%s.%s' % (type, font.split('.')[1])
    subset_icons_font(font, unicodes, output)
    subset_fonts.append(output)

if len(subset_fonts) > 0:
    merge_icons_fonts(subset_fonts, 'icons.ttf')
    copy('icons.ttf', font_dir)

if len(icons_group) > 0:
    generate_icons_kt(icons_group, 'Icons.kt')
    copy('Icons.kt', icons_kt_dir)
