#!/usr/bin/env sh

ROOT=$(cd $(dirname "$0") || exit; pwd)
GIT_ROOT="$(git rev-parse --show-toplevel)"

FONT_DIR="$GIT_ROOT/app/src/main/assets"
ICONSKT_DIR="$GIT_ROOT/app/src/main/java/com/dede/android_eggs/ui"

PYTHON_EXE="$ROOT/subset_icons_font.py"

# Install requirements
pip3 install -r "$ROOT/requirements.txt"

# Subset and general Icon.kt
# Detect the platform (similar to $OSTYPE)
if [ "$(uname)" = 'WindowsNT' ]; then
  python "$PYTHON_EXE"
else
  python3 "$PYTHON_EXE"
fi

# Copy product
#cp "$ROOT/icons_outlined.otf" "$FONT_DIR"
#cp "$ROOT/icons_filled.ttf" "$FONT_DIR"
cp "$ROOT/icons_rounded.otf" "$FONT_DIR"
cp "$ROOT/Icons.kt" "$ICONSKT_DIR"

exit 0