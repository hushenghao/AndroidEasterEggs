# AndroidEasterEggs Project Map

This document is for AI coding tools. Use it to locate code, choose the right
module, and run the smallest useful verification command.

## Repository Rules

- Keep commit message style consistent with the existing project history.
- Do not revert unrelated working tree changes.
- Prefer focused fixes in the module or method named by the task.
- Use `rg` / `rg --files` for code search.
- For Android changes, verify with the most relevant Gradle module task when possible.

## Maintenance

- Treat source code and Gradle configuration as the source of truth when this
  document conflicts with the project.
- Update this document whenever modules are added, removed, renamed, or their
  responsibilities change.
- Keep this document focused on stable structure, ownership, search targets, and
  verification commands.
- Do not record temporary implementation details, one-off bug context, or
  volatile internal method behavior here.

## Project Shape

This is a multi-module Android project using Kotlin DSL Gradle files.

- Root project name: `Easter Eggs`
- Main application module: `:app`
- Shared Android/Kotlin modules: `:core:*`, `:basic`, `:jvm-basic`
- Feature modules: `:feature:*`
- Android Easter egg implementation modules: `:eggs:*`
- Build convention plugins: `build-logic`
- Utility script modules and scripts: `:script:*`, `script/`

Primary module registration lives in `settings.gradle.kts`.
Dependency versions and plugin aliases live in `gradle/libs.versions.toml`.

### Plugin Inheritance Hierarchy

Convention plugins build on each other in a tiered stack. Each tier
automatically injects certain dependencies:

```
easter.eggs.basic.library     →  :jvm-basic (api)
 ├─ easter.eggs.library       →  :basic (implementation) + Hilt + lintBaseline
 │    └─ easter.eggs.compose.library  →  all of above + Compose BOM/Foundation/UI
 └─ easter.eggs.app           →  all library + Compose + signing + packaging
```

| Plugin ID                      | Module Type | Hilt | Compose | Lint Baseline | Used By |
|-------------------------------|-------------|------|---------|---------------|---------|
| `easter.eggs.basic.library`   | LIBRARY     | No   | No      | No            | `:basic` |
| `easter.eggs.library`         | LIBRARY     | Yes  | No      | Yes           | Older `:eggs:*` (Base–Tiramisu), `:core:custom-tab-browser`, `:core:resources`, `:core:shortcut` |
| `easter.eggs.compose.library` | LIBRARY     | Yes  | Yes     | Yes           | All `:core:*` (except above 3), `:feature:*`, newer `:eggs:*` (UpsideDownCake+) |
| `easter.eggs.app`             | APP         | Yes  | Yes     | No            | `:app` only |

Auto-injected dependencies (no explicit declaration needed in module build file):
- APP and LIBRARY modules → `:basic` (implementation)
- BASIC modules → `:jvm-basic` (api)
- Hilt-enabled → Hilt runtime + compiler (ksp)
- Compose-enabled → Compose BOM + `foundation` + `ui`
- Lint-baseline-enabled → `lint-baseline.xml` in module root

## Version & Environment Constants

| Constant     | Value  | Defined In                                              |
|-------------|--------|---------------------------------------------------------|
| compileSdk  | 37     | `build-logic/convention/src/main/kotlin/Versions.kt`   |
| targetSdk   | 37     | `build-logic/convention/src/main/kotlin/Versions.kt`   |
| minSdk      | 23     | `build-logic/convention/src/main/kotlin/Versions.kt`   |
| buildTools  | 37.0.0 | `build-logic/convention/src/main/kotlin/Versions.kt`   |
| Java        | 17     | `build-logic/convention/src/main/kotlin/Versions.kt`   |
| Kotlin      | 2.4.0  | `gradle/libs.versions.toml`                             |
| AGP         | 9.2.1  | `gradle/libs.versions.toml`                             |
| Hilt        | 2.59.2 | `gradle/libs.versions.toml`                             |
| Compose BOM | 2026.06.00 | `gradle/libs.versions.toml`                         |
| applicationId | `com.dede.android_eggs` | `app/build.gradle.kts`                      |
| versionName | 5.0.1  | `app/build.gradle.kts`                                  |
| versionCode | 77     | `app/build.gradle.kts`                                  |

## Build System

### Build Files

- `build.gradle.kts` — declares top-level plugin aliases, applies `kotlin-gradle-plugin` in buildscript.
- `settings.gradle.kts` — includes all Gradle modules and the `build-logic` included build.
- `gradle/libs.versions.toml` — centralized version catalog for dependencies and plugins.
- `gradle.properties` — JVM args, parallel, configuration-cache, `android.useAndroidX=true`,
  `kotlin.code.style=official`, `android.nonTransitiveRClass=true`.

### Key Gradle Properties

| Property                     | Default | Purpose |
|------------------------------|---------|---------|
| `eggs.androidNext.enable`    | `false` | Conditionally include `:eggs:AndroidNext` module |
| `org.gradle.parallel`        | `true`  | Parallel project execution |
| `org.gradle.configuration-cache` | `true` | Enable configuration cache |
| `android.nonTransitiveRClass` | `true` | Non-transitive R class generation |
| `android.nonFinalResIds`     | `true`  | Non-final resource IDs (legacy eggs compatibility) |
| `kotlin.code.style`          | `official` | Kotlin coding style |

### Product Flavors

Two dimensions: `app` and `track`:

| Flavor   | Dimension | Description |
|----------|-----------|-------------|
| `foss`   | `app`     | FOSS variant |
| `market` | `app`     | Google Play variant (enabled only when track=`product`) |
| `alpha`  | `track`   | Alpha track, versionNameSuffix=`-alpha` |
| `beta`   | `track`   | Beta track, versionNameSuffix=`-beta01` |
| `product` | `track`  | Production track |

Market-specific dependencies use the `marketImplementation()` extension function:
```kotlin
marketImplementation(libs.google.play.review)
marketImplementation(libs.google.play.update)
```

Lint configuration:
- `NewApi` and `InlinedApi` are treated as **fatal** (build-breaking).
- Lint baselines exist per module (in modules using `easter.eggs.library` or `easter.eggs.compose.library`).

Common verification commands:

```sh
./gradlew app:compileFossProductDebugKotlin
./gradlew app:assembleFossProductRelease
./gradlew :feature:analog-clock-widget:compileDebugKotlin
./gradlew :feature:neko-controls-widget:compileDebugKotlin
./gradlew :core:local-provider:compileDebugKotlin
```

Prefer the smallest module-specific compile/test command that covers the change.

## Commit Conventions

**AI Submission Identifier**: AI-generated code commits should infer identity from runtime context first (`copilot` / `codex` / `opencode` / `gemini` / `claude`), then fall back to `opencode`. Only override per commit (never global config).

```sh
AI_COMMIT_TOOL="<infer-from-runtime-context>" # copilot | codex | opencode | gemini | claude
case "$AI_COMMIT_TOOL" in
  copilot|codex|opencode|gemini|claude) AI_INFERRED_NAME="$AI_COMMIT_TOOL" ;;
  *) AI_INFERRED_NAME="opencode" ;;
esac
AI_INFERRED_EMAIL="${AI_INFERRED_NAME}[bot]@users.noreply.github.com"

git -c user.name="$AI_INFERRED_NAME" \
    -c user.email="$AI_INFERRED_EMAIL" \
    commit -m "xxx"
```

The project uses **Conventional Commits** without scope parentheses. PR references
appear as `(#NNN)` at the end.

Observed types: `feat`, `fix`, `chore`, `build`, `ci`, `del`.

Examples:
```
feat: New translations from Crowdin (#896)
fix: CinnamonBun notification not dismissed on exit (#883)
chore: Update dependencies
build: Bump gradle-wrapper from 9.5.1 to 9.6.0 (#890)
ci: Bump actions/checkout from 6 to 7 (#894)
del: Remove easter egg log script
```

Do not invent new types. Match the existing style exactly.

## Coding Conventions

### Kotlin / Java

- Follow the **Kotlin official code style** (`kotlin.code.style=official` in `gradle.properties`).
- **4-space indentation.**
- **UTF-8** source files.
- **K&R bracing** (opening brace on same line as `if`, `for`, `while`, etc.).
- **Braces always required** after `if`, `for`, `while` — never omit single-statement braces.
- **One statement per line.**
- **Import order**: static imports first, then non-static grouped by package prefix:
  `android`, `androidx`, `com`, `junit`, `net`, `org`, `java`, `javax`, all others.
  (Tools → Code Style → Java/Kotlin → Imports in Android Studio.)
- **No `.editorconfig`** exists; use IntelliJ's per-project settings in `.idea/codeStyles/`.
- **No ktlint / detekt / spotless** configured; rely on IDE formatting (Ctrl+Alt+L) and lint baselines.

### Resources

- Resource files: **UTF-8**, XML well-formed.
- Egg modules use **resource prefixes** to avoid cross-module collisions
  (see Easter Egg Modules table below).
- Widget XML (RemoteViews) must use concrete resource values, not Compose theme
  references.
- Localized strings go in `core/resources` unless feature-specific.

### Documentation & Comments

- Write comments in **English**.
- Do not commit secrets, keys, or tokens.
- Do not add comments unless they convey non-obvious intent.

### AOSP-derived Code

- Preserve original package structure and behavior.
- Only modify when necessary for compatibility (minSdk, API changes).

## App Module

Path: `app/`

### Purpose

- Android application entry point.
- Wires together core modules, feature modules, and all `eggs` modules.
- Defines application id `com.dede.android_eggs`.

### Module Dependency List

The `:app` module depends on **every** `:core:*`, `:feature:*`, and `:eggs:*` module
(excluding optional `:eggs:AndroidNext`, which is conditionally added).

### Flavor-specific Source Sets

| Source Set           | Purpose |
|---------------------|---------|
| `app/src/main/`     | Shared app code |
| `app/src/foss/`     | FOSS variant overrides |
| `app/src/market/`   | Market variant overrides (Play Review/Update APIs) |
| `app/src/debug/`    | Debug-only code (LeakCanary, Compose tooling) |
| `app/src/androidTest/` | Instrumentation tests |

### Important Paths

- `app/src/main/AndroidManifest.xml` — activities, activity-aliases, providers, receivers.
- `app/src/main/java/com/dede/android_eggs/views/main/` — main app navigation and home flow.
  - `EasterEggsNavHost.kt` — top-level NavHost wiring.
  - `MainActivity.kt` — single-activity entry point.
- `app/src/main/java/com/dede/android_eggs/views/settings/` — settings UI and preferences.
  - `compose/prefs/AppIconPref.kt` — adaptive icon switching preference.
- `app/src/main/res/` — app resources, launcher icons (`mipmap-*`).
- `app/proguard-rules.pro` — release ProGuard/R8 rules.

## Core Modules

Path group: `core/`

All core modules use `easter.eggs.compose.library` except where noted.

| Module                            | Plugin                   | Namespace                                           | Purpose |
|-----------------------------------|--------------------------|-----------------------------------------------------|---------|
| `:core:activity-actions`          | compose.library          | `com.dede.android_eggs.activity_actions`            | Action definitions & launch helpers for Easter egg activities. Depends on ~12 `:eggs:*` modules. |
| `:core:alterable-adaptive-icon`   | compose.library          | `com.dede.android_eggs.alterable_adaptive_icon`     | Adaptive launcher icon switching with Compose UI preview. |
| `:core:composable`                | compose.library          | `com.dede.android_eggs.composable`                  | Shared Compose UI utilities and reusable composables. |
| `:core:custom-tab-browser`        | library (non-Compose)    | `com.dede.android_eggs.browser`                     | AndroidX Custom Tabs browser integration. Keep this module browser-focused; do not add Compose UI here. |
| `:core:icons`                     | compose.library          | `com.dede.android_eggs.ui.composes.icons`           | Compose Material icon assets and wrappers. |
| `:core:local-provider`            | compose.library          | `com.dede.android_eggs.local_provider`              | CompositionLocal providers and URI handling (when Compose-dependent). |
| `:core:navigation`                | compose.library          | `com.dede.android_eggs.navigation`                  | Navigation3 route definitions and contracts. Uses `kotlinx.serialization`. |
| `:core:resources`                 | library (non-Compose)    | `com.dede.android_eggs.resources`                   | Shared strings, drawables, XML resources, localized values. |
| `:core:settings`                  | compose.library          | `com.dede.android_eggs.settings`                    | Settings storage and setting-related shared APIs. |
| `:core:shortcut`                  | library (non-Compose)    | `com.dede.android_eggs.shortcut`                    | Launcher shortcut and app-icon shortcut support. |
| `:core:theme`                     | compose.library          | `com.dede.android_eggs.views.theme`                 | Material 3 theme, colors, typography, theme resources. |

### Dependency Rules for Core

- **Do not** add circular dependencies between core modules.
- **`custom-tab-browser`** is the only non-Compose core module with browser responsibility.
  Do not add Compose UI dependents to it.
- **`local-provider`** owns Compose provider logic and URI handling that needs Compose.
- **`resources`** owns shared non-code Android resources; avoid putting strings here
  that should be feature-local.

## Feature Modules

Path group: `feature/`

All feature modules use `easter.eggs.compose.library`.

| Module                        | Namespace                                          | Key Dependencies         | Purpose |
|-------------------------------|---------------------------------------------------|--------------------------|---------|
| `:feature:analog-clock-widget` | `com.dede.android_eggs.views.widget`              | DataStore                | Analog clock app widget, widget configuration activity, XML metadata, preview resources. |
| `:feature:cat-editor`          | `com.dede.android_eggs.cat_editor`                | Room, Navigation3, Capturable | Neko cat editor UI with local Room database. Schemas exported to `feature/cat-editor/schemas/`. |
| `:feature:crash`               | `com.dede.android_eggs.crash`                     | Curtains, Startup        | Crash test / display feature. Initialized at app startup. |
| `:feature:embedding-splits`    | `com.dede.android_eggs.embedding_splits`          | AndroidX Window          | Large-screen / split embedding support. |
| `:feature:libraries-info`      | `com.dede.android_eggs.libraries_info`            | AboutLibraries compose-m3 | Open source libraries information UI, external link handling. |
| `:feature:neko-controls-widget` | `com.dede.android_eggs.neko_controls_widget`     | DataStore, Material       | Neko controls app widget with RemoteViews layouts and night-mode theming. |

### Key Paths for Common Tasks

- Analog clock widget:
  - `feature/analog-clock-widget/src/main/java/com/dede/android_eggs/views/widget/`
  - `feature/analog-clock-widget/src/main/res/xml/analog_clock_widget_info.xml`
  - `feature/analog-clock-widget/src/main/res/layout*/`
- Neko controls widget:
  - `feature/neko-controls-widget/src/main/java/`
  - `feature/neko-controls-widget/src/main/res/layout/`
  - `feature/neko-controls-widget/src/main/res/values/`
  - `feature/neko-controls-widget/src/main/res/values-night/`
  - `feature/neko-controls-widget/src/main/res/xml/`
- Cat editor:
  - `feature/cat-editor/src/main/java/`
  - `feature/cat-editor/schemas/` — Room schema exports for migration testing.

## Base Utility Modules

| Module      | Plugin                     | Namespace        | Purpose |
|-------------|----------------------------|------------------|---------|
| `:basic`    | `easter.eggs.basic.library` | `com.dede.basic` | Shared Android utility code. `api`-depends on `:jvm-basic`. Uses Okio, AppCompat, Lifecycle, ViewModel, Startup, Activity. |
| `:jvm-basic` | `java-library` + `kotlin.jvm` | (none)          | Shared JVM-only utility code. Used by script modules (e.g., `:script:emoji-svg-xml-convertor`). Java 17. |

## Easter Egg Modules

Path group: `eggs/`

These modules preserve or adapt Android platform Easter egg implementations.
Prefer minimal changes when editing AOSP-derived code.

### Module Reference Table

| Module                   | Android Version  | API   | Egg Name         | Plugin            | Resource Prefix   | Build File Type |
|--------------------------|-----------------|-------|------------------|-------------------|-------------------|-----------------|
| `:eggs:Gingerbread`      | 2.3             | 9–10  | —                | library           | `g_`              | Groovy `.gradle` |
| `:eggs:Honeycomb`        | 3.0–3.2         | 11–13 | —                | library           | `h_`              | Groovy `.gradle` |
| `:eggs:IceCreamSandwich` | 4.0             | 14–15 | Nyandroid        | library           | `i_`              | Groovy `.gradle` |
| `:eggs:JellyBean`        | 4.1–4.3         | 16–18 | BeanBag          | library           | `j_`              | Groovy `.gradle` |
| `:eggs:KitKat`           | 4.4–4.4W        | 19–20 | Dessert Case     | library           | `k_`              | Groovy `.gradle` |
| `:eggs:Lollipop`         | 5.0–5.1         | 21–22 | L Land           | library           | `l_`              | Groovy `.gradle` |
| `:eggs:Marshmallow`      | 6.0             | 23    | Marshmallow Land | library           | `m_`              | Groovy `.gradle` |
| `:eggs:Nougat`           | 7.0–7.1         | 24–25 | Neko             | library           | `n_`              | Groovy `.gradle` |
| `:eggs:Oreo`             | 8.0–8.1         | 26–27 | Octopus          | library           | `o_`              | Groovy `.gradle` |
| `:eggs:Pie`              | 9               | 28    | PAINT.APK        | library           | `p_`              | Groovy `.gradle` |
| `:eggs:Q`                | 10              | 29    | Icon Quiz        | library           | `q_`              | Groovy `.gradle` |
| `:eggs:R`                | 11              | 30    | Cat Controls     | library           | `r_`              | Groovy `.gradle` |
| `:eggs:S`                | 12–12L          | 31–32 | Paint Chips      | library           | `s_`              | Groovy `.gradle` |
| `:eggs:Tiramisu`         | 13              | 33    | Paint Chips      | library           | `t_`              | Groovy `.gradle` |
| `:eggs:UpsideDownCake`   | 14              | 34    | Landroid         | compose.library   | `u_`              | Kotlin `.gradle.kts` |
| `:eggs:VanillaIceCream`  | 15              | 35    | Landroid         | compose.library   | `v_`              | Kotlin `.gradle.kts` |
| `:eggs:Baklava`          | 16              | 36    | Landroid         | compose.library   | `baklava_`        | Kotlin `.gradle.kts` |
| `:eggs:CinnamonBun`      | (next)          | —     | —                | compose.library   | `cinnamon_bun_`   | Kotlin `.gradle.kts` |
| `:eggs:AndroidNext`      | (future)        | —     | —                | compose.library   | —                 | Kotlin `.gradle.kts` |
| `:eggs:Base`             | —               | —     | Shared base code | library           | `b_`              | Kotlin `.gradle.kts` |
| `:eggs:RocketLauncher`   | —               | —     | Legacy launcher  | library           | —                 | Kotlin `.gradle.kts` |

### Key Notes

- **Plugin threshold**: Gingerbread through Tiramisu use `easter.eggs.library` (non-Compose).
  UpsideDownCake and later use `easter.eggs.compose.library` (Compose). All older eggs
  use Groovy `build.gradle`; newer eggs use Kotlin `build.gradle.kts`.
- `:eggs:AndroidNext` is conditionally included only when `eggs.androidNext.enable=true`
  in `gradle.properties`. The check happens in `EasterEggsApp.kt`.
- `:eggs:RocketLauncher` contains launcher/dream-related legacy code used by
  older Easter eggs.
- `:eggs:Base` is a shared base module for egg-internal utilities.
- Some eggs have local `README.md` files with version-specific context (17 eggs have them;
  AndroidNext, Base, CinnamonBun, RocketLauncher do not).

## Build Logic

Path: `build-logic/convention/`

### Key Files

| File | Purpose |
|------|---------|
| `src/main/kotlin/Versions.kt` | compileSdk, minSdk, targetSdk, Java version constants |
| `src/main/kotlin/com/dede/android_eggs/plugins/AbsConfigurablePlugin.kt` | Base plugin class for all convention plugins |
| `src/main/kotlin/com/dede/android_eggs/plugins/EasterEggsApp.kt` | App plugin: signing, build types, packaging, AndroidNext conditional |
| `src/main/kotlin/com/dede/android_eggs/plugins/EasterEggsLibrary.kt` | Non-Compose library plugin |
| `src/main/kotlin/com/dede/android_eggs/plugins/EasterEggsComposeLibrary.kt` | Compose library plugin |
| `src/main/kotlin/com/dede/android_eggs/plugins/EasterEggsBasicLibrary.kt` | Basic library plugin for `:basic` |
| `src/main/kotlin/com/dede/android_eggs/dls/Dls.kt` | DSL extensions: `keyprops`, `libs`, `marketImplementation()`, `android<>` accessor |
| `src/main/kotlin/com/dede/android_eggs/tasks/UpdateChangelogsTask.kt` | Task that runs `python3 changelogs.py` to update fastlane changelogs |
| `src/main/kotlin/com/dede/android_eggs/tasks/UpdateModularizationGraphTask.kt` | Task that auto-generates a Mermaid modularization graph |

### Custom Gradle Tasks

- `updateChangelogs` — runs `script/changelogs/changelogs.py` to generate fastlane metadata
  from `CHANGELOG.md` and `CHANGELOG_zh.md`.
- `updateModularizationGraph` — generates a Mermaid dependency graph in `BUILD.md`,
  color-coded by module group.

## Scripts & Assets

### Gradle Script Modules

| Module | Type | Purpose |
|--------|------|---------|
| `:script:compose-material-icons-generator` | JVM Kotlin | Generates Compose Material icon code. Depends on Guava, KotlinPoet, XmlPull. |
| `:script:emoji-svg-xml-convertor` | JVM Kotlin | Converts emoji SVG files to Android drawable XML. Depends on `:jvm-basic`. |

### Python Scripts (in `script/`)

| Directory | Purpose |
|-----------|---------|
| `script/blurhash/` | BlurHash image placeholder generation (`multi_blurhash.py`) |
| `script/changelogs/` | Fastlane changelog generation from `CHANGELOG.md` and `CHANGELOG_zh.md` |
| `script/crowdin/` | Crowdin API v2 integration for translation progress SVG generation |
| `script/icons/` | (Deprecated) Material Design Icons font subsetting using FontTools |

### Other Assets

- `assets/` — Design, image, and shapeshifter source assets.
- `fastlane/` — Store metadata and release assets.
- `wiki/` — Git submodule for documentation and localized wiki pages (remote: `git@github.com:hushenghao/AndroidEasterEggs.wiki.git`).

## Dependency Management

### Version Catalog

`gradle/libs.versions.toml` is the single source of truth for all dependency versions.

**Version groups:**
- `kotlin`, `ksp`, `agp` — core toolchain
- `hilt` — DI framework
- `compose-bom` — Compose UI versions (managed by BOM)
- `accompanist` — Compose accompanist utilities
- `lifecycle`, `activity` — AndroidX lifecycle/activity
- `room` — Room database
- `about-libraries` — OSS license UI
- `ktor` — HTTP client
- `nav3` — Navigation3

### Adding Dependencies

1. Add the version/library to `gradle/libs.versions.toml` if not already present.
2. Reference via `libs.*` in Kotlin DSL build files.
3. For flavor-specific dependencies, use the `marketImplementation()` extension:
   ```kotlin
   marketImplementation(libs.google.play.review)
   ```
   This is equivalent to `add("marketImplementation", dependency)`.
4. Module dependency order in `app/build.gradle.kts`:
   - `:core:*` modules first, then `:feature:*`, then `:eggs:*`.

### Key Libraries by Layer

| Layer | Libraries |
|-------|-----------|
| DI | Hilt (dagger) |
| UI | Compose BOM, Material3, Navigation3, Konfetti, Squircle shapes |
| Data | Room, DataStore, Ktor, Okio |
| Images | BlurHash, Accompanist drawablepainter |
| Debug | LeakCanary, Curtains, Compose tooling |
| Market | Google Play Review, Play In-App Update |

## Adding a New Easter Egg Module

Follow this checklist when adding a new Android version Easter egg:

1. **Create the module directory**: `eggs/<NewVersion>/`
2. **Choose the convention plugin**:
   - Modern eggs (API 34+) → `easter.eggs.compose.library`
   - Legacy eggs → `easter.eggs.library`
3. **Create the build file**:
   - Modern: `build.gradle.kts` with `id("easter.eggs.compose.library")`
   - Legacy: `build.gradle` with `apply plugin: "easter.eggs.library"`
4. **Set namespace and resource prefix**:
   - Namespace: `com.android_<letter>.egg` (one-letter prefix for platform versions)
   - Resource prefix: single letter + underscore (e.g. `v_` for VanillaIceCream)
5. **Register in `settings.gradle.kts`**: add `":eggs:<NewVersion>"`
6. **Add to `app/build.gradle.kts`**: add `implementation(project(":eggs:<NewVersion>"))`
7. **Create `lint-baseline.xml`** in the module root (if using a plugin with baseline enabled)
8. **Wire up activity actions** in `:core:activity-actions` if the egg has launchable activities
9. **Use `sync-android-egg` skill** for AOSP-derived content fetch and adaptation

## Testing & Verification

### Quick Verification by Module Group

```sh
# Core modules
./gradlew :core:local-provider:compileDebugKotlin
./gradlew :core:navigation:compileDebugKotlin
./gradlew :core:composable:compileDebugKotlin
./gradlew :core:theme:compileDebugKotlin

# Feature modules
./gradlew :feature:analog-clock-widget:compileDebugKotlin
./gradlew :feature:neko-controls-widget:compileDebugKotlin
./gradlew :feature:cat-editor:compileDebugKotlin

# Eggs modules (Compose)
./gradlew :eggs:Baklava:compileDebugKotlin
./gradlew :eggs:VanillaIceCream:compileDebugKotlin
./gradlew :eggs:UpsideDownCake:compileDebugKotlin

# Eggs modules (non-Compose)
./gradlew :eggs:Tiramisu:compileDebugJavaWithJavac

# App module (full assemble)
./gradlew app:compileFossProductDebugKotlin
./gradlew app:assembleFossProductRelease

# App module (market variant — only works with product track)
./gradlew app:assembleMarketProductRelease
```

### Test Infrastructure

- **Instrumentation tests**: `app/src/androidTest/` using `AndroidJUnitRunner`.
- **No `src/test/` unit tests** exist in any module.
- **Room schemas** exported from `feature/cat-editor/schemas/` for migration verification.
- Test dependencies are in the `android-test` bundle in `libs.versions.toml`.

## Common Search Targets

Use these first for common tasks:

- **Launcher icons and aliases**:
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/res/mipmap-*`
  - `app/src/main/java/com/dede/android_eggs/views/settings/compose/prefs/AppIconPref.kt`
  - `basic/src/main/java/com/dede/basic/Utils.kt`
- **Shortcuts**:
  - `core/shortcut/`
  - `core/shortcut/src/main/java/com/dede/android_eggs/views/main/util/EasterEggShortcutsHelp.kt`
- **App navigation**:
  - `app/src/main/java/com/dede/android_eggs/views/main/`
  - `app/src/main/java/com/dede/android_eggs/views/main/EasterEggsNavHost.kt`
  - `core/navigation/`
- **Shared Compose UI**:
  - `core/composable/`
  - `core/theme/`
  - `core/local-provider/`
- **Custom Tabs / URI handling**:
  - `core/custom-tab-browser/`
  - `core/local-provider/`
  - `app/src/main/java/com/dede/android_eggs/views/main/EasterEggsNavHost.kt`
- **Analog clock widget**:
  - `feature/analog-clock-widget/`
- **Neko controls widget**:
  - `feature/neko-controls-widget/`
- **Cat editor / Neko cats**:
  - `feature/cat-editor/`
  - `eggs/Nougat/` (original Neko implementation)
  - `eggs/R/` (Cat Controls)
- **Easter egg code by Android version**:
  - `eggs/<VersionName>/` — see Easter Egg Modules table for version mapping
- **Build plugin logic**:
  - `build-logic/convention/src/main/kotlin/com/dede/android_eggs/plugins/`
- **Gradle version catalog**:
  - `gradle/libs.versions.toml`
- **Settings / module registration**:
  - `settings.gradle.kts`

## Resource Conventions

- Android resources live under each module's `src/main/res`.
- Shared app strings and localization mostly live in `core/resources`.
- Theme resources live in `core/theme` and feature-local `values` /
  `values-night` folders when the feature needs isolated widget resources.
- For RemoteViews widgets, prefer concrete resource values over runtime-only
  Compose or theme assumptions.
- Egg modules use **resource prefixes** to avoid collisions: `g_`, `h_`, `i_`,
  `j_`, `k_`, `l_`, `m_`, `n_`, `o_`, `p_`, `q_`, `r_`, `s_`, `t_`, `u_`,
  `v_`, `baklava_`, `cinnamon_bun_`, `b_`.

## Editing Guidance

- Keep feature-specific resources inside that feature module.
- Avoid adding cross-module `R` references when copying a small local resource
  is the existing pattern for ownership.
- Keep Compose-facing helpers in Compose-enabled modules.
- Keep non-Compose browser logic in `core/custom-tab-browser`.
- For AOSP-derived Easter egg code, preserve package structure and behavior
  unless the task explicitly asks for modernization.
- When adding or changing XML-driven widget metadata, keep XML as the source of
  truth if the existing flow reads from XML.
- When adding a new library dependency, add it to `gradle/libs.versions.toml`
  first, then reference it via `libs.*` in build files.
- Do not introduce new build convention plugins without updating this document.
