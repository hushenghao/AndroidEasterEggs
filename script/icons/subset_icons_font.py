#!/usr/bin/python
# -*- coding: UTF-8 -*-

import json
import os
import shutil
import sys

import fontTools.subset
import fontTools.merge


FORMAT_OUT_FONT = 'icons_%s.%s'

ICONS_KT = 'Icons.kt'

FORMAT_CLASS_ICONS = """package com.dede.android_eggs.ui

/** Generated automatically via **subset_icons_font.py**, do not modify this file. */
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

MERGED_FONT = 'icons.ttf'

material_fonts = {'filled': 'font/MaterialIcons-Regular.ttf',
                  'rounded': 'font/MaterialIconsRound-Regular.otf',
                  'outlined': 'font/MaterialIconsOutlined-Regular.otf'}

root_path = sys.path[0]
font_dir = os.path.abspath(os.path.join(
    root_path, '../../app/src/main/assets'))
icons_kt_dir = os.path.abspath(os.path.join(
    root_path, '../../app/src/main/java/com/dede/android_eggs/ui'))


def subset_icons_fonts(icons_group):
    subset_fonts = []
    for type in icons_group.keys():
        font = material_fonts[type]
        icons = icons_group[type].values()
        if len(icons) == 0:
            continue
        output = FORMAT_OUT_FONT % (type.lower(), font.split('.')[1])
        unicodes = ','.join(icons_group[type].values())

        # https://fonttools.readthedocs.io/en/latest/subset/index.html
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
        subset_fonts.append(output)
    return subset_fonts


def merge_icons_fonts(fonts):
    options = [
        '--output-file=%s' % MERGED_FONT,
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

    _class = FORMAT_CLASS_ICONS % (''.join(all_icons))
    with open(output, 'w', encoding='utf-8') as f:
        f.write(_class)

def copy(src_file, dts_dir):
    path, name = os.path.split(src_file)
    shutil.copyfile(src_file, os.path.join(dts_dir, name))

icons_group = {}
with open('unicodes.json', 'r', encoding='utf-8') as f:
    icons_group = json.loads(f.read())

subset_fonts = subset_icons_fonts(icons_group)
merge_icons_fonts(subset_fonts)

generate_icons_kt(icons_group, ICONS_KT)

copy(MERGED_FONT, font_dir)
copy(ICONS_KT, icons_kt_dir)
