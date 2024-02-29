# Subset Icons font

FontTools Subset [Material Design Icons](https://fonts.google.com/icons) font

## Material Icons

https://github.com/google/material-design-icons#material-icons
* font/MaterialIcons-Regular.ttf
* font/MaterialIcons-Regular.codepoints
* font/MaterialIconsRound-Regular.otf
* font/MaterialIconsRound-Regular.codepoints
* font/MaterialIconsOutlined-Regular.otf
* font/MaterialIconsOutlined-Regular.codepoints

Git hash: **6745d95590b1a5593888b6c402401fc3db75fbdb**

# Usage

## Install requirements
```shell
pip install -r requirements.txt
```

## Run
```shell
python subset_icons_font.py
```

## Subset
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

## Merge
```shell
pyftmerge "icons_rounded.otf" "icons_outlined.otf" \
  --output-file="icons.ttf" \
  --verbose
```

# About

[fontTools.subset](https://fonttools.readthedocs.io/en/latest/subset/index.html)

[fontTools.megre](https://fonttools.readthedocs.io/en/latest/merge.html)
