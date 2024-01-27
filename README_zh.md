# Android Easter Egg Collections

收集了Android系统各正式版的彩蛋

[![GitHub license](https://img.shields.io/github/license/hushenghao/AndroidEasterEggs?logo=apache)](https://github.com/hushenghao/AndroidEasterEggs/blob/master/LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/hushenghao/AndroidEasterEggs?logo=github)](https://github.com/hushenghao/AndroidEasterEggs/releases)
[![F-Droid (including pre-releases)](https://img.shields.io/f-droid/v/com.dede.android_eggs?logo=fdroid)](https://f-droid.org/packages/com.dede.android_eggs)
[![Crowdin](https://badges.crowdin.net/easter-eggs/localized.svg)](https://crowdin.com/project/easter-eggs)
[![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/hushenghao/AndroidEasterEggs/buildRelease.yml?logo=githubactions&logoColor=white)](https://github.com/hushenghao/AndroidEasterEggs/actions/workflows/buildRelease.yml)

<div align="center">

![logo](assets/image/ic_launcher_round.png)

**[English](./README.md) • [中文](./README_zh.md)**

</div>

项目包含了系统彩蛋完整代码，旨在对系统彩蛋的整理和兼容，以保证大多数设备可以体验到不同版本的彩蛋，不会对系统彩蛋代码做过多修改。部分版本使用了系统新特性，低版本只能使用部分功能。

## 下载

| [![下载应用，请到 F-Droid](https://fdroid.gitlab.io/artwork/badge/get-it-on-zh-cn.svg)](https://f-droid.org/packages/com.dede.android_eggs) | [![下载应用，请到 Google Play](assets/image/badge_playstore_fixpadding-zh.png)](https://play.google.com/store/apps/details?id=com.dede.android_eggs&utm_source=Github&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1) | [![Beta](assets/image/badge_pgyer.svg)](https://www.pgyer.com/eggs) |
|--------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|

* **Google Play** 使用 [Play 应用签名功能](https://support.google.com/googleplay/android-developer/answer/9842756)，可能出现无法和其他下载渠道互相升级的问题。
* **蒲公英** 下载的是Beta版本，可能包含了一些尚未稳定的新功能。

## 截图

| ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/1.png) | ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/2.png) | ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/3.png) | ![](./fastlane/metadata/android/en-US/images/phoneScreenshots/4.png) |
|----------------------------------------------------------------------|----------------------------------------------------------------------|----------------------------------------------------------------------|----------------------------------------------------------------------|

## Android 版本
| 名称                                     | API level | 彩蛋                                                      | 使用了新特性 [<sup>[1]</sup>](#id_new_features) | 最低 API [<sup>[2]</sup>](#id_full_egg_mini_api) | 发布时间 [<sup>[8]</sup>](#first_release_date) |
|----------------------------------------|:---------:|---------------------------------------------------------|:-----------------------------------------:|:----------------------------------------------:|:------------------------------------------:|
| Android 14 (Upside Down Cake)          |    34     | Android 14 Easter Egg [<sup>[3]</sup>](#id_14_egg_name) |                    🚫                     |                       -                        |                  2023-09                   |
| Android 13 (Tiramisu)                  |    33     | Paint Chips                                             |                     ✅                     |       31 [<sup>[4]</sup>](#id_android12)       |                  2022-09                   |
| Android 12-12L (Snow Cone)             |   31-32   | Paint Chips                                             |                     ✅                     |       31 [<sup>[4]</sup>](#id_android12)       |                  2021-09                   |
| Android 11 (Red Velvet Cake)           |    30     | Cat Controls(Collection)                                |                     ✅                     |       30 [<sup>[5]</sup>](#id_android11)       |                  2020-09                   |
| Android 10 (Quince Tart)               |    29     | Icon Quiz                                               |                    🚫                     |                       -                        |                  2019-09                   |
| Android 9 (Pie)                        |    28     | PAINT.APK                                               |                    🚫                     |                       -                        |                  2018-08                   |
| Android 8.0-8.1 (Oreo)                 |   26-27   | Octopus                                                 |                    🚫                     |                       -                        |                  2017-08                   |
| Android 7.0-7.1 (Nougat)               |   24-25   | Neko                                                    |                     ✅                     |       24 [<sup>[6]</sup>](#id_android7)        |                  2016-08                   |
| Android 6.0 (Marshmallow)              |    23     | Marshmallow Land                                        |                    🚫                     |                       -                        |                  2015-10                   |
| Android 5.0-5.1 (Lollipop)             |   21-22   | L Land                                                  |                    🚫                     |                       -                        |                  2014-11                   |
| Android 4.4-4.4W (KitKat)              |   19-20   | Dessert Case                                            |                    🚫                     |                       -                        |                  2013-10                   |
| Android 4.1-4.3 (Jelly Bean)           |   16-18   | BeanBag                                                 |                    🚫                     |                       -                        |                  2012-07                   |
| Android 4.0-4.0.3 (Ice Cream Sandwich) |   14-15   | Nyandroid                                               |                    🚫                     |                       -                        |                  2011-10                   |
| Android 3.0-3.2 (Honeycomb)            |   11-13   | Honeycomb [<sup>[7]</sup>](#id_egg_name)                |                    🚫                     |                       -                        |                  2011-02                   |
| Android 2.3-2.3.3 (Gingerbread)        |   9-10    | Gingerbread [<sup>[7]</sup>](#id_egg_name)              |                    🚫                     |                       -                        |                  2010-12                   |
| Android 2.2 (Froyo)                    |     8     | -                                                       |                     -                     |                       -                        |                  2010-05                   |
| Android 2.0-2.1 (Eclair)               |    5-7    | -                                                       |                     -                     |                       -                        |                  2009-10                   |
| Android 1.6 (Donut)                    |     4     | -                                                       |                     -                     |                       -                        |                  2009-09                   |
| Android 1.5 (Cupcake)                  |     3     | -                                                       |                     -                     |                       -                        |                  2009-04                   |
| Android 1.1 (Petit Four)               |     2     | -                                                       |                     -                     |                       -                        |                  2009-02                   |
| Android 1.0 (Base)                     |     1     | -                                                       |                     -                     |                       -                        |                  2008-09                   |

1. <span id='id_new_features'>使用了系统新特性的彩蛋，老版本系统只能使用部分功能。</span>
2. <span id='id_full_egg_mini_api'>完整体验彩蛋所需要的最低API版本。</span>
3. <span id='id_14_egg_name'>这里使用的是 Android 14 彩蛋代码中的名称，目前没有找到官方的彩蛋命名。</span>
4. <span id='id_android12'>Android 12 (API level 31) 改进了现有的 [Widgets API](https://developer.android.google.cn/about/versions/12/features/widgets?hl=zh-cn)，以改善平台和启动器中的用户和开发者体验。</span>
5. <span id='id_android11'>在 Android 11 (API level 30) 及更高版本中，[快速访问设备控制器](https://developer.android.google.cn/guide/topics/ui/device-control?hl=zh-cn) 功能可让用户通过 Android 电源菜单快速查看和控制外部设备。</span>
6. <span id='id_android7'>在 Android 7 (API level 24) 中，扩展了 [快速设置](https://developer.android.google.cn/about/versions/nougat/android-7.0?hl=zh-cn#tile_api) 的范围，使其更加有用而且更方便。</span>
7. <span id='id_egg_name'>老版本的彩蛋没有具体命名，这里使用系统版本别名。</span>
8. <span id='first_release_date'>发布时间为第一个正式版本发布的月份，正式版之后发布的MR小版本没有列出。**目前Android正式版发布时间为每年的9月。**</span>

## 贡献

请查看我们的[贡献文档](.github/CONTRIBUTING.md)来报告问题，或参与应用程序和文档的[翻译](https://zh.crowdin.com/project/easter-eggs)。

<details>
<summary>查看所有语言的翻译状态。</summary>

[![](script/crowdin/crowdin_project_progress.svg)](https://zh.crowdin.com/project/easter-eggs)

</details>

## 感谢
* [AOSP Frameworks](https://github.com/aosp-mirror/platform_frameworks_base)
* [所有翻译贡献者](https://zh.crowdin.com/project/easter-eggs/members)

## 协议
```text
Copyright 2023 Hu Shenghao

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
