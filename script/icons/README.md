# Subset Icons font

FontTools Subset [Material Design Icons](https://fonts.google.com/icons) font

## Material Icons

https://github.com/google/material-design-icons#material-icons
* font/MaterialIconsOutlined-Regular.otf
* font/MaterialIconsOutlined-Regular.codepoints

Git md5: **511eea577b20d2b02ad77477750da1e44c66a52c**

# Usage

## Quick(Linux/MacOS)
```shell
sh ./subset_icons_font.sh
```

## Windows

### Install requirements
```shell
pip install -r requirements.txt
```
### Run script
```shell
pyftsubset MaterialIconsOutlined-Regular.otf --unicodes-file=unicodes.txt --output-file=icons.otf --verbose
```

Output: `icons.otf`

Copy `icons.otf` to `app/src/main/assets/icons.otf`.

# About

[FontTools](https://fonttools.readthedocs.io/en/latest/subset/index.html)
