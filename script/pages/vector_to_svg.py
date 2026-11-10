#!/usr/bin/env python3
"""Convert Android drawables to SVG for the docs site.

Markdown cannot render Android vector drawables, so the version logos used by
the wiki tables are exported as SVG. Supported shapes:

    <vector>        plain VectorDrawable (transparent background)
    <adaptive-icon> Android 8+ adaptive icon, composited from its background
                    and foreground layers and clipped to the launcher mask

References (`@drawable/...`, `@color/...`) are resolved against the `res`
directory the source file lives in. System accent colors fall back to the
static values from `:core:system-colors`. Vector paths may use `aapt:attr`
gradient fills.

Usage:
    python3 script/pages/vector_to_svg.py SOURCE_XML OUTPUT_SVG [...]
"""

from __future__ import annotations

import argparse
import itertools
import xml.etree.ElementTree as ET
from pathlib import Path

ANDROID_NS = "http://schemas.android.com/apk/res/android"
AAPT_NS = "http://schemas.android.com/aapt"

# Adaptive icon canvas: 108dp with a 72dp circular launcher mask.
CANVAS = 108.0
MASK_RADIUS = 36.0

# Every exported logo is normalized to this square canvas.
TARGET_SIZE = 72.0

# Static fallbacks for the dynamic color aliases in :core:system-colors.
SYSTEM_COLORS = {
    "system_accent1_400": "#219BCC",
    "system_accent3_500": "#787296",
    "system_accent3_800": "#322C4C",
    "system_neutral1_100": "#E1E3E5",
}


def _android(element: ET.Element, name: str) -> str | None:
    return element.get(f"{{{ANDROID_NS}}}{name}")


def _float(value: str | None, default: float = 0.0) -> float:
    if value is None:
        return default
    return float(value.removesuffix("dp").removesuffix("dip").removesuffix("px"))


def _format_float(value: float) -> str:
    text = f"{value:.4f}".rstrip("0").rstrip(".")
    return text or "0"


def _parse_color(value: str | None) -> tuple[str, float] | None:
    """Return (rgb, alpha) or None for fully transparent colors."""
    if value is None:
        return None
    value = value.strip()
    if value in ("transparent", "@android:color/transparent"):
        return None
    if not value.startswith("#"):
        raise ValueError(f"unsupported color value: {value}")

    digits = value[1:]
    if len(digits) in (3, 4):
        digits = "".join(ch * 2 for ch in digits)
    if len(digits) == 6:
        return f"#{digits}", 1.0
    if len(digits) == 8:
        return f"#{digits[2:]}", int(digits[:2], 16) / 255.0
    raise ValueError(f"unsupported color value: {value}")


class Resolver:
    """Resolve @drawable/@color references inside one module's res directory."""

    def __init__(self, res_dir: Path):
        self.res_dir = res_dir
        self.colors = dict(SYSTEM_COLORS)
        values_dir = res_dir / "values"
        if values_dir.is_dir():
            for xml in sorted(values_dir.glob("*.xml")):
                try:
                    root = ET.parse(xml).getroot()
                except ET.ParseError:
                    continue
                for color in root.iter("color"):
                    name = color.get("name")
                    text = (color.text or "").strip()
                    if name and text:
                        self.colors[name] = text

    def color(self, value: str | None, depth: int = 0) -> str | None:
        if value is None:
            return None
        value = value.strip()
        for prefix in ("@color/", "@android:color/"):
            if value.startswith(prefix):
                name = value[len(prefix) :]
                if name not in self.colors:
                    raise ValueError(f"unknown color: {value}")
                resolved = self.colors[name]
                if resolved.startswith("@"):
                    if depth > 10:
                        raise ValueError(f"color reference loop: {value}")
                    return self.color(resolved, depth + 1)
                return resolved
        return value

    def drawable(self, name: str) -> Path:
        candidates = []
        for path in self.res_dir.glob(f"drawable*/{name}.xml"):
            parent = path.parent.name
            # Prefer the API 26 adaptive icon over the legacy layer list.
            priority = 0 if "v26" in parent else 1 if "anydpi" in parent else 2
            candidates.append((priority, path.name, path))
        if not candidates:
            raise FileNotFoundError(f"drawable not found: {name}")
        return sorted(candidates)[0][2]


def _group_transform(group: ET.Element) -> str:
    translate_x = _float(_android(group, "translateX"))
    translate_y = _float(_android(group, "translateY"))
    pivot_x = _float(_android(group, "pivotX"))
    pivot_y = _float(_android(group, "pivotY"))
    scale_x = _float(_android(group, "scaleX"), 1.0)
    scale_y = _float(_android(group, "scaleY"), 1.0)
    rotation = _float(_android(group, "rotation"))

    parts: list[str] = []
    if translate_x or translate_y:
        parts.append(
            f"translate({_format_float(translate_x)},{_format_float(translate_y)})"
        )
    if rotation:
        parts.append(
            f"rotate({_format_float(rotation)},{_format_float(pivot_x)},"
            f"{_format_float(pivot_y)})"
        )
    if scale_x != 1.0 or scale_y != 1.0:
        parts.append(
            f"translate({_format_float(pivot_x)},{_format_float(pivot_y)}) "
            f"scale({_format_float(scale_x)},{_format_float(scale_y)}) "
            f"translate({_format_float(-pivot_x)},{_format_float(-pivot_y)})"
        )
    return " ".join(parts)


def _aapt_fill_gradient(path: ET.Element) -> ET.Element | None:
    for child in path:
        tag = child.tag.rsplit("}", 1)[-1]
        if tag == "attr" and _android(child, "name") == "android:fillColor":
            return child.find("gradient")
    return None


def _render_gradient(
    gradient: ET.Element, resolver: Resolver, defs: list[str], ids: itertools.count
) -> str:
    gradient_id = f"grad{next(ids)}"
    stops = []
    for item in gradient.findall("item"):
        color = _parse_color(resolver.color(_android(item, "color")))
        if color is None:
            continue
        rgb, alpha = color
        opacity = (
            "" if alpha >= 1.0 else f' stop-opacity="{_format_float(alpha)}"'
        )
        stops.append(
            f'<stop offset="{_format_float(_float(_android(item, "offset")))}" '
            f'stop-color="{rgb}"{opacity} />'
        )
    body = "".join(stops)

    if _android(gradient, "type") == "radial":
        defs.append(
            f'<radialGradient id="{gradient_id}" gradientUnits="userSpaceOnUse" '
            f'cx="{_android(gradient, "centerX")}" '
            f'cy="{_android(gradient, "centerY")}" '
            f'r="{_android(gradient, "gradientRadius")}">{body}</radialGradient>'
        )
    else:
        defs.append(
            f'<linearGradient id="{gradient_id}" gradientUnits="userSpaceOnUse" '
            f'x1="{_android(gradient, "startX")}" '
            f'y1="{_android(gradient, "startY")}" '
            f'x2="{_android(gradient, "endX")}" '
            f'y2="{_android(gradient, "endY")}">{body}</linearGradient>'
        )
    return gradient_id


def _path_attributes(
    path: ET.Element, resolver: Resolver, defs: list[str], ids: itertools.count
) -> dict[str, str]:
    attrs: dict[str, str] = {}
    data = _android(path, "pathData")
    if not data:
        return attrs
    attrs["d"] = " ".join(data.split())

    gradient = _aapt_fill_gradient(path)
    if gradient is not None:
        attrs["fill"] = f"url(#{_render_gradient(gradient, resolver, defs, ids)})"
    else:
        fill = _parse_color(resolver.color(_android(path, "fillColor")))
        if fill is None or fill[1] == 0.0:
            attrs["fill"] = "none"
        else:
            color, color_alpha = fill
            attrs["fill"] = color
            fill_alpha = _float(_android(path, "fillAlpha"), 1.0) * color_alpha
            if fill_alpha < 1.0:
                attrs["fill-opacity"] = _format_float(fill_alpha)
            if _android(path, "fillType") == "evenOdd":
                attrs["fill-rule"] = "evenodd"

    stroke = _parse_color(resolver.color(_android(path, "strokeColor")))
    if stroke is not None and stroke[1] > 0.0:
        color, color_alpha = stroke
        attrs["stroke"] = color
        stroke_alpha = _float(_android(path, "strokeAlpha"), 1.0) * color_alpha
        if stroke_alpha < 1.0:
            attrs["stroke-opacity"] = _format_float(stroke_alpha)
        attrs["stroke-width"] = _format_float(
            _float(_android(path, "strokeWidth"), 0.0)
        )
        cap = _android(path, "strokeLineCap")
        if cap:
            attrs["stroke-linecap"] = cap
        join = _android(path, "strokeLineJoin")
        if join:
            attrs["stroke-linejoin"] = join

    return attrs


def _render_path(
    path: ET.Element, resolver: Resolver, defs: list[str], ids: itertools.count
) -> list[str]:
    attrs = _path_attributes(path, resolver, defs, ids)
    if not attrs:
        return []
    rendered = " ".join(f'{key}="{value}"' for key, value in attrs.items())
    return [f"<path {rendered} />"]


def _render_group(
    group: ET.Element, resolver: Resolver, defs: list[str], ids: itertools.count
) -> list[str]:
    body: list[str] = []
    clip_attr = ""
    for child in group:
        tag = child.tag.rsplit("}", 1)[-1]
        if tag == "clip-path":
            data = _android(child, "pathData")
            if data:
                clip_id = f"clip{next(ids)}"
                defs.append(
                    f'<clipPath id="{clip_id}">'
                    f'<path d="{" ".join(data.split())}" /></clipPath>'
                )
                clip_attr = f' clip-path="url(#{clip_id})"'
        elif tag == "path":
            body.extend(_render_path(child, resolver, defs, ids))
        elif tag == "group":
            body.extend(_render_group(child, resolver, defs, ids))
        else:
            raise ValueError(f"unsupported element: {tag}")

    if not body:
        return []
    open_tag = f"<g{clip_attr}"
    transform = _group_transform(group)
    if transform:
        open_tag += f' transform="{transform}"'
    return [f"{open_tag}>", *body, "</g>"]


def _render_vector(
    root: ET.Element, resolver: Resolver, defs: list[str], ids: itertools.count
) -> list[str]:
    body: list[str] = []
    for child in root:
        tag = child.tag.rsplit("}", 1)[-1]
        if tag == "path":
            body.extend(_render_path(child, resolver, defs, ids))
        elif tag == "group":
            body.extend(_render_group(child, resolver, defs, ids))
        else:
            raise ValueError(f"unsupported element: {tag}")
    return body


def _scale_to_canvas(
    root: ET.Element,
    resolver: Resolver,
    defs: list[str],
    ids: itertools.count,
    canvas: float,
) -> list[str]:
    viewport_width = _float(_android(root, "viewportWidth"))
    viewport_height = _float(_android(root, "viewportHeight"))
    scale_x = canvas / viewport_width
    scale_y = canvas / viewport_height
    body = _render_vector(root, resolver, defs, ids)
    return [
        f'<g transform="scale({_format_float(scale_x)},'
        f'{_format_float(scale_y)})">',
        *body,
        "</g>",
    ]


def _gradient_direction(angle: float) -> tuple[str, str, str, str]:
    # GradientDrawable angle: 0 left-to-right, 90 bottom-to-top,
    # 270/-90 top-to-bottom, 180 right-to-left.
    if angle in (90, -270):
        return "0", "1", "0", "0"
    if angle in (270, -90):
        return "0", "0", "0", "1"
    if angle in (180, -180):
        return "1", "0", "0", "0"
    return "0", "0", "1", "0"


def _render_shape(
    root: ET.Element, resolver: Resolver, defs: list[str], ids: itertools.count
) -> list[str]:
    gradient = root.find("gradient")
    if gradient is None:
        raise ValueError("unsupported <shape> without a gradient")
    start = _parse_color(resolver.color(_android(gradient, "startColor")))
    end = _parse_color(resolver.color(_android(gradient, "endColor")))
    if start is None or end is None:
        raise ValueError("gradient colors must be opaque")
    x1, y1, x2, y2 = _gradient_direction(_float(_android(gradient, "angle")))
    gradient_id = f"grad{next(ids)}"
    stops = []
    for offset, color in ((0.0, start), (1.0, end)):
        rgb, alpha = color
        opacity = (
            "" if alpha >= 1.0 else f' stop-opacity="{_format_float(alpha)}"'
        )
        stops.append(
            f'<stop offset="{_format_float(offset)}" stop-color="{rgb}"{opacity} />'
        )
    defs.append(
        f'<linearGradient id="{gradient_id}" '
        f'x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}">'
        + "".join(stops)
        + "</linearGradient>"
    )
    return [
        f'<rect width="{_format_float(CANVAS)}" '
        f'height="{_format_float(CANVAS)}" fill="url(#{gradient_id})" />'
    ]


def _render_layer(
    element: ET.Element, resolver: Resolver, defs: list[str], ids: itertools.count
) -> list[str]:
    drawable = _android(element, "drawable")
    if drawable is None:
        return []
    if drawable.startswith("@color/") or drawable.startswith("@android:color/"):
        color = _parse_color(resolver.color(drawable))
        if color is None or color[1] == 0.0:
            return []
        rgb, alpha = color
        opacity = (
            "" if alpha >= 1.0 else f' fill-opacity="{_format_float(alpha)}"'
        )
        return [
            f'<rect width="{_format_float(CANVAS)}" '
            f'height="{_format_float(CANVAS)}" fill="{rgb}"{opacity} />'
        ]
    if drawable.startswith("@drawable/"):
        path = resolver.drawable(drawable[len("@drawable/") :])
        root = ET.parse(path).getroot()
        tag = root.tag.rsplit("}", 1)[-1]
        if tag == "vector":
            return _scale_to_canvas(root, resolver, defs, ids, CANVAS)
        if tag == "shape":
            return _render_shape(root, resolver, defs, ids)
        if tag == "color":
            color = _parse_color(
                resolver.color(root.get(f"{{{ANDROID_NS}}}color"))
            )
            if color is None or color[1] == 0.0:
                return []
            rgb, alpha = color
            opacity = (
                "" if alpha >= 1.0 else f' fill-opacity="{_format_float(alpha)}"'
            )
            return [
                f'<rect width="{_format_float(CANVAS)}" '
                f'height="{_format_float(CANVAS)}" fill="{rgb}"{opacity} />'
            ]
        raise ValueError(f"unsupported layer drawable: {path}")
    raise ValueError(f"unsupported drawable reference: {drawable}")


def _find_res_dir(source: Path) -> Path | None:
    for parent in source.parents:
        if parent.name == "res":
            return parent
    return None


def convert(source: Path, target: Path) -> None:
    res_dir = _find_res_dir(source)
    if res_dir is None:
        raise ValueError(f"{source}: cannot locate the module res directory")
    resolver = Resolver(res_dir)
    ids = itertools.count(1)
    defs: list[str] = []
    root = ET.parse(source).getroot()
    tag = root.tag.rsplit("}", 1)[-1]

    if tag == "adaptive-icon":
        layers = {}
        for child in root:
            child_tag = child.tag.rsplit("}", 1)[-1]
            if child_tag in ("background", "foreground"):
                layers[child_tag] = child
        body: list[str] = []
        # Background must be drawn first; the XML order is not guaranteed
        # (Android 12's adaptive icon lists foreground before background).
        for layer_name in ("background", "foreground"):
            if layer_name in layers:
                body.extend(_render_layer(layers[layer_name], resolver, defs, ids))
        mask_id = f"clip{next(ids)}"
        defs.append(
            f'<clipPath id="{mask_id}">'
            f'<circle cx="54" cy="54" r="{_format_float(MASK_RADIUS)}" />'
            "</clipPath>"
        )
        lines = [
            '<?xml version="1.0" encoding="UTF-8"?>',
            '<svg xmlns="http://www.w3.org/2000/svg" '
            'width="72" height="72" viewBox="18 18 72 72">',
            "<defs>",
            *defs,
            "</defs>",
            f'<g clip-path="url(#{mask_id})">',
            *body,
            "</g>",
            "</svg>",
        ]
    elif tag == "vector":
        viewport_width = _float(_android(root, "viewportWidth"))
        viewport_height = _float(_android(root, "viewportHeight"))
        body = _render_vector(root, resolver, defs, ids)
        # Normalize every logo to the same square canvas so the wiki tables
        # can use one fixed image size regardless of the source viewport.
        scale = min(TARGET_SIZE / viewport_width, TARGET_SIZE / viewport_height)
        offset_x = (TARGET_SIZE - viewport_width * scale) / 2
        offset_y = (TARGET_SIZE - viewport_height * scale) / 2
        lines = [
            '<?xml version="1.0" encoding="UTF-8"?>',
            '<svg xmlns="http://www.w3.org/2000/svg" '
            f'width="{_format_float(TARGET_SIZE)}" '
            f'height="{_format_float(TARGET_SIZE)}" '
            f'viewBox="0 0 {_format_float(TARGET_SIZE)} '
            f'{_format_float(TARGET_SIZE)}">',
        ]
        if defs:
            lines.extend(["<defs>", *defs, "</defs>"])
        lines.append(
            f'<g transform="translate({_format_float(offset_x)},'
            f'{_format_float(offset_y)}) scale({_format_float(scale)})">'
        )
        lines.extend([*body, "</g>", "</svg>"])
    else:
        raise ValueError(f"{source}: unsupported root element: {tag}")

    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"{source} -> {target}")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "pairs",
        nargs="+",
        metavar="SOURCE_XML OUTPUT_SVG",
        help="source drawable and destination SVG path (repeatable)",
    )
    args = parser.parse_args()
    if len(args.pairs) % 2:
        parser.error("arguments must be SOURCE_XML OUTPUT_SVG pairs")
    for index in range(0, len(args.pairs), 2):
        convert(Path(args.pairs[index]), Path(args.pairs[index + 1]))


if __name__ == "__main__":
    main()
