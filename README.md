# Android Easter Eggs

Organize the Android release Easter eggs

[![GitHub license](https://img.shields.io/github/license/hushenghao/AndroidEasterEggs?logo=apache)](https://github.com/hushenghao/AndroidEasterEggs/blob/master/LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/hushenghao/AndroidEasterEggs?logo=github)](https://github.com/hushenghao/AndroidEasterEggs/releases)
[![F-Droid (including pre-releases)](https://img.shields.io/f-droid/v/com.dede.android_eggs?logo=fdroid)](https://f-droid.org/packages/com.dede.android_eggs)
[![Crowdin](https://badges.crowdin.net/easter-eggs/localized.svg)](https://crowdin.com/project/easter-eggs)
[![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/hushenghao/AndroidEasterEggs/buildRelease.yml?logo=githubactions&logoColor=white)](https://github.com/hushenghao/AndroidEasterEggs/actions/workflows/buildRelease.yml)

<div align="center">

![logo](assets/image/ic_launcher_round.png)

**[English](./README.md) • [中文](./README_zh.md)**

</div>

This project contains the complete code of the system Easter eggs, which aims to organize and be compatible with the system easter eggs, so as to ensure that most devices can experience different versions of Easter eggs without making too many modifications to the easter egg's code. Newer versions use new features of said system, but lower versions can only use some functions.

## Download

| [![Get it on F-Droid](https://fdroid.gitlab.io/artwork/badge/get-it-on.svg)](https://f-droid.org/packages/com.dede.android_eggs) | [![Get it on Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.dede.android_eggs&utm_source=Github&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1) | [![Get it on CoolApk](assets/image/badge_coolapk-en.svg)](https://www.coolapk.com/apk/com.dede.android_eggs) | [![Beta](assets/image/badge_pgyer.svg)](https://www.pgyer.com/eggs) |
|----------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|

* **Google Play** use [Play App Signing](https://support.google.com/googleplay/android-developer/answer/9842756), which may not be able to upgrade with other download channels.
* **Pgyer** downloaded the Beta version, which may contain some new features that are not yet stable.

## Screenshots

| ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/1.png) | ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/2.png) | ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/3.png) | ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/4.png) |
|----------------------------------------------------------------------|----------------------------------------------------------------------|----------------------------------------------------------------------|----------------------------------------------------------------------|

## Android Versions
| Name                                   | API level | Egg                                                     | Use new features [<sup>[1]</sup>](#id_new_features) | Minimum API [<sup>[2]</sup>](#id_full_egg_mini_api) |
|----------------------------------------|:---------:|---------------------------------------------------------|:---------------------------------------------------:|:---------------------------------------------------:|
| Android 14 (Upside Down Cake)          |    34     | Android 14 Easter Egg [<sup>[3]</sup>](#id_14_egg_name) |                         🚫                          |                          -                          |
| Android 13 (Tiramisu)                  |    33     | Paint Chips                                             |                          ✅                          |         31 [<sup>[4]</sup>](#id_android12)          |
| Android 12-12L (Snow Cone)             |   31-32   | Paint Chips                                             |                          ✅                          |         31 [<sup>[4]</sup>](#id_android12)          |
| Android 11 (Red Velvet Cake)           |    30     | Cat Controls(Collection)                                |                          ✅                          |         30 [<sup>[5]</sup>](#id_android11)          |
| Android 10 (Quince Tart)               |    29     | Icon Quiz                                               |                         🚫                          |                          -                          |
| Android 9 (Pie)                        |    28     | PAINT.APK                                               |                         🚫                          |                          -                          |
| Android 8.0-8.1 (Oreo)                 |   26-27   | Octopus                                                 |                         🚫                          |                          -                          |
| Android 7.0-7.1 (Nougat)               |   24-25   | Neko                                                    |                          ✅                          |          24 [<sup>[6]</sup>](#id_android7)          |
| Android 6.0 (Marshmallow)              |    23     | Marshmallow Land                                        |                         🚫                          |                          -                          |
| Android 5.0-5.1 (Lollipop)             |   21-22   | L Land                                                  |                         🚫                          |                          -                          |
| Android 4.4-4.4W (KitKat)              |   19-20   | Dessert Case                                            |                         🚫                          |                          -                          |
| Android 4.1-4.3 (Jelly Bean)           |   16-18   | BeanBag                                                 |                         🚫                          |                          -                          |
| Android 4.0-4.0.3 (Ice Cream Sandwich) |   14-15   | Nyandroid                                               |                         🚫                          |                          -                          |
| Android 3.0-3.2 (Honeycomb)            |   11-13   | Honeycomb [<sup>[7]</sup>](#id_egg_name)                |                         🚫                          |                          -                          |
| Android 2.3-2.3.3 (Gingerbread)        |   9-10    | Gingerbread [<sup>[7]</sup>](#id_egg_name)              |                         🚫                          |                          -                          |
| Android 2.2 (Froyo)                    |     8     | -                                                       |                          -                          |                          -                          |
| Android 2.0-2.1 (Eclair)               |    5-7    | -                                                       |                          -                          |                          -                          |
| Android 1.6 (Donut)                    |     4     | -                                                       |                          -                          |                          -                          |
| Android 1.5 (Cupcake)                  |     3     | -                                                       |                          -                          |                          -                          |
| Android 1.1 (Petit Four)               |     2     | -                                                       |                          -                          |                          -                          |
| Android 1.0 (Base)                     |     1     | -                                                       |                          -                          |                          -                          |

1. <span id='id_new_features'>Easter eggs with new features of the system are used, and the old version of the system can only use some functions.</span>
2. <span id='id_full_egg_mini_api'>The minimum API level required to fully experience Easter Egg.</span>
3. <span id='id_14_egg_name'>The name in the Android 14 easter egg code is used here, and no official easter egg naming has been found yet.</span>
4. <span id='id_android12'>Android 12 (API level 31) revamps the existing [Widgets API](https://developer.android.com/about/versions/12/features/widgets) to improve the user and developer experience in the platform and launchers.</span>
5. <span id='id_android11'>In Android 11 (API level 30) and later, the [Quick Access Device Controls](https://developer.android.com/develop/ui/views/device-control) feature lets the user quickly view and control external devices.</span>
6. <span id='id_android7'>In Android 7.0 (API level 24), expanded the scope of [Quick Settings](https://developer.android.com/about/versions/nougat/android-7.0#tile_api) to make it even more useful and convenient.</span>
7. <span id='id_egg_name'>There is no specific name for the old version of Easter eggs, and the system version alias are used here.</span>

## Contributing

See our [Contributing doc](.github/CONTRIBUTING.md) for information on how to report issues, [translate](https://crowdin.com/project/easter-eggs) the app into your language or help with development.

<details>
<summary>View translation status for all languages.</summary>

[![](script/crowdin/crowdin_project_progress.svg)](https://crowdin.com/project/easter-eggs)

</details>

## Build

Prerequisites The `Java17` and `Android SDK` have been installed, and environment variables have been configured.

```shell
./gradlew assembleRelease
```

## Other

[Privacy Policy](https://github.com/hushenghao/AndroidEasterEggs/wiki/Privacy-policy)

[Contact me 📧](mailto:dede.hu@qq.com)

### Thanks

[AOSP Frameworks](https://github.com/aosp-mirror/platform_frameworks_base)

[All translation contributors](https://crowdin.com/project/easter-eggs/members)
