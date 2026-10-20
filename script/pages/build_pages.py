#!/usr/bin/env python3
"""Build the MkDocs site source from the wiki submodule for GitHub Pages.

The wiki submodule stays read-only and keeps serving GitHub wiki. This script
copies the published pages into pages-docs/ with site-specific adjustments:

- English pages at the root, zh-CN pages under zh/.
- The Home pages lose the centered badge block (<div align="center">...</div>).
- Absolute GitHub wiki URLs are rewritten to relative .md links so MkDocs
  resolves them against use_directory_urls. Wiki pages that are not published
  here (e.g. the retired 隐私协议 placeholder) keep their GitHub URL.
"""

import posixpath
import re
import shutil
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
WIKI_DIR = REPO_ROOT / "wiki"
DOCS_DIR = Path(__file__).resolve().parent / "pages-docs"

WIKI_URL_PREFIX = "https://github.com/hushenghao/AndroidEasterEggs/wiki/"

# Wiki pages published to the site; Home becomes the language's index page.
PAGES = [
    "Home",
    "Android-versions",
    "The-application-failed-to-uninstall",
    "Sponsor",
    "Privacy-policy",
]

# One entry per published language: wiki locale ("" = English, pages at the
# wiki root) -> docs URL prefix ("" = English, pages at the docs root).
# Adding a language means adding it here (plus the five translated pages in
# the wiki), an extra.alternate entry and a not_in_nav pattern in mkdocs.yml.
LANGUAGES = {
    "": "",
    "zh-CN": "zh",
}


def build_language_maps():
    """Derive the wiki-to-docs source/target maps from PAGES and LANGUAGES."""
    sources = []
    targets = {}
    for locale, prefix in LANGUAGES.items():
        for page in PAGES:
            suffix = f"-{locale}" if locale else ""
            slug = "index.md" if page == "Home" else f"{page.lower()}.md"
            target = posixpath.join(prefix, slug) if prefix else slug
            source = (
                f"locale/{locale}/{page}{suffix}.md" if locale else f"{page}.md"
            )
            sources.append((source, target))
            targets[f"{page}{suffix}".lower()] = target
    return sources, targets


SOURCES, PAGE_TARGETS = build_language_maps()

BADGE_BLOCK = re.compile(r'<div align="center">.*?</div>\s*', re.DOTALL)

# Material renders the theme toggle before the language switcher; flex order
# swaps them visually (search first, then language, then theme, then repo).
SITE_CSS = """\
.md-header__inner > label[for="__search"],
.md-header__inner > .md-search {
    order: 1;
}

.md-header__inner > div.md-header__option {
    order: 2;
}

.md-header__inner > form.md-header__option {
    order: 3;
}

.md-header__inner > .md-header__source {
    order: 4;
}
"""

# Rewrite the header language switcher to the current page's counterpart
# instead of the other language's home page. The canonical URL always
# carries the deployed path of the current page, so this works under any
# hosting root; local previews link to the deployed counterpart page.
# Language roots come from the switcher itself, so new languages need no
# changes here (longest root prefix wins).
SITE_JS = """\
function updateLanguageLinks() {
    var links = document.querySelectorAll(".md-select__link[hreflang]");
    var byLang = {};
    links.forEach(function (link) {
        byLang[link.getAttribute("hreflang")] = link.getAttribute("href");
    });
    var langs = Object.keys(byLang);
    var canonical = document.querySelector('link[rel="canonical"]');
    if (!langs.length || !canonical) {
        return;
    }
    var path = new URL(canonical.href).pathname;
    langs.sort(function (a, b) {
        return byLang[b].length - byLang[a].length;
    });
    var current = null;
    for (var i = 0; i < langs.length; i++) {
        if (path.indexOf(byLang[langs[i]]) === 0) {
            current = langs[i];
            break;
        }
    }
    if (!current) {
        return;
    }
    var slug = path.slice(byLang[current].length);
    links.forEach(function (link) {
        var base = byLang[link.getAttribute("hreflang")];
        link.setAttribute("href", base + slug);
    });
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", updateLanguageLinks);
} else {
    updateLanguageLinks();
}
"""


def strip_badges(text: str) -> str:
    return BADGE_BLOCK.sub("", text, count=1)


def rewrite_links(text: str, source_dir: str) -> str:
    def replace(match: re.Match) -> str:
        target = PAGE_TARGETS.get(match.group(1).lower())
        if target is None:
            return match.group(0)
        return posixpath.relpath(target, source_dir or ".")

    pattern = re.escape(WIKI_URL_PREFIX) + r"([A-Za-z0-9\-_%]+)"
    return re.sub(pattern, replace, text)


def main() -> None:
    if not (WIKI_DIR / "Home.md").is_file():
        sys.exit("wiki submodule is not checked out; run: git submodule update --init")
    if DOCS_DIR.exists():
        shutil.rmtree(DOCS_DIR)
    for source, target in SOURCES:
        source_path = WIKI_DIR / source
        if not source_path.is_file():
            sys.exit(f"missing wiki page: {source}")
        target_path = DOCS_DIR / target
        target_path.parent.mkdir(parents=True, exist_ok=True)
        text = source_path.read_text(encoding="utf-8")
        if target_path.name == "index.md":
            text = strip_badges(text)
        text = rewrite_links(text, posixpath.dirname(target))
        target_path.write_text(text, encoding="utf-8")
        print(f"  {source} -> {target}")
    assets_dir = DOCS_DIR / "assets"
    assets_dir.mkdir(parents=True, exist_ok=True)
    (assets_dir / "site.css").write_text(SITE_CSS, encoding="utf-8")
    (assets_dir / "site.js").write_text(SITE_JS, encoding="utf-8")
    print("  <site css/js> -> assets/site.css, assets/site.js")


if __name__ == "__main__":
    main()
