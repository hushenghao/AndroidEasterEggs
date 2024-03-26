#!/usr/bin/python
# -*- coding: UTF-8 -*-

import re
import sys

pathdata = sys.argv[1].strip()

if len(pathdata) == 0:
    print("PathData is empty!")
    exit()

# todo Add more cmd

cmd_close = "close()"
cmd_move = "moveTo"
cmd_move_r = "moveToRelative"
cmd_line = "lineTo"
cmd_line_r = "lineToRelative"
cmd_arc = "arcTo"
cmd_arc_r = "arcToRelative"
cmd_curve = "curveTo"
cmd_curve_r = "curveToRelative"
cmd_format = "{}({})"

cmds = ["m", "M", "l", "L", "z", "Z", "a", "A", "c", "C"]

float_re = r"[, ]"


def float_parse(floats: str) -> list[float]:
    if floats[0] == "z" or floats[0] == "Z":
        return []
    arr = re.split(float_re, floats[1:])
    arr = filter(lambda s: len(s.strip()) > 0, arr)
    arr = map(lambda s: float(s), arr)
    return list(arr)


def float_groups(floats: list, group: int) -> list[list]:
    groups = []
    for i in range(0, len(floats), group):
        b = floats[i:i + group]
        groups.append(b)
    return groups


def float_map(cmd: str, floats: list) -> list[str]:
    for i in range(0, len(floats)):
        if (cmd == "a" or cmd == "A") and (i == 3 or i == 4):
            floats[i] = "true" if floats[i] == "1" else "false"
        else:
            floats[i] = str(floats[i]) + "f"
    return floats


def cmd_map(c: str, floats: list) -> list[str]:
    cmd = ""
    cmd_float_count = -1
    is_a_cmd = False
    if c == "M":
        cmd = cmd_move
        cmd_float_count = 2
    elif c == "m":
        cmd = cmd_move_r
        cmd_float_count = 2
    elif c == "L":
        cmd = cmd_line
        cmd_float_count = 2
    elif c == "l":
        cmd = cmd_line_r
        cmd_float_count = 2
    elif c == "A":
        cmd = cmd_arc
        cmd_float_count = 7
        is_a_cmd = True
    elif c == "a":
        cmd = cmd_arc_r
        cmd_float_count = 7
        is_a_cmd = True
    elif c == "C":
        cmd = cmd_curve
        cmd_float_count = 6
    elif c == "c":
        cmd = cmd_curve_r
        cmd_float_count = 6
    elif c == "z" or c == "Z":
        return cmd_close
    else:
        print("Unsupported cmd: {}".format(c))

    if (cmd_float_count > 0 and cmd_float_count < len(floats)):
        groups = float_groups(floats, cmd_float_count)
        for g in groups:
            float_map(c, g)
        return list(map(lambda g: cmd_format.format(cmd, ", ".join(g)),
                        groups))

    float_map(c, floats)
    return [cmd_format.format(cmd, ", ".join(floats))]


def next_cmd_start(s: str, offset: int) -> int:
    while (offset < len(s)):
        c = s[offset]
        if c in cmds:
            return offset
        offset = offset + 1
    return offset


def pathdata_parse(pathdata: str) -> list:
    pathNode = []
    start = 0
    end = 1
    while (end < len(pathdata)):
        end = next_cmd_start(pathdata, end)
        s = pathdata[start:end].strip()
        if (len(s) > 0):
            floats = float_parse(s)
            pathNode.extend(cmd_map(s[0], floats))
        start = end
        end = end + 1
    if end - start == 1 and start < len(pathdata):
        pathNode.append(cmd_close)
    return pathNode


if __name__ == '__main__':
    pathNode = pathdata_parse(pathdata)
    for cmd in pathNode:
        print(cmd)
