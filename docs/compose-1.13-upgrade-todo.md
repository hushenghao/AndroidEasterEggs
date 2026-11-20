# Compose 1.13 升级与优化清单

> 依据 [Jetpack Compose November '26 release](https://developer.android.com/blog/posts/jetpack-compose-november-2026-release) 梳理。
> 项目现状：BOM `2026.11.01`、Kotlin `2.4.21`、compileSdk `37.2` —— 升级前置条件（Kotlin ≥ 2.2、compileSdk ≥ 37.1）已全部满足。

## 一、一次性小升级（✅ 已完成 2026-11-20）

### 1. Compose BOM 2026.09.00 → 2026.11.01

- 位置：`gradle/libs.versions.toml` 的 `compose-bom`
- ✅ 已完成：`2026.11.01` 已于 2026-11-20 发布到 Google Maven（`runtime`/`ui` `1.13.0`、`material3` `1.5.0` 均可解析）。
- 验证命令：`./gradlew app:compileFossProductDebugKotlin`

### 2. `computedStateOf` 替换 `derivedStateOf`（✅ 已完成）

`computedStateOf` 是 1.13 新 API，BOM 升级后才存在。官方建议：轻量条件/过滤状态用 `computedStateOf`（不缓存、开销更小），`derivedStateOf` 只留给重计算/需缓存场景。项目里 4 处全是轻量场景，直接换函数名 + import 即可：

| 文件 | 位置 | 内容 |
|------|------|------|
| `feature/cat-editor/.../CatEditor.kt` | :152 | `selectedPart != UNSELECTED_PART` 条件判断 |
| `feature/cat-editor/.../CatEditorScreen.kt` | :427 | 缩放值格式化字符串 |
| `app/.../ui/composes/PredictiveBackProgressHandler.kt` | :116 | 单纯读 `progress` |
| `core/composable/.../ScrollableModalBottomSheet.kt` | :56 | `canScrollBackward != true` |

> 附：1.13 中 `Grid` 已转正、`ExperimentalGridApi` 注解被移除，`app/.../views/settings/compose/prefs/IconShapePref.kt` 已去掉该 OptIn/import 以通过编译。

## 二、后续优化（按优先级，BOM 升级后逐个评估）

### 1. `PolygonShape` / `MorphPolygonShape` 简化 icon-shape（中）

`:core:icon-shape` 现在直接用 `graphics-shapes` 手写 `RoundedPolygon`→`Path`→`Shape`（`IconShapePrefUtil.kt`、`Utilities.kt`）。1.13 的 `PolygonShape` 是原生 `Shape`（自动适配布局尺寸），`MorphPolygonShape` 绘制期无重组形变，可用于图标形状预览动画。
⚠️ `shapeSpecs` 是 **append-only** 索引存储，改动时严禁插入/重排。

### 2. `CapturedAnimatedVisibility` 排查面板退出动画（中）

13 个源文件用了 `AnimatedVisibility`。凡是「退出动画期间内容依赖已置空状态」（选中项清 null 的面板）的场景都值得换，可消除空白/闪烁、退出快 7–8%。候选集中在 `CatRememberBottomSheet.kt`、`CatEditorScreen.kt`、`ExpandOptionsPref.kt`、`SnapshotPref.kt`、`BottomSearchBar.kt` 等。

### 3. `BottomSearchBar` 迁移到状态化 `TextFieldState`（中）

`BottomSearchBar.kt:78` 现为 value-based `TextFieldValue`。1.13 已弃用 value-based `BasicTextField`，状态化 `TextFieldState` 同步状态、性能更好、自带撤销/重做。

### 4. `FlexBox` 替换 `FlowRow`（低）

现有 2 处 `FlowRow`：`EasterEggItems.kt:239`、`EggStatsDialog.kt:155`。FlexBox 已转正且新增 `maxItemsInEachLine`（每行最多 N 个）。FlowRow 未删，可只在新代码用。

### 5. `LazyLayoutCacheWindow` 彩蛋列表窗口级预取（低）

首页列表用 `LazyColumn`/`StaggeredGrid`/`Grid` 三种布局。窗口级预取（cache window 参数从 scroll state 挪到布局组件）对离屏组合有收益。

### 6. `LineHeightStyle.Mode.PerLine` 多语言行高（低）

项目 36 个语言目录，缅/高棉/泰文高字身文字混排会撞行。多语言文本场景可按需引入（当前无 `LineHeightStyle` 用法）。

## 三、免费/自动收益（无需改代码）

- `VectorPainter` 静态图标缓存：默认开启，`core/icons` 图标复用 `ImageVector` 实例别重建即可最大化收益。
- `Modifier.border` 更快 + 全形状类型支持 RuntimeShader。
- 嵌套滚动速度计算修复：Compose 嵌套在 View 滚动容器里快速滚动速度算错的 bug。

## 四、行为变化须知（本项目无落点）

- `Modifier.onFocusedBoundsChanged` 实现已移除 —— 本项目 0 处使用，无影响。
- `Modifier.clickable` 自动播放系统音效 —— 全局行为变化，需确认符合预期。
- ConstraintLayout / MotionLayout 弃用 —— 本项目已无残留。
- Material 2 弃用 —— 本项目已是 M3。

## 五、工具 / CI

- Compose 性能静态分析 skill（`android/skills` 的 `jetpack-compose-performance`，经 Android CLI 跑）。
- `TestFailurePolicy`：`baselineprofile` 冒烟测试失败时自动导出截图 + 语义树。

## 暂不相关

`WebContent`/`WebSurface`（本项目用 Custom Tabs 不用 WebView）、A2UI 渲染器、Styles（仍实验且文档已撤）、`DeferredTransition`。
