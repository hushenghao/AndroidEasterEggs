#!/usr/bin/env sh

HOME="$(git rev-parse --show-toplevel)"
echo "$HOME"

TARGET_DIR="$HOME/app/src/main/assets"

# Install requirements
pip3 install -r requirements.txt

# https://fonttools.readthedocs.io/en/latest/subset/index.html
pyftsubset MaterialIconsOutlined-Regular.otf \
  --unicodes-file=unicodes.txt \
  --output-file=icons.otf \
  --drop-tables=meta \
  --ignore-missing-unicodes \
  --desubroutinize \
  --recalc-timestamp \
  --with-zopfli \
  --no-hinting \
  --verbose

cp icons.otf "$TARGET_DIR"

exit 0