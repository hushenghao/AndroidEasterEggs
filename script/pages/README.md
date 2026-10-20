# Pages

Build the [GitHub Pages](https://hushenghao.github.io/AndroidEasterEggs/) site from the
[wiki submodule](../../wiki). The wiki itself stays the source of truth and keeps serving
GitHub wiki; this directory only converts it into a [MkDocs Material](https://squidfunk.github.io/mkdocs-material/)
site.

| Path                     | Tracked | Purpose                                                        |
|--------------------------|---------|----------------------------------------------------------------|
| `mkdocs.yml`             | yes     | Site config, navigation (replaces the wiki `_Sidebar.md`)      |
| `build_pages.py`         | yes     | Converts the wiki submodule into the `pages-docs/` source tree |
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
- The wiki submodule is read-only and never modified.

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

The header language switcher links are absolute and bound to the GitHub Pages project
path, so they only resolve on the deployed site; local previews (`mkdocs serve`,
opening `site/` from a file server) hit a 404 when switching languages. The switcher
script (`assets/site.js`) rewrites those links to the current page's counterpart in
the other language, derived from the canonical URL.

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
