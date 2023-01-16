import os
import fontTools.subset
import zopfli

# https://fonttools.readthedocs.io/en/latest/subset/index.html
args = [
    "MaterialIconsOutlined-Regular.otf",
    "--unicodes-file=unicodes.txt",   # The end character of UTF-8 is ['U+FE0F']
    "--output-file=icons.otf",
    "--drop-tables=meta",  # WARNING: meta NOT subset; don't know how to subset; dropped
    "--verbose",
    "--ignore-missing-unicodes",
    "--desubroutinize",
    "--recalc-timestamp",
#     "--flavor=woff",
    "--with-zopfli",    # Google Zopfli -3~8% size
    "--no-hinting",
#     "--no-notdef-glyph",
]
fontTools.subset.main(args)