import os
import fontTools.subset
import zopfli

output_dir = os.path.join(os.getcwd(), "../", "src", "main", "assets")
if os.path.exists(output_dir) == False:
    os.makedirs(output_dir)
output = os.path.abspath(os.path.join(output_dir, "NotoColorEmojiSubset.ttf"))

# https://fonttools.readthedocs.io/en/latest/subset/index.html
args = [
    "NotoColorEmojiCompat.ttf",
    "--text-file=plain_text.txt",   # The end character of UTF-8 is ['U+FE0F']
    "--output-file=" + output,
    "--drop-tables=meta",  # WARNING: meta NOT subset; don't know how to subset; dropped
    "--verbose",
    "--ignore-missing-unicodes",
    "--desubroutinize",
    "--recalc-timestamp",
#     "--flavor=woff",
    "--with-zopfli",    # Google Zopfli -3~8% size
    "--no-hinting",
    "--no-notdef-glyph",
]
fontTools.subset.main(args)
