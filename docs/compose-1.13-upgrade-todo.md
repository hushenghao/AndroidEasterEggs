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
| `feature/cat-editor/.../CatEditorScreen.kt` | :167 | 缩放值格式化字符串 |
| `app/.../ui/composes/PredictiveBackProgressHandler.kt` | :116 | 单纯读 `progress` |
| `core/composable/.../ScrollableModalBottomSheet.kt` | :56 | `canScrollBackward != true` |

> 附：1.13 中 `Grid` 已转正、`ExperimentalGridApi` 注解被移除，`app/.../views/settings/compose/prefs/IconShapePref.kt` 已去掉该 OptIn/import 以通过编译。

## 二、后续优化（✅ 已评估完毕 2026-11-20）

### 1. `PolygonShape` / `MorphPolygonShape` 简化 icon-shape —— 无需改

`:core:icon-shape` 已通过 Material3 的 `RoundedPolygon.toShape()`（`IconShapePrefUtil.kt:31`）转换，不存在博客所说的「手写 `RoundedPolygon`→`Path`→`Shape`」；`PathShape` 只服务系统图标遮罩（Path 而非多边形）。`MorphPolygonShape` 属新增形变动画功能、非简化，且 `shapeSpecs` 是 **append-only** 索引，不为微优化改动。

### 2. `CapturedAnimatedVisibility` —— ✅ 已完成（2 处）

仅替换「退出动画期间内容依赖被清空状态」的两处：`BottomSearchBar.kt`（`close()` 立即清空 `text`）、`CatSeedInputDialog.kt`（supporting 文本依赖 `inputSeedText`）。其余 `AnimatedVisibility` 的退出内容仍存活（bitmap/选项/轮播/静态文本）或属 AOSP 派生彩蛋，保持原样。

### 3. `BottomSearchBar` → 状态化 `TextFieldState` —— 暂缓

M3 value-based `TextField` 在 1.5.0 **未弃用**；单字段收益小，且会动到 `BottomSearchBarState` 的 Saver 与游标管理。等官方正式弃用 value-based 重载后再迁。

### 4. `FlexBox` 替换 `FlowRow` —— 不做

FlowRow 未弃用（官方只建议**新代码**用 FlexBox）；FlexBox 是 `FlexBoxConfig { columnGap/rowGap }` DSL，与 FlowRow 参数异构，现有 2 处也无 `maxItemsInEachLine` 需求。纯「将来式」迁移，零行为收益。

### 5. `LazyLayoutCacheWindow` 窗口级预取 —— 已自动生效

1.13 的 `LazyColumn`/`LazyVerticalStaggeredGrid` 默认 `cacheWindow = LazyListDefaults.cacheWindow(state)`，即动态窗口级预取（替代旧 item 级），彩蛋列表用默认值已自动受益。仅在需要按 benchmark 调 ahead/behind 窗口时才需自定义。

### 6. `LineHeightStyle.Mode.PerLine` 多语言行高 —— 不做

该模式针对「同一段落内混合字号/多语言混排」导致高字身文字撞行；彩蛋列表文本均为单一字号、单一段落（蛋名 `headlineSmall`、版本 `bodyMedium`、chip `labelMedium`），无撞行场景。

## 三、免费/自动收益（无需改代码）

- `VectorPainter` 静态图标缓存：默认开启，`core/icons` 图标复用 `ImageVector` 实例别重建即可最大化收益。
- `Modifier.border` 更快 + 全形状类型支持 RuntimeShader。
- 嵌套滚动速度计算修复：Compose 嵌套在 View 滚动容器里快速滚动速度算错的 bug。

## 四、行为变化须知（本项目无落点）

- `Modifier.onFocusedBoundsChanged` 实现已移除 —— 本项目 0 处使用，无影响。
- `Modifier.clickable` 自动播放系统音效 —— 已确认符合预期。
- ConstraintLayout / MotionLayout 弃用 —— 本项目已无残留。
- Material 2 弃用 —— 本项目已是 M3。

## 五、工具 / CI

- Compose 性能静态分析 skill（`android/skills` 的 `jetpack-compose-performance`）：已对主界面 + 核心 UI 模块跑过审计，**未发现显著可行动问题**（懒布局 `key`/`contentType`、`collectAsStateWithLifecycle`、无 `Modifier.composed`/热路径 `onGloballyPositioned` 等均合规）。
- `TestFailurePolicy`：不适用（`baselineprofile` 冒烟测试为 `UiAutomator` 跨进程黑盒测试，无 `ComposeTestRule` 与进程内语义树；项目当前也无进程内 Compose UI 测试）。
- **诊断堆栈跟踪（Diagnostic Stack Traces）**：已在 `ApplicationInitializer.create` 启用 `Composer.setDiagnosticStackTraceMode(ComposeStackTraceMode.Auto)`。在发生崩溃时会自动附加包含 `@Composable` 组件调用树的堆栈信息；线上混淆包使用 `GroupKeys` 模式配合 R8 映射表还原，未混淆包无额外开销。

## 六、渐进式模糊 / 背景模糊 vs Haze（评估结论：Haze 保留）

1.13 的图形特性里有两类「模糊」，与项目现有的 Haze 磨砂不是一回事，**替代不了 Haze**：

- **`Modifier.blur { blurRadiusSpec = ... }`（渐进式模糊）**：**自模糊**——只模糊它自己那块内容（图边渐隐、径向聚光），不采样背后内容；且仅 API 31+ 生效（`Blur.kt` 注释），项目 `minSdk 24` 兜不住。
- **`DialogProperties.blurBehindRadius` / `backgroundBlurRadius`**：**跨窗口**背景模糊（Dialog/Popup 窗口级），而项目磨砂是窗口内（app bar + overlay host），且历史上已刻意放弃 Dialog 方案（入场闪动）、否决 popup 模糊。

项目两处 Haze（app bar 磨砂、overlay 全屏磨砂）都是「采样背后内容」，1.13 上述能力覆盖不了，**Haze 保留**。卡片底边/背景渐隐已通过 `EasterEggItems.kt` 中的 `drawWithContent` + `Brush.horizontalGradient` + `alpha` + `blur` 方案实现，无需再引入 1.13 渐进式模糊。

## 暂不相关

`WebContent`/`WebSurface`（本项目用 Custom Tabs 不用 WebView）、A2UI 渲染器、Styles（仍实验且文档已撤）、`DeferredTransition`。
