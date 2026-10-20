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

import json
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
# Brand colors come from the app icon: dark teal header with the bright
# Android green as accent, in both color schemes.
SITE_CSS = """\
:root > * {
    --md-primary-fg-color: #0f2e2a;
    --md-primary-fg-color--light: #1c4a42;
    --md-primary-fg-color--dark: #0b211e;
    --md-accent-fg-color: #37a65f;
}

/* The color scheme attribute lives on <body>, not <html> */
body[data-md-color-scheme="slate"] {
    --md-primary-fg-color: #12332d;
    --md-accent-fg-color: #4cc96b;
}

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

# Sidebar navigation label translations, injected into assets/site.js.
# Keyed by the LANGUAGES docs prefix; English needs no entry. Adding a
# language means adding its dictionary here (plus LANGUAGES above).
NAV_LABELS = {
    "zh": {
        "Home": "首页",
        "Android Versions": "Android 版本",
        "Failed to Uninstall": "应用卸载失败",
        "Sponsor": "捐赠",
        "Privacy Policy": "隐私协议",
        "Downloads": "下载",
        "Developers": "开发人员",
        "License": "开源协议",
        "Contributing": "参与贡献",
        "Translation": "翻译",
        "Pgyer (beta)": "蒲公英 (beta)",
    },
}


def build_site_js() -> str:
    labels = json.dumps(NAV_LABELS, ensure_ascii=False, indent=4)
    return SITE_JS.replace("/*__NAV_LABELS__*/{}", labels)


# Rewrite the header language switcher to the current page's counterpart
# instead of the other language's home page. The canonical URL always
# carries the deployed path of the current page, so this works under any
# hosting root; local previews link to the deployed counterpart page.
# Language roots come from the switcher itself, so new languages need no
# changes here (longest root prefix wins).
SITE_JS = """\
var NAV_LABELS = /*__NAV_LABELS__*/{};

/* Directory-relative href from the directory of fromDir (a page URL:
   trailing "/" is optional, "index.html" forms are normalized away) to
   an absolute directory path toPath (ending in "/"). Algorithm: drop the
   common prefix, climb back with "..", descend into the remainder. */
function relativeHref(toPath, fromDir) {
    var from = fromDir.replace(/(index\.html?)?$/, "").split("/");
    var to = toPath.split("/");
    var i = 0;
    while (i < from.length - 1 && i < to.length - 1 && from[i] === to[i]) {
        i++;
    }
    var parts = [];
    for (var k = i; k < from.length - 1; k++) {
        parts.push("..");
    }
    for (var j = i; j < to.length - 1; j++) {
        parts.push(to[j]);
    }
    var rel = parts.join("/");
    return rel === "" ? "./" : rel + "/";
}

/* Point the header language switcher and the sidebar navigation at the
   current page's counterparts. The canonical URL provides the page slug;
   the hosting root is derived from the current page URL itself (it is
   <root><langPrefix><slug>, whatever the deployment form: project
   subpath, plain local root or file://). Language prefixes come from the
   switcher, so new languages need no changes here. */
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
    var canonicalPath = new URL(canonical.href).pathname;
    langs.sort(function (a, b) {
        return byLang[b].length - byLang[a].length;
    });
    var current = null;
    for (var i = 0; i < langs.length; i++) {
        if (canonicalPath.indexOf(byLang[langs[i]]) === 0) {
            current = langs[i];
            break;
        }
    }
    if (!current) {
        return;
    }
    var slug = canonicalPath.slice(byLang[current].length);
    var langPrefix = byLang[current].slice(byLang.en.length);
    var tail = "/" + langPrefix + slug;
    var cur = location.pathname.replace(/index\\.html?$/i, "");
    if (cur.slice(cur.length - tail.length) !== tail) {
        return;
    }
    var root = cur.slice(0, cur.length - tail.length);
    var localRoots = {};
    for (var l = 0; l < langs.length; l++) {
        var lang = langs[l];
        var pfx = byLang[langs[l]].slice(byLang.en.length).replace(/\\/$/, "");
        localRoots[langs[l]] = root + (pfx ? "/" + pfx : "");
    }
    function languageDirs(lang) {
        /* root may be an empty string (plain local root = "/"), already
           ending in "/", so only non-empty roots get the separator */
        var languageRoot = localRoots[lang] === "" ? "/" : localRoots[lang] + "/";
        return languageRoot;
    }
    links.forEach(function (link) {
        var target = languageDirs(link.getAttribute("hreflang")) + slug;
        link.setAttribute("href", relativeHref(target, cur));
    });
    localizeNav(current, languageDirs, relativeHref, cur, byLang);
}

/* Localize the primary sidebar navigation for non-English pages: entries
   pointing at English pages get rewritten to the language's counterpart
   (relative href) and their labels translated via NAV_LABELS. External
   links keep their href and only get a translated label. */
function localizeNav(current, languageDirs, relativeHref, fromDir, byLang) {
    var labels = NAV_LABELS[current];
    if (!labels) {
        return;
    }
    var enDir = languageDirs("en");
    var enCanonicalRoot = byLang.en;
    var ownDir = languageDirs(current);
    function entrySlug(href) {
        if (!href) {
            return null;
        }
        var resolved = new URL(href, location.href);
        if (resolved.origin !== location.origin) {
            return null;
        }
        var p = resolved.pathname;
        for (var lang in byLang) {
            if (lang !== "en" && p.indexOf(byLang[lang]) === 0) {
                return null;
            }
        }
        /* Prefer the local hosting root: tree links resolve against the
           page URL, so under most hosting forms the target carries the
           local root prefix. The deployed root is only a fallback for
           mismatched forms. */
        if (p === enDir) {
            return "";
        }
        if (p.indexOf(enDir) === 0) {
            return p.slice(enDir.length);
        }
        if (enCanonicalRoot && p.indexOf(enCanonicalRoot) === 0) {
            return p.slice(enCanonicalRoot.length);
        }
        return p.charAt(0) === "/" ? p.slice(1) : p;
    }
    document.querySelectorAll(".md-sidebar--primary .md-nav__link").forEach(function (a) {
        var entrySlugValue = entrySlug(a.getAttribute("href"));
        if (entrySlugValue !== null) {
            var target = ownDir + entrySlugValue;
            a.setAttribute("href", relativeHref(target, fromDir));
        }
        var ell = a.querySelector(".md-ellipsis");
        var label = ell ? ell.textContent.trim() : "";
        if (ell && labels[label]) {
            ell.textContent = labels[label];
        }
    });
    document.querySelectorAll(".md-sidebar--primary .md-nav__title .md-ellipsis").forEach(function (ell) {
        var label = ell.textContent.trim();
        if (labels[label]) {
            ell.textContent = labels[label];
        }
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
    (assets_dir / "site.js").write_text(build_site_js(), encoding="utf-8")
    print("  <site css/js> -> assets/site.css, assets/site.js")


if __name__ == "__main__":
    main()
