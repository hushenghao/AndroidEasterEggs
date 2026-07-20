# Changelogs

## `changelogs.py` — Fastlane metadata

Parse `CHANGELOG.md` and `app/build.gradle.kts` files, generate the latest version `fastlane changelogs/*.txt` files

| Code      | Markdown                                 | fastlane changelogs/*.txt                                                            |
|-----------|------------------------------------------|--------------------------------------------------------------------------------------|
| **en-US** | [CHANGELOG.md](../../CHANGELOG.md)       | [fastlane/../**en-US**/changelogs](../../fastlane/metadata/android/en-US/changelogs) |
| **zh-CN** | [CHANGELOG_zh.md](../../CHANGELOG_zh.md) | [fastlane/../**zh-CN**/changelogs](../../fastlane/metadata/android/zh-CN/changelogs) |

### Usage

1. Make sure the `versionName` and `versionCode` fields of `app/build.gradle.kts` are correct.

    ```kotlin
    versionCode = 38
    versionName = "2.3.1"
    ```

2. Edit the latest changelog of the `CHANGLOG*.md` files to `versionName`.

    ```markdown
    ### v2.3.1

    - Feat: xxx
    - Fix: xxx
    - ...
    ```

3. Run script.

    ```sh
    python3 ./changelogs.py
    ```

4. Check whether the generated changelog files `versionCode.txt` is correct.

    * fastlane/metadata/android/**en-US**/changelogs/38.txt
    * fastlane/metadata/android/**zh-CN**/changelogs/38.txt

---

## `generate_release_notes.py` — Release notes from git log

Parse Conventional Commits between git refs and generate release notes in Markdown.

### Usage

```sh
# Generate grouped release notes (for GitHub Release)
python3 generate_release_notes.py --current-tag v5.3.0

# Generate CHANGELOG.md draft (flat list, with <DATE> placeholder)
python3 generate_release_notes.py --current-tag v5.3.0 --draft

# Specify ref range explicitly
python3 generate_release_notes.py --from-ref v5.2.0 --to-ref v5.3.0

# Write to file
python3 generate_release_notes.py --current-tag v5.3.0 --output release_notes.md
```

### How it works

- Runs `git log` between `v5.2.0..v5.3.0` (auto-detects previous tag if `--from-ref` omitted)
- Parses Conventional Commits (`feat:`, `fix:`, `chore:`, etc.)
- Groups by type and outputs Markdown
- `ci:` commits are excluded (infrastructure noise)
- `--draft` mode produces a flat list suitable for pasting into `CHANGELOG.md`
