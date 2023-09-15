#!/usr/bin/python
# -*- coding: UTF-8 -*-
import blurhash
import os

with open("images.txt", "r", encoding='utf-8') as f:
    paths = f.readlines()

xml_format = "<string name=\"{}\" translatable=\"false\">{}</string>\n"

for path in paths:
    path = path.strip()
    if len(path) == 0 or path.startswith("#"):
        continue
    print(path)
    hash = blurhash.encode(path, x_components=4, y_components=3)
    name = os.path.split(path)[1].split(".")[0]
    print(xml_format.format(name, hash))
