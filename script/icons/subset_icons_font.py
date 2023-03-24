#!/usr/bin/python
# -*- coding: UTF-8 -*-

import json

import fontTools.subset

UNICODES = 'unicodes.json'

fonts = {'filled': 'MaterialIcons-Regular.ttf',
         'rounded': 'MaterialIconsRound-Regular.otf',
         'outlined': 'MaterialIconsOutlined-Regular.otf'}
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


FONT_DIR="../../app/src/main/assets"
ICONSKT_DIR="../../app/src/main/java/com/dede/android_eggs/ui"

def subset_icons_fonts(iconsGroup):
    for type in iconsGroup.keys():
        font = fonts[type]
        icons = iconsGroup[type].values()
        if len(icons) == 0:
            continue
        output = FORMAT_OUT_FONT % (type.lower(), font.split('.')[1])
        unicodes = ','.join(iconsGroup[type].values())

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


def generate_icons_kt(iconsGroup, output):
    allIcons = []
    for group in iconsGroup.items():
        type = group[0]
        icons = group[1].items()
        if len(icons) == 0:
            continue
        icons = sorted(icons, key=lambda icon: icon[0])
        icons = map(lambda icon: FORMAT_PROPERTY %
                    (icon[0], icon[0], icon[1].upper()), icons)
        allIcons.append(
            FORMAT_TYPE_CLASS % (type.capitalize(), ''.join(icons))
        )

    _class = FORMAT_CLASS_ICONS % (''.join(allIcons))
    with open(output, 'w', encoding='utf-8') as f:
        f.write(_class)


iconsGroup = {}
with open(UNICODES, 'r', encoding='utf-8') as f:
    iconsGroup = json.loads(f.read())

subset_icons_fonts(iconsGroup)

generate_icons_kt(iconsGroup, ICONS_KT)
