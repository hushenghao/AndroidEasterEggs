# Subset Icons font

FontTools Subset [Material Design Icons](https://fonts.google.com/icons) font

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
