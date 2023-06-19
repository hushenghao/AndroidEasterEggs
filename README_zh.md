# Android Easter Eggs

整理了Android系统各正式版的彩蛋

![Easter Eggs](https://img.shields.io/badge/Android-Easter%20Eggs-red?logo=android) ![GitHub top language](https://img.shields.io/github/languages/top/hushenghao/AndroidEasterEggs?logo=kotlin)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/hushenghao/AndroidEasterEggs) [![GitHub](https://img.shields.io/github/license/hushenghao/AndroidEasterEggs)](https://github.com/hushenghao/AndroidEasterEggs/blob/master/LICENSE) [![GitHub release (latest by date)](https://img.shields.io/github/v/release/hushenghao/AndroidEasterEggs)](https://github.com/hushenghao/AndroidEasterEggs/releases)

![logo](./images/ic_launcher_round.png)

[English](./README.md) • [中文](./README_zh.md)

## 下载安装

| [![下载应用，请到 Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/zh-cn_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.dede.android_eggs&utm_source=Github&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1) | [![](./images/badge_coolapk.png)](https://www.coolapk.com/apk/com.dede.android_eggs) | [![Beta](./images/badge_pgyer.png)](https://www.pgyer.com/eggs) |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|-----------------------------------------------------------------|

项目包含了系统彩蛋完整代码，旨在对系统彩蛋的整理和兼容，以保证大多数设备可以体验到不同版本的彩蛋，不会对系统彩蛋代码做过多修改。部分版本使用了系统新特性，低版本只能使用部分功能。

## 截图

| ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/2.png) | ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/1.png) | ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/3.png) |
|----------------------------------------------------------------------|----------------------------------------------------------------------|----------------------------------------------------------------------|

## Android版本
| 名称                                     |  SDK  | 彩蛋                            | 状态  | 有使用新特性 [*](#id_new_features) | 最小版本 [*](#id_full_egg_mini_sdk) |
|----------------------------------------|:-----:|-------------------------------|:---:|:----------------------------:|:-------------------------------:|
| Android 14 (Upside Down Cake)          |  34   |                               | ⌛️  |                              |                                 |
| Android 13 (Tiramisu)                  |  33   | Paint Chips                   |  ✅  |              ✅               | 31 [*](#id_color_vector_fonts)  |
| Android 12 (Snow Cone)                 | 31~32 | Paint Chips                   |  ✅  |              ✅               |               31                |
| Android 11 (Red Velvet Cake)           |  30   | Cat Controls(Collection)      |  ✅  |              ✅               |               30                |
| Android 10 (Queen Cake)                |  29   | Icon Quiz                     |  ✅  |              ❌               |                -                |
| Android 9 (Pie)                        |  28   | PAINT.APK                     |  ✅  |              ❌               |                -                |
| Android 8.0~8.1 (Oreo)                 | 26~27 | Octopus                       |  ✅  |              ❌               |                -                |
| Android 7.0~7.1 (Nougat)               | 24~25 | Neko                          |  ✅  |              ✅               |               24                |
| Android 6.0 (Marshmallow)              |  23   | Marshmallow Land              |  ✅  |              ❌               |                -                |
| Android 5.0~5.1 (Lollipop)             | 21~22 | L Land                        |  ✅  |              ❌               |                -                |
| Android 4.4~4.4W (KitKat)              | 19~20 | Dessert Case                  |  ✅  |              ❌               |                -                |
| Android 4.1~4.3 (Jelly Bean)           | 16~18 | BeanBag                       |  ✅  |              ❌               |                -                |
| Android 4.0~4.0.3 (Ice Cream Sandwich) | 14~15 | Nyandroid                     |  ✅  |              ❌               |                -                |
| Android 3.0~3.2 (Honeycomb)            | 11~13 | Honeycomb [*](#id_egg_name)   |  ✅  |              ❌               |                -                |
| Android 2.3~2.3.3 (Gingerbread)        | 9~10  | Gingerbread [*](#id_egg_name) |  ✅  |              ❌               |                -                |
| Android 2.2 (Froyo)                    |   8   | -                             |  -  |              -               |                -                |
| Android 2.0~2.1 (Eclair)               |  5~7  | -                             |  -  |              -               |                -                |
| Android 1.6 (Donut)                    |   4   | -                             |  -  |              -               |                -                |
| Android 1.5 (Cupcake)                  |   3   | -                             |  -  |              -               |                -                |
| Android 1.1 (Petit Four)               |   2   | -                             |  -  |              -               |                -                |
| Android 1.0 (Base)                     |   1   | -                             |  -  |              -               |                -                |

* <span id='id_new_features'>使用了系统新特性的彩蛋，老版本系统只能使用部分功能。</span>
* <span id='id_full_egg_mini_sdk'>完整体验彩蛋所需要的最低SDK版本。</span>
* <span id='id_color_vector_fonts'>[Android 13](https://developer.android.google.cn/about/versions/13/features#color-vector-fonts) 可支持呈现 [COLRv1](https://developer.chrome.com/blog/colrv1-fonts/) 字体，并将系统表情符号更新成了 COLRv1 格式。</span>
* <span id='id_egg_name'>老版本的彩蛋没有具体命名，这里使用系统版本别名。</span>

## 测试

使用Android设备或者模拟器.
```shell
./gradlew app:cAT
```

由Android Gradle插件管理的模拟器的设备类型。

```shell
./gradlew app:pixel4Api33DebugAndroidTest
```

## 其他

如果存在错误和问题，欢迎提交 [Issues](https://github.com/hushenghao/AndroidEasterEggs/issues) 。如果你对本项目感兴趣，欢迎提交 [Pull requests](https://github.com/hushenghao/AndroidEasterEggs/pulls) 参与。

[联系我 📧](mailto:dede.hu@qq.com)

### 感谢
[系统源码](https://github.com/aosp-mirror/platform_frameworks_base)

<!--
[🦖 T-Rex Run 3D](https://github.com/Priler/dino3d)
-->
