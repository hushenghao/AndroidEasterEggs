#!/usr/bin/python
# -*- coding: UTF-8 -*-
import sys

class_format = """package com.dede.android_eggs.ui

/** Generated automatically via **generate_icons_kt.py**, do not modify this file. */
object Icons {
%s
}
"""
property_format = """
    /** %s */
    const val %s = "\\u%s"
"""

with open("unicodes.txt", "r", encoding='utf-8') as f:
    lines = f.readlines()

""" sample unicodes.txt 
# android
e859
"""
icons = []
name = None
value = None
for line in lines:
    line = line.strip()
    if len(line) == 0:
        continue
    if (line.startswith("#")):
        name = line.replace("#", "").strip()
    elif (name != None):
        value = line
        icons.append({"name": name, "value": value})
        print("parser: %s=%s" % (name, value))
    else:
        print("unknown line: " % line)
        
icons = sorted(icons, key=lambda icon: icon["name"])
icons = map(lambda icon: property_format %
            (icon["name"], icon["name"].upper(), icon["value"].upper()), icons)

_class = class_format % (''.join(icons))
with open("Icons.kt", "w", encoding='utf-8') as f:
    f.write(_class)
