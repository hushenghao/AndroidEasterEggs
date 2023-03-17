#!/usr/bin/python
# -*- coding: UTF-8 -*-
import blurhash

with open("images.txt", "r", encoding='utf-8') as f:
    paths = f.readlines()

for path in paths:
    path = path.strip()
    if len(path) == 0 or path.startswith("#"):
        continue
    print(path)
    hash = blurhash.encode(path, x_components=4, y_components=3)
    print(hash)