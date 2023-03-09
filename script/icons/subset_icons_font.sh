#!/usr/bin/env sh

ROOT=$(cd $(dirname "$0") || exit; pwd)
GIT_ROOT="$(git rev-parse --show-toplevel)"

TARGET_DIR="$GIT_ROOT/app/src/main/assets"
OUTPUT_FILE="$ROOT/icons.otf"

# Install requirements
pip3 install -t "$ROOT/Library" -r "$ROOT/requirements.txt"

# https://fonttools.readthedocs.io/en/latest/subset/index.html
pyftsubset "$ROOT/MaterialIconsOutlined-Regular.otf" \
  --unicodes-file="$ROOT/unicodes.txt" \
  --output-file="$OUTPUT_FILE" \
  --drop-tables=meta \
  --ignore-missing-unicodes \
  --desubroutinize \
  --recalc-timestamp \
  --with-zopfli \
  --no-hinting \
  --verbose

cp "$OUTPUT_FILE" "$TARGET_DIR"

python "$ROOT/generate_icons_kt.py"

cp "$ROOT/Icons.kt" "$GIT_ROOT/app/src/main/java/com/dede/android_eggs/ui/"

exit 0