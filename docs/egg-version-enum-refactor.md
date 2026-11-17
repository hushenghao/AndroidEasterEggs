# 彩蛋标识重构：full API Level → 自定义枚举

- **状态**：规划中（未开始）
- **记录日期**：2026-11-17
- **范围**：`:core:provider`、`:core:egg-stats`、`:app`、`:core:activity-actions`、`:core:shortcut`、`:eggs:*`
- **目标版本**：待定

## 1. 背景

当前彩蛋的“身份”、排序、搜索、统计和快捷方式都依赖 `fullApiLevel` / `fullApiLevelRange`，其值来自 `Build.VERSION_CODES_FULL`：

```kotlin
open class EasterEgg(
    val iconRes: Int,
    val nameRes: Int,
    val nicknameRes: Int,
    override val fullApiLevelRange: IntRange,
    val actionClass: Class<out Activity>? = null,
) : BaseEasterEgg
```

相关事实：

- `EasterEgg.equals/hashCode` 使用 `fullApiLevelRange`
- 列表 key `lazyItemKey()` 使用 `fullApiLevelRange.first/last`
- `EasterEggModules` 按 `fullApiLevel` 排序
- 统计表 `egg_play_events` 用 `(api_level_first, api_level_last)` 作为彩蛋身份
- 快捷方式用 `egg.hashCode()` / `fullApiLevel`
- 深链、AppFunctions、模拟时钟小部件用公开 API level 查找彩蛋
- 时间线、组件管理也用 `fullApiLevel`
- `EasterEgg.VERSION_CODES_FULL` 内部用 `major * 100000 + minor` 编码，预览版用 `stable - 1`

由此带来的问题：

- 彩蛋身份被平台常量 `Build.VERSION_CODES_FULL` 约束，compileSdk 变化或未来预览版会影响业务代码
- 预览/beta 与稳定版可能映射到同一个公开 API（例如 T Beta 与 Android 13 都是 API 33）
- “Android 版本”和“公开 API level”是两套编号，转换逻辑散落在 `EasterEggHelp`、搜索、深链、AppFunctions 等处
- 区间彩蛋的身份是一个 `IntRange`，未来若调整区间，统计/快捷方式历史会变成孤儿
- 新增未来版本（如 Android Next）依赖 `Build.VERSION_CODES.CUR_DEVELOPMENT`，不是稳定的业务常量

## 2. 目标与非目标

### 目标

- 用自定义枚举描述 Android 版本和彩蛋版本，不再用 `Build.VERSION_CODES_FULL` 做身份
- 彩蛋拥有稳定、可落库的字符串身份 `EasterEgg.id`
- `Build.VERSION_CODES` / `Build.VERSION.SDK_INT` 只用于：
  - 设备能力判断（`SDK_INT`、`@RequiresApi`、`@ChecksSdkIntAtLeast`）
  - 界面 “API X” 文案的来源（由枚举字段提供，不参与身份比较）
- 搜索、排序、分组、时间线、组件、统计、快捷方式、深链、AppFunctions、小部件全部统一到新模型
- 统计表在重构后期迁移到稳定 `egg_key`，不再依赖 full API 区间

### 非目标

- 不改变彩蛋 activity 的行为、资源或 AOSP 派生代码
- 不改变深链/AppFunctions 对外的公开 API level 约定
- 不引入自定义彩蛋（issue #1012）所需的动态彩蛋机制；但身份类型要为未来留扩展空间
- 不删除设备能力判断中的 `Build.VERSION_CODES` / `Build.VERSION.SDK_INT`

## 3. 推荐模型

分两层：

1. **`AndroidVersion` 枚举**：Android 版本目录，替代 `Build.VERSION_CODES_FULL` 与 `EasterEggHelp.apiLevelArrays`
2. **`EasterEgg.id` 字符串**：彩蛋的稳定身份，默认取主版本的枚举 ID，未来可覆盖

```kotlin
// :core:provider，纯 Kotlin，不依赖 Android
enum class AndroidVersion(
    val id: String,             // 稳定、append-only，落库/快捷方式用
    val versionName: String,    // "13"、"12L"、"T"、"16.1"
    val apiLevel: Int?,         // 公开 API，用于深链/AppFunctions/API X
    val apiLevelLabel: String,  // 显示用，可表达 "36.1"
    val sortOrder: Int,         // 显式排序，避免依赖 ordinal
    val isPreview: Boolean = false,
    val isBeta: Boolean = false,
) {
    BASE("android_1_0", "1.0", 1, "1", 1),
    // ...
    S("android_12", "12", 31, "31", 3100),
    S_V2("android_12l", "12L", 32, "32", 3200),
    T_BETA("android_t_beta", "T", 33, "33", 3299, isBeta = true),
    TIRAMISU("android_13", "13", 33, "33", 3300),
    // ...
    BAKLAVA("android_16", "16", 36, "36", 3600),
    BAKLAVA_1("android_16_1", "16.1", 36, "36.1", 3601),
    CINNAMON_BUN("android_17", "17", 37, "37", 3700),
    CINNAMON_BUN_1("android_17_1", "17.1", 37, "37.1", 3701),
    NEXT("android_next", "Next", null, "Next", 9999, isPreview = true),
}
```

彩蛋：

```kotlin
open class EasterEgg(
    ...
    val version: AndroidVersion,                               // 主版本
    val additionalVersions: List<AndroidVersion> = emptyList(), // 区间追加版本
) : BaseEasterEgg {
    val versions = listOf(version) + additionalVersions
    open val id: String get() = version.id                      // 稳定身份
}
```

示例：

```kotlin
// 单版本
fullApiLevel = TIRAMISU
// 替换为
version = AndroidVersion.TIRAMISU

// 区间
new IntRange(HONEYCOMB, HONEYCOMB_MR2)
// 替换为
version = AndroidVersion.HONEYCOMB,
additionalVersions = listOf(AndroidVersion.HONEYCOMB_MR1, AndroidVersion.HONEYCOMB_MR2)
```

其他契约：

- `TimelineEvent`：`version: AndroidVersion`
- `ComponentProvider.Component`：`version` / `versions`
- `EasterEggGroup`：身份/排序基于成员 `id` / `versions`
- `EasterEgg.id` 默认 `version.id`；如需拆分/合并/改区间，可覆盖
- 未来的用户自定义彩蛋可使用 `user_` 前缀的字符串 ID，枚举只服务内置彩蛋

`Build.VERSION_CODES` 的保留边界：

- 设备能力判断：`Build.VERSION.SDK_INT`、`@RequiresApi`、`@ChecksSdkIntAtLeast`
- 不参与彩蛋身份/排序/搜索/统计
- 界面 “API X” 直接使用 `AndroidVersion.apiLevel` / `apiLevelLabel`，不再从 `Build.VERSION_CODES` 反推

## 4. 分阶段 TODO

### P0：设计定稿与全量清单

- [ ] 确定枚举放在 `:core:provider`，纯 Kotlin，不引用 Android API
- [ ] 定义 `AndroidVersion` 字段、命名、append-only ID 规则
- [ ] 建立完整清单：每个彩蛋的 `version`、`additionalVersions`、`id`
- [ ] 确定 `sortOrder`：预览/beta 放在稳定版之前，12L/16.1/17.1 放在稳定版之后
- [ ] 对照当前列表顺序，确定 `EasterEggGroup` 用最小还是最大成员排序
- [ ] 确定公开 API 歧义策略：API 33 优先解析到稳定版 Android 13，而不是 T Beta
- [ ] 确定 “API X” 显示规则：是否显示 `36.1`、范围是否显示 `31–32`
- [ ] 明确 `Build.VERSION_CODES` 保留边界，避免误删设备能力判断
- [ ] 建立 legacy 映射表：旧 `(fullApiLevelFirst, last)` → 新 `id`

### P1：枚举与兼容层

- [ ] 新增 `core/provider/src/main/java/com/dede/basic/provider/AndroidVersion.kt`
- [ ] 新增 `LegacyApiLevel` 兼容层，集中旧的乘法、预览减一逻辑，标记 `@Deprecated`
- [ ] `EasterEgg` 增加 `version` / `additionalVersions`，默认从旧 full range 推导
- [ ] `EasterEgg.id` 默认取 `version.id`
- [ ] `TimelineEvent` / `Component` 增加 `version` / `versions`
- [ ] 校验：`versions` 非空、按 `sortOrder` 升序、`id` 唯一、`sortOrder` 唯一
- [ ] 此阶段所有旧 provider 仍可编译

### P2：契约层切换身份

- [ ] `EasterEgg.equals/hashCode` 改为基于 `id`
- [ ] `EasterEggGroup` 身份/排序改为成员 `id` / `versions`
- [ ] `BaseEasterEgg` 增加 `id` / `versions` / `primaryVersion`
- [ ] `fullApiLevelRange` 标记 `@Deprecated`，仅作显示/过渡
- [ ] `TimelineEvent.fullApiLevel` 替换为 `version`
- [ ] `Component.fullApiLevelRange` 替换为 `version` / `versions`
- [ ] `toRange` / `toApiLevelRange` / `toFullApiLevel` 标记 deprecated

### P3：上层消费方切换

- [ ] `EasterEggModules` 排序改用 `sortOrder`
- [ ] `lazyItemKey` 改用 `id`
- [ ] `EasterEggItems`、`EasterEggGroupSelector`、`EasterEggList`、`EasterEggSearchList`
- [ ] `EasterEggHelp` 的 `VersionFormatter` / `ApiLevelFormatter` 改用 `versions`
- [ ] 删除 `EasterEggHelp.apiLevelArrays`，版本名/API 映射移到枚举
- [ ] `filterEasterEggs` 改用枚举的 `versionName` / `apiLevel`，去掉 `toApiLevelRange`
- [ ] `AndroidLogoMatcher`、`AndroidReleaseDateMatcher`、`TimelineEventHelp`、`TimelineListDialog`
- [ ] `SnapshotPref`、`ComponentManagerPref`、`EggStatsDialog`
- [ ] `EasterEggShortcutsHelp` 的 ID、rank、extra 改用 `egg.id`
- [ ] `EggActionHelp`、`IntentHandler`、`EggActivityAction`
- [ ] `EasterEggAppFunctionService`：对外仍用公开 API level，内部通过枚举查找
- [ ] 深链 `egg://easter_egg/api/<level>` 对外不变，内部用统一查找函数

### P4：蛋模块迁移

- [ ] `eggs/Base` 的 `BaseEasterEgg` 辅助类
- [ ] Base 的 6 个彩蛋（1.0、1.1、1.5、1.6、2.0/2.1、2.2）
- [ ] Java 老蛋：Gingerbread、Honeycomb、ICS、JellyBean、KitKat、Lollipop、Marshmallow、Nougat、Oreo、Pie、Q、R、S、Tiramisu
- [ ] Kotlin 新蛋：U、V、Baklava、CinnamonBun、AndroidNext
- [ ] 各模块的 `TimelineEvent` / `ComponentProvider.Component`
- [ ] `UniverseProgressNotifier` 的 `SDK_INT_FULL` 不动，它不是彩蛋身份
- [ ] 对比迁移前后的列表顺序、分组、版本名、API 标签

### P5：统计数据与快捷方式迁移

- [ ] 统计表 v1 → v2：新增 `egg_key`，用 legacy 映射把旧 range 转成新 `id`
- [ ] `EggStatsDatabase` / `EggStatsRepository` / DAO / UI 改用 `egg_key`
- [ ] 更新统计表注释：身份从 full range 改为 `egg_key`
- [ ] 快捷方式新增 enum ID extra；旧 full range extra 保留一个版本兼容
- [ ] 动态快捷方式自动刷新；固定快捷方式准备兼容回退
- [ ] 深链、AppFunctions、小部件对外 API 不变
- [ ] 升级测试：旧统计 DB、旧快捷方式、旧深链

### P6：清理旧模型

- [ ] 删除 `EasterEgg.VERSION_CODES_FULL`
- [ ] 删除 `SDK_INT_MULTIPLIER`、`toApiLevel()`、`toFullApiLevel()`
- [ ] 删除 `fullApiLevelRange` / `fullApiLevel`（或保留一个版本 deprecated）
- [ ] 删除 `EasterEggHelp.apiLevelArrays`
- [ ] 检查 `Build.VERSION_CODES_FULL`：只允许出现在迁移兼容层
- [ ] 检查 `Build.VERSION_CODES`：只允许设备能力检查和 API 标签显示
- [ ] 更新 `AGENTS.md`、模块注释、统计表注释
- [ ] 全量编译 + lint

### P7：验证与回归

- [ ] 枚举校验脚本/测试：ID 唯一、sortOrder 唯一、覆盖所有公开 API
- [ ] golden 列表：迁移前后 `EasterEggModules` 输出的 id 顺序完全一致
- [ ] 搜索：版本名、`12L`、`16.1`、API 数字、昵称、乱序关键词
- [ ] 分组切换、子彩蛋统计互不串数据
- [ ] 时间线顺序、logo、发布日期
- [ ] 组件管理顺序、标签
- [ ] 统计面板：首次/最近/次数、清除、进程重启
- [ ] 快捷方式：新建、旧版、固定、桌面启动
- [ ] 深链、AppFunctions、模拟时钟小部件
- [ ] API 标签：普通版本、12L、16.1、17.1、preview、T Beta
- [ ] 备份/恢复、升级迁移
- [ ] 中英文/RTL/深色/平板宽度
- [ ] `app:assembleFossProductDebug` + 相关模块编译

## 5. 兼容与迁移

### 统计数据库

当前 v1：

```sql
egg_play_events(
  id, api_level_first, api_level_last, played_at, source
)
```

重构后 v2：

```sql
egg_play_events(
  id, egg_key, played_at, source
)
```

迁移方式：

1. 新增 `egg_key TEXT NOT NULL`
2. 用 legacy 映射表把 `(api_level_first, api_level_last)` 转成 `EasterEgg.id`
3. 删除旧两列/重建表
4. 无匹配的旧 key 保留 `legacy_<first>_<last>` 兜底，避免丢数据

### 快捷方式

- 新 extra：`EXTRA_EGG_ID`（enum ID）
- 旧 extra：`EXTRA_EGG_API_LEVEL_FIRST/LAST` 保留一个版本，读到旧值时走 legacy 映射
- 动态快捷方式启动时刷新；固定快捷方式可能有旧 intent，需要兼容路径

### 深链 / AppFunctions / 小部件

- 对外仍使用公开 API level（`egg://easter_egg/api/34`）
- 内部用 `findEggByPublicApiLevel(level)`：
  - 遍历 `egg.versions` 中 `apiLevel == level` 的条目
  - 同 level 多个彩蛋时稳定版优先（33 → Android 13，而不是 T Beta）
  - 再按 `sortOrder` 取最新
- AppFunctions 的 `apiLevel` 入参与返回值保持公开 API 不变

### Java provider 构造器

- 迁移期间保留旧的 `fullApiLevelRange` 构造器并标记 `@Deprecated`
- 逐步替换为 `version` / `additionalVersions`
- 每一步保持模块可编译，避免一次性大爆炸

## 6. 风险

- `equals/hashCode` 和 `lazyItemKey` 换身份会影响 Compose 的 key、状态恢复和分组选中状态，需要专门回归
- 公开 API 存在歧义（T Beta 与 Android 13 都是 33），必须在查找函数里固定“稳定版优先”策略
- `sortOrder` 必须精确复刻当前列表顺序，尤其是预览/beta 和 16.1/17.1
- 统计表、快捷方式 extra 都有存量格式，迁移要一次做完整，避免半切换
- Java provider 使用位置参数构造，直接改签名会牵连大量文件；建议分阶段保留旧构造器
- 不要把设备能力判断（`SDK_INT`、`@RequiresApi`、通知特性判断）误迁到彩蛋枚举里
- 未来用户自定义彩蛋需要动态 ID，当前设计预留字符串 ID 前缀，但不在本次范围

## 7. 预估

- P0 设计/清单：0.5 天
- P1 枚举/兼容层：0.5–1 天
- P2 契约：0.5 天
- P3 上层消费方：1 天
- P4 蛋模块：1–1.5 天
- P5 数据迁移：0.5–1 天
- P6 清理 + P7 验证：1 天

合计约 **4–6 人日**，其中 P4 的文件数量最多，P5 的迁移风险最高。

## 8. 待确认

- 枚举命名：`AndroidVersion` 还是 `EggVersion` / `EasterEggVersion`
- `sortOrder` 用显式 Int，还是声明顺序（推荐显式 Int）
- “API X” 是否展示小数 QPR（`36.1` / `37.1`）
- 区间彩蛋的 `EasterEggGroup` 排序用最小还是最大成员
- 统计表迁移是否随本次一起发布，还是先保留旧列一个版本

## 9. 参考文件

- `core/provider/src/main/java/com/dede/basic/provider/EasterEggProvider.kt`
- `core/provider/src/main/java/com/dede/basic/provider/ComponentProvider.kt`
- `core/provider/src/main/java/com/dede/basic/provider/TimelineEvent.kt`
- `app/src/main/java/com/dede/android_eggs/views/main/util/EasterEggHelp.kt`
- `app/src/main/java/com/dede/android_eggs/views/main/util/AndroidReleaseDateMatcher.kt`
- `app/src/main/java/com/dede/android_eggs/views/main/util/AndroidLogoMatcher.kt`
- `app/src/main/java/com/dede/android_eggs/views/main/compose/EasterEggSearchList.kt`
- `app/src/main/java/com/dede/android_eggs/appfunctions/EasterEggAppFunctionService.kt`
- `core/egg-stats/src/main/java/com/dede/android_eggs/egg_stats/`
- `core/shortcut/src/main/java/com/dede/android_eggs/views/main/util/EasterEggShortcutsHelp.kt`
- `core/activity-actions/src/main/java/com/dede/android_eggs/util/actions/EggActivityAction.kt`
- `eggs/Base/src/main/java/com/android_b/egg/BaseEasterEgg.kt`
- `eggs/*/src/main/java/**/*EasterEgg*`
