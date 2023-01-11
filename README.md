# Android Easter Eggs

Organize the Easter eggs of the official versions of the Android system.

![Easter Eggs](https://img.shields.io/badge/Android-Easter%20Eggs-red?logo=android) ![GitHub top language](https://img.shields.io/github/languages/top/hushenghao/AndroidEasterEggs?logo=kotlin)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/hushenghao/AndroidEasterEggs) [![GitHub](https://img.shields.io/github/license/hushenghao/AndroidEasterEggs)](https://github.com/hushenghao/AndroidEasterEggs/blob/master/LICENSE) [![GitHub release (latest by date)](https://img.shields.io/github/v/release/hushenghao/AndroidEasterEggs)](https://github.com/hushenghao/AndroidEasterEggs/releases)

![logo](./images/ic_launcher_round.png)

**Download and install**

<a href='https://play.google.com/store/apps/details?id=com.dede.android_eggs&utm_source=Github&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img height='60' alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>
 
[![coolapk](https://img.shields.io/badge/Coolapk--4caf50?logo=android&style=for-the-badge)](https://www.coolapk.com/apk/com.dede.android_eggs) [![pgyer](https://img.shields.io/badge/Beta-Pgyer-1abc9c?logo=androidstudio&style=for-the-badge)](https://www.pgyer.com/eggs)

The project contains the complete code of the system Easter eggs, which aims to organize and compatible with the system eggs, so as to ensure that most devices can experience different versions of Easter eggs and will not make too many modifications to the system egg code. Some versions use new features of the system, and lower versions can only use some functions.

[中文](./README_zh.md)

## Screenshots
<img height="512" src="./fastlane/metadata/android/en-US/images/phoneScreenshots/3.png"/> <img height="512" src="./fastlane/metadata/android/en-US/images/phoneScreenshots/1.png"/>

## Details
| Name                               |  SDK  | Egg                           | Status | Use new features [*](#id_new_features) | Minimum SDK [*](#id_full_egg_mini_sdk) |
|------------------------------------|:-----:|-------------------------------|:------:|:--------------------------------------:|:--------------------------------------:|
| Android 14 (Upside Down Cake)      |       |                               |   ⌛️   |                                        |                                        |
| Android 13 (Tiramisu)              |  33   | Paint Chips                   |   ✅    |                   ✅                    |     31 [*](#id_color_vector_fonts)     |
| Android 12 (Snow Cone)             | 31~32 | Paint Chips                   |   ✅    |                   ✅                    |                   31                   |
| Android 11 (Red Velvet Cake)       |  30   | Cat Controls(Collection)      |   ✅    |                   ✅                    |                   30                   |
| Android 10 (Queen Cake)            |  29   | Icon Quiz                     |   ✅    |                   ❌                    |                   -                    |
| Android 9 (Pie)                    |  28   | PAINT.APK                     |   ✅    |                   ❌                    |                   -                    |
| Android 8.x (Oreo)                 | 26~27 | Octopus                       |   ✅    |                   ❌                    |                   -                    |
| Android 7.x (Nougat)               | 24~25 | Neko                          |   ✅    |                   ✅                    |                   24                   |
| Android 6.x (Marshmallow)          |  23   | Marshmallow Land              |   ✅    |                   ❌                    |                   -                    |
| Android 5.x (Lollipop)             | 21~22 | L Land                        |   ✅    |                   ❌                    |                   -                    |
| Android 4.4 (KitKat)               | 19~20 | Dessert Case                  |   ✅    |                   ❌                    |                   -                    |
| Android 4.x (Jelly Bean)           | 16~18 | BeanBag                       |   ✅    |                   ❌                    |                   -                    |
| Android 4.0.x (Ice Cream Sandwich) | 14~15 | Nyandroid                     |   ✅    |                   ❌                    |                   -                    |
| Android 3.x (Honeycomb)            | 11~13 | Honeycomb [*](#id_egg_name)   |   ✅    |                   ❌                    |                   -                    |
| Android 2.3.x (Gingerbread)        | 9~10  | Gingerbread [*](#id_egg_name) |   ✅    |                   ❌                    |                   -                    |
| Android 2.2 (Froyo)                |   8   | -                             |   -    |                   -                    |                   -                    |
| Android 2.x (Eclair)               |  5~7  | -                             |   -    |                   -                    |                   -                    |
| Android 1.6 (Donut)                |   4   | -                             |   -    |                   -                    |                   -                    |
| Android 1.5 (Cupcake)              |   3   | -                             |   -    |                   -                    |                   -                    |
| Android 1.x (Base)                 |  1~2  | -                             |   -    |                   -                    |                   -                    |

* <span id='id_new_features'>Easter eggs with new features of the system are used, and the old version of the system can only use some functions.</span>
* <span id='id_full_egg_mini_sdk'>The minimum SDK version required to fully experience Easter Egg.</span>
* <span id='id_color_vector_fonts'>Starting in [Android 13](https://developer.android.google.cn/about/versions/13/features#color-vector-fonts), the system includes rendering support for [COLRv1](https://developer.chrome.com/blog/colrv1-fonts/) fonts and updates system emoji to the COLRv1 format.</span>
* <span id='id_egg_name'>There is no specific name for the old version of Easter eggs, and the system version alias are used here.</span>

## Test

Using Android devices or emulators.
```shell
./gradlew app:cAT
```

Device type for emulators to be managed by the Android Gradle Plugin.

```shell
./gradlew app:pixel4Api33DebugAndroidTest
```

## Other

If there are errors and problems, please submit [Issues](https://github.com/hushenghao/AndroidEasterEggs/issues). If you are interested in this project, please submit [Pull requests](https://github.com/hushenghao/AndroidEasterEggs/pulls) to participate.

[System source code](https://github.com/aosp-mirror/platform_frameworks_base)

[Contact me](mailto:dede.hu@qq.com)
