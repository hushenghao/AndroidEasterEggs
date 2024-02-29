# Changelogs

解析 `CHANGELOG.md` 和 `app/build.gradle.kts` 文件，生成最新版本 `fastlane changelogs/*.txt` 文件

| Code      | Markdown                                 | fastlane changelogs/*.txt                                                            |
|-----------|------------------------------------------|--------------------------------------------------------------------------------------|
| **en-US** | [CHANGELOG.md](../../CHANGELOG.md)       | [fastlane/../**en-US**/changelogs](../../fastlane/metadata/android/en-US/changelogs) |
| **zh-CN** | [CHANGELOG_zh.md](../../CHANGELOG_zh.md) | [fastlane/../**zh-CN**/changelogs](../../fastlane/metadata/android/zh-CN/changelogs) |

## Usage

1. 确保 `app/build.gradle.kts` 的 `versionName` 和 `versionCode` 字段的正确

    ```kotlin
    versionCode = 38
    versionName = "2.3.1"
    ```

2. 编辑 `CHANGLOG*.md` 文件的最新更新日志为 versionName 对应版本名

    ```markdown
    ### v2.3.1

    - 更新日志1
    - 更新日志2
    - ...
    ```

3. 运行脚本

    ```sh
    python3 ./changelogs.py
    ```

4. 检查生成对应 versionCode.txt 文件的更新日志是否正确

    * fastlane/metadata/android/**en-US**/changelogs/38.txt
    * fastlane/metadata/android/**zh-CN**/changelogs/38.txt
