# Changelogs

Parse `CHANGELOG.md` and `app/build.gradle.kts` files, generate the latest version `fastlane changelogs/*.txt` files

| Code      | Markdown                                 | fastlane changelogs/*.txt                                                            |
|-----------|------------------------------------------|--------------------------------------------------------------------------------------|
| **en-US** | [CHANGELOG.md](../../CHANGELOG.md)       | [fastlane/../**en-US**/changelogs](../../fastlane/metadata/android/en-US/changelogs) |
| **zh-CN** | [CHANGELOG_zh.md](../../CHANGELOG_zh.md) | [fastlane/../**zh-CN**/changelogs](../../fastlane/metadata/android/zh-CN/changelogs) |

## Usage

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
