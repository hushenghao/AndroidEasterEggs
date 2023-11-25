#!/usr/bin/python
# -*- coding: UTF-8 -*-
import blurhash
import os

with open("images.txt", "r", encoding='utf-8') as f:
    lines = f.readlines()

xml_string_format = "<string name=\"{}\" translatable=\"false\">{}</string>\n"
xml_array_format = "<string-array name=\"{}\" translatable=\"false\">\n{}</string-array>\n"
xml_array_item_format = "<item>{}</item>\n"

def _blurhash(path):
    return blurhash.encode(path, x_components=4, y_components=3)

def string_name(path):
    name = os.path.split(path)[1].split(".")[0]
    if name.startswith("hash"):
        return name
    return "hash_{}".format(name)

def blurhash_dir(dir):
    files = os.listdir(dir)
    print(files)
    items = map(lambda path: _blurhash(os.path.join(dir, path)), files)
    items = map(lambda hash: xml_array_item_format.format(hash), items)
    print(xml_array_format.format(string_name(dir), "".join(items)))

def blurhash_file(path):
    hash = _blurhash(path)
    print(xml_string_format.format(string_name(path), hash))

for line in lines:
    path = line.strip()
    if len(path) == 0 or path.startswith("#") or os.path.exists(path) != True:
        continue
    print(path)
    if os.path.isdir(path):
        blurhash_dir(path)
    else:
        blurhash_file(path)
