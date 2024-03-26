# Subset Icons font

FontTools Subset [Material Design Icons](https://fonts.google.com/icons) font.

## Material Icons

<https://github.com/google/material-design-icons#material-icons>

* font/MaterialIcons-Regular.ttf
* font/MaterialIcons-Regular.codepoints
* font/MaterialIconsRound-Regular.otf
* font/MaterialIconsRound-Regular.codepoints
* font/MaterialIconsOutlined-Regular.otf
* font/MaterialIconsOutlined-Regular.codepoints

Git hash: **6745d95590b1a5593888b6c402401fc3db75fbdb**

## Usage

### Install requirements

```shell
pip install -r requirements.txt
```

### Run

```shell
python subset_icons_font.py
```

### Subset

```shell
pyftsubset "MaterialIconsOutlined-Regular.otf" \
  --unicodes-file="unicodes.txt" \
  --output-file="output_outlined.otf" \
  --drop-tables=meta \
  --ignore-missing-unicodes \
  --desubroutinize \
  --recalc-timestamp \
  --with-zopfli \
  --no-hinting \
  --verbose
```

### Merge

```shell
pyftmerge "icons_rounded.otf" "icons_outlined.otf" \
  --output-file="icons.ttf" \
  --verbose
```

## About

* [fontTools.subset](https://fonttools.readthedocs.io/en/latest/subset/index.html)
* [fontTools.megre](https://fonttools.readthedocs.io/en/latest/merge.html)

----

## Vector path data converter

Converts vector path data into **Compose** `materialIcon` block.

> [Compose Icon Generation](https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/material/material/icons/)

### Run converter

```shell
python icon_pathdata_convert.py "pathData"
```

### Example

Run command line:

```shell
python icon_pathdata_convert.py "M8,0c4.42,0 8,3.58 8,8a8.013,8.013 0,0 1,-5.45 7.59c-0.4,0.08 -0.55,-0.17 -0.55,-0.38 0,-0.27 0.01,-1.13 0.01,-2.2 0,-0.75 -0.25,-1.23 -0.54,-1.48 1.78,-0.2 3.65,-0.88 3.65,-3.95 0,-0.88 -0.31,-1.59 -0.82,-2.15 0.08,-0.2 0.36,-1.02 -0.08,-2.12 0,0 -0.67,-0.22 -2.2,0.82 -0.64,-0.18 -1.32,-0.27 -2,-0.27 -0.68,0 -1.36,0.09 -2,0.27 -1.53,-1.03 -2.2,-0.82 -2.2,-0.82 -0.44,1.1 -0.16,1.92 -0.08,2.12 -0.51,0.56 -0.82,1.28 -0.82,2.15 0,3.06 1.86,3.75 3.64,3.95 -0.23,0.2 -0.44,0.55 -0.51,1.07 -0.46,0.21 -1.61,0.55 -2.33,-0.66 -0.15,-0.24 -0.6,-0.83 -1.23,-0.82 -0.67,0.01 -0.27,0.38 0.01,0.53 0.34,0.19 0.73,0.9 0.82,1.13 0.16,0.45 0.68,1.31 2.69,0.94 0,0.67 0.01,1.3 0.01,1.49 0,0.21 -0.15,0.45 -0.55,0.38A7.995,7.995 0,0 1,0 8c0,-4.42 3.58,-8 8,-8Z"
```

Output content:

```txt
moveTo(8f, 0f)
curveToRelative(4.42f, 0f, 8f, 3.58f, 8f, 8f)
arcToRelative(8.013f, 8.013f, 0f, false, true, -5.45f, 7.59f)
curveToRelative(-0.4f, 0.08f, -0.55f, -0.17f, -0.55f, -0.38f)
curveToRelative(0f, -0.27f, 0.01f, -1.13f, 0.01f, -2.2f)
curveToRelative(0f, -0.75f, -0.25f, -1.23f, -0.54f, -1.48f)
curveToRelative(1.78f, -0.2f, 3.65f, -0.88f, 3.65f, -3.95f)
curveToRelative(0f, -0.88f, -0.31f, -1.59f, -0.82f, -2.15f)
curveToRelative(0.08f, -0.2f, 0.36f, -1.02f, -0.08f, -2.12f)
curveToRelative(0f, 0f, -0.67f, -0.22f, -2.2f, 0.82f)
curveToRelative(-0.64f, -0.18f, -1.32f, -0.27f, -2f, -0.27f)
curveToRelative(-0.68f, 0f, -1.36f, 0.09f, -2f, 0.27f)
curveToRelative(-1.53f, -1.03f, -2.2f, -0.82f, -2.2f, -0.82f)
curveToRelative(-0.44f, 1.1f, -0.16f, 1.92f, -0.08f, 2.12f)
curveToRelative(-0.51f, 0.56f, -0.82f, 1.28f, -0.82f, 2.15f)
curveToRelative(0f, 3.06f, 1.86f, 3.75f, 3.64f, 3.95f)
curveToRelative(-0.23f, 0.2f, -0.44f, 0.55f, -0.51f, 1.07f)
curveToRelative(-0.46f, 0.21f, -1.61f, 0.55f, -2.33f, -0.66f)
curveToRelative(-0.15f, -0.24f, -0.6f, -0.83f, -1.23f, -0.82f)
curveToRelative(-0.67f, 0.01f, -0.27f, 0.38f, 0.01f, 0.53f)
curveToRelative(0.34f, 0.19f, 0.73f, 0.9f, 0.82f, 1.13f)
curveToRelative(0.16f, 0.45f, 0.68f, 1.31f, 2.69f, 0.94f)
curveToRelative(0f, 0.67f, 0.01f, 1.3f, 0.01f, 1.49f)
curveToRelative(0f, 0.21f, -0.15f, 0.45f, -0.55f, 0.38f)
arcTo(7.995f, 7.995f, 0f, false, true, 0f, 8f)
curveToRelative(0f, -4.42f, 3.58f, -8f, 8f, -8f)
close()
```

Copy the output to the `materialPath` code block

```kotlin
private var _github: ImageVector? = null

val Icons.Github: ImageVector
    get() {
        if (_github == null) {
            _github = materialIcon(
                "Github",
                defaultHeight = 24.dp,
                defaultWidth = 24.dp,
                viewportHeight = 16f,
                viewportWidth = 16f,
            ) {
                group(
                    scaleX = 0.92f,
                    scaleY = 0.92f,
                    pivotX = 8f,
                    pivotY = 8f
                ) {
                    materialPath {
                        moveTo(8f, 0f)
                        curveToRelative(4.42f, 0f, 8f, 3.58f, 8f, 8f)
                        arcToRelative(8.013f, 8.013f, 0f, false, true, -5.45f, 7.59f)
                        curveToRelative(-0.4f, 0.08f, -0.55f, -0.17f, -0.55f, -0.38f)
                        curveToRelative(0f, -0.27f, 0.01f, -1.13f, 0.01f, -2.2f)
                        curveToRelative(0f, -0.75f, -0.25f, -1.23f, -0.54f, -1.48f)
                        curveToRelative(1.78f, -0.2f, 3.65f, -0.88f, 3.65f, -3.95f)
                        curveToRelative(0f, -0.88f, -0.31f, -1.59f, -0.82f, -2.15f)
                        curveToRelative(0.08f, -0.2f, 0.36f, -1.02f, -0.08f, -2.12f)
                        curveToRelative(0f, 0f, -0.67f, -0.22f, -2.2f, 0.82f)
                        curveToRelative(-0.64f, -0.18f, -1.32f, -0.27f, -2f, -0.27f)
                        curveToRelative(-0.68f, 0f, -1.36f, 0.09f, -2f, 0.27f)
                        curveToRelative(-1.53f, -1.03f, -2.2f, -0.82f, -2.2f, -0.82f)
                        curveToRelative(-0.44f, 1.1f, -0.16f, 1.92f, -0.08f, 2.12f)
                        curveToRelative(-0.51f, 0.56f, -0.82f, 1.28f, -0.82f, 2.15f)
                        curveToRelative(0f, 3.06f, 1.86f, 3.75f, 3.64f, 3.95f)
                        curveToRelative(-0.23f, 0.2f, -0.44f, 0.55f, -0.51f, 1.07f)
                        curveToRelative(-0.46f, 0.21f, -1.61f, 0.55f, -2.33f, -0.66f)
                        curveToRelative(-0.15f, -0.24f, -0.6f, -0.83f, -1.23f, -0.82f)
                        curveToRelative(-0.67f, 0.01f, -0.27f, 0.38f, 0.01f, 0.53f)
                        curveToRelative(0.34f, 0.19f, 0.73f, 0.9f, 0.82f, 1.13f)
                        curveToRelative(0.16f, 0.45f, 0.68f, 1.31f, 2.69f, 0.94f)
                        curveToRelative(0f, 0.67f, 0.01f, 1.3f, 0.01f, 1.49f)
                        curveToRelative(0f, 0.21f, -0.15f, 0.45f, -0.55f, 0.38f)
                        arcTo(7.995f, 7.995f, 0f, false, true, 0f, 8f)
                        curveToRelative(0f, -4.42f, 3.58f, -8f, 8f, -8f)
                        close()
                    }
                }
            }
        }
        return _github!!
    }
```
