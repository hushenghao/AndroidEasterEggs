# Pages

Build the [GitHub Pages](https://hushenghao.github.io/AndroidEasterEggs/) site from the
[wiki submodule](../../wiki). The wiki itself stays the source of truth and keeps serving
GitHub wiki; this directory only converts it into a [MkDocs Material](https://squidfunk.github.io/mkdocs-material/)
site.

| Path                     | Tracked | Purpose                                                        |
|--------------------------|---------|----------------------------------------------------------------|
| `mkdocs.yml`             | yes     | Site config, navigation (replaces the wiki `_Sidebar.md`)      |
| `build_pages.py`         | yes     | Converts the wiki submodule into the `pages-docs/` source tree |
| `vector_to_svg.py`       | yes     | Exports VectorDrawable / adaptive-icon XML to SVG              |
| `requirements.txt`       | yes     | Pinned `mkdocs-material` version                               |
| `.gitignore`             | yes     | Ignores the two generated paths below, scoped to this directory|
| `pages-docs/`            | no      | Generated MkDocs sources                                       |
| `site/`                  | no      | Generated site output                                          |

## Conversion rules

- English pages at the root, zh-CN pages under `zh/`.
- The Home pages drop the centered badge block (`<div align="center">...</div>`);
  everything else (feature graphic, screenshots, star history) is kept.
- Absolute `github.com/.../wiki/<Page>` URLs are rewritten to site-relative links.
  Pages that are not published here (the retired `隐私协议.md` placeholder, `_Sidebar.md`)
  keep their GitHub URL, so links to them still reach the wiki.
- The wiki submodule is read-only for `build_pages.py`; the script never modifies it.
  Wiki content changes are committed in the wiki repository and picked up through the
  submodule pointer.

## Version logos

The version tables embed one logo per Android release from
`assets/image/android-versions/`. The wiki pages use absolute
`raw.githubusercontent.com` URLs, so the same markup renders on GitHub wiki and on the
MkDocs site.

Android drawables cannot be referenced directly from Markdown: browsers only render
SVG, not VectorDrawable XML (`<vector android:pathData=...>`). Regenerate the exported
files after a drawable change:

```sh
python3 script/pages/vector_to_svg.py SOURCE_XML OUTPUT_SVG [...]
```

The script resolves `@drawable/` and `@color/` references against the source module's
`res` directory, including `aapt:attr` gradient fills and chained color aliases. An
`<adaptive-icon>` source is composited from its background and foreground layers
(background first, regardless of the XML order) and clipped to the 72dp launcher mask,
so the exported icon matches what launchers display. Every export is normalized to a
72×72 square canvas with the artwork centered, so the wiki tables use one fixed
24×24 image size. Releases without a logo drawable fall back to the classic robot;
Gingerbread, Honeycomb, Jelly Bean and KitKat are still legacy `.webp` bitmaps.

## Usage

```sh
# From the repository root
python3 -m pip install -r script/pages/requirements.txt
python3 script/pages/build_pages.py

# Local preview, then open http://127.0.0.1:8000
mkdocs serve -f script/pages/mkdocs.yml

# Production build (strict mode fails on broken internal links)
mkdocs build --strict -f script/pages/mkdocs.yml
```

## Deployment

`.github/workflows/pages.yml` builds and publishes the site on pushes that change the
wiki submodule pointer, `script/pages/**`, or the workflow itself. Editing wiki content
means bumping the submodule pointer in the main repository (`cd wiki && git pull`);
commits made only in the wiki repository do not trigger a build.

The `mkdocs-material` version in `requirements.txt` is pinned because MkDocs 2.0 has
announced backward-incompatible changes without a migration path — do not upgrade it
casually.

At page load `assets/site.js` rewrites the header language switcher and the primary
sidebar for non-English pages: links point at the current page's counterpart in the
other language (the canonical URL supplies the page slug) and sidebar labels are
translated via `NAV_LABELS`. All generated links are relative to the current page,
so they resolve identically on the deployed project path, plain-root local servers
and `file://` previews.

## Adding a language

1. Publish the translated pages in the wiki under `locale/<locale>/` as
   `<Page>-<locale>.md` (one for each entry of `PAGES`), then bump the wiki submodule
   pointer in the main repository.
2. `build_pages.py`: add the locale to `LANGUAGES` (`"<locale>": "<url prefix>"`).
3. `mkdocs.yml`: add an `extra.alternate` switcher entry (`name`/`lang`/`link`) and
   a `<lang>/*` pattern to `not_in_nav`.

The badge strip, link rewriting and the switcher script pick the new language up
automatically. Page sets must stay symmetric across languages — the switcher jumps
by equal page slug, and a missing translation would 404.

## Solution research (2026-10-20)

Survey of popular GitHub Pages documentation generators, evaluated against this
project's requirements: theme switching, i18n, index navigation and client-side
search. Star counts taken from the GitHub API on 2026-10-20.

| Solution                  | Stars  | Stack        | Theme switch | i18n                              | Navigation | Search                | Status                                   |
|---------------------------|--------|--------------|--------------|-----------------------------------|------------|-----------------------|------------------------------------------|
| MkDocs Material (current)| 27.6k  | Python       | ✅            | custom (`build_pages.py`)         | ✅          | ✅ built-in           | ⚠️ Maintenance mode, EOL 2027-05          |
| Zensical (successor)      | 6.0k   | Python/Rust  | ✅            | ⚠️ single language only            | ✅          | ✅ Disco engine       | ✅ Active, 0.1.0 stable line 2026-11     |
| Docusaurus                | 66.5k  | Node/React   | ✅            | ✅ native                          | ✅          | ✅ local (hosted opt) | ✅ Meta-maintained                       |
| VitePress                 | 18.4k  | Node/Vue     | ✅            | ✅ native                          | ✅          | ✅ built-in           | ✅ Active (2.0 in prerelease)            |
| Starlight (Astro)         | 9.4k   | Astro        | ✅            | ✅ native (auto UI translation)    | ✅          | ✅ built-in           | ✅ Active                                |
| Docsify                   | 31.6k  | Browser JS   | ✅            | ⚠️ plugins                         | ✅          | ⚠️ plugins            | ⚠️ No static build, weak SEO             |
| Hugo + Docsy              | 90.2k  | Go           | ✅            | ✅                                 | ✅          | ✅ Algolia or built-in| ✅ Active, complex config                |

### Key findings

- **Material for MkDocs entered maintenance mode in 2025-11** (announced with 9.7.0,
  the last feature release); the team moved to Zensical. Critical fixes are committed
  through 2027-05-05 ([mkdocs-material#8523](https://github.com/squidfunk/mkdocs-material/issues/8523)).
  MkDocs core itself has been quiet since 2024; MkDocs 2.0 (prerelease) drops the
  plugin system, which is why `requirements.txt` stays pinned.
- **Zensical** is the official successor (MIT, free): a rewrite with a much faster
  build engine and the Disco search engine (~20× faster than Material's). Its config
  mirrors `mkdocs.yml`, so a migration is a config-level switch. Multi-language
  support is only on its roadmap — not a blocker here, because i18n in this site is
  already implemented by `build_pages.py` rather than by the theme.
- **Docusaurus** is the popularity leader with native i18n/versioning, but adds a
  Node + React toolchain; overkill for a ~10-page site sourced from the wiki.
- **Hugo** has the largest community overall but is a general-purpose generator whose
  documentation features come from themes (Docsy/Doks) with high config overhead.

### Decision

- **Keep MkDocs Material for now.** All four requirements are already met and
  deployed; migration cost (rewriting `build_pages.py`, new toolchain) outweighs the
  benefit of a maintenance-mode theme with fixes until 2027-05.
- **Re-evaluate Zensical after its 0.1.0 stable line (2026-11)** as a drop-in
  upgrade path (`mkdocs.yml` compatible), which would also pick up the new search
  engine and future features.
- **Consider Docusaurus** only if the site outgrows the wiki conversion model (blog,
  API docs, versioned docs).
