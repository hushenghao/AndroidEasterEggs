# AndroidEasterEggs Project Map

This document is for AI coding tools. Use it to locate code, choose the right
module, and run the smallest useful verification command.

## Repository Rules

- Keep commit message style consistent with the existing project history.
- Do not revert unrelated working tree changes.
- Prefer focused fixes in the module or method named by the task.
- Use `rg` / `rg --files` for code search.
- For Android changes, verify with the most relevant Gradle module task when possible.

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

## Build System

- `build.gradle.kts` declares top-level plugin aliases.
- `settings.gradle.kts` includes all Gradle modules and the `build-logic` included build.
- `build-logic/convention` defines convention plugins:
  - `easter.eggs.app`
  - `easter.eggs.basic.library`
  - `easter.eggs.library`
  - `easter.eggs.compose.library`
- App signing may use `key.properties`.
- Java 17 and Android SDK are required.

Common verification commands:

```sh
./gradlew app:compileFossProductDebugKotlin
./gradlew app:assembleFossProductRelease
./gradlew :feature:analog-clock-widget:compileDebugKotlin
./gradlew :feature:neko-controls-widget:compileDebugKotlin
./gradlew :core:local-provider:compileDebugKotlin
```

Prefer the smallest module-specific compile/test command that covers the change.

## App Module

Path: `app/`

Purpose:

- Android application entry point.
- Wires together core modules, feature modules, and all `eggs` modules.
- Defines application id `com.dede.android_eggs`.
- Defines product flavors:
  - `foss`
  - `market`
  - `alpha`
  - `beta`
  - `product`

Important paths:

- `app/src/main/AndroidManifest.xml`: app activities, aliases, providers, receivers.
- `app/src/main/java/com/dede/android_eggs/views/main/`: main app navigation and home flow.
- `app/src/main/java/com/dede/android_eggs/views/settings/`: settings UI and preferences.
- `app/src/main/res/`: app resources and launcher icons.
- `app/src/foss/`: FOSS flavor source set.
- `app/src/market/`: market flavor source set.
- `app/src/androidTest/`: instrumentation tests.

## Core Modules

Path group: `core/`

- `:core:activity-actions`
  - Action definitions and launch helpers for Easter egg activities.
  - Depends on several `:eggs:*` modules.
- `:core:alterable-adaptive-icon`
  - Adaptive launcher icon switching support and related resources.
- `:core:composable`
  - Shared Compose UI utilities and reusable composables.
- `:core:custom-tab-browser`
  - Custom Tabs browser integration.
  - Keep this module Android/browser focused; do not add Compose UI helpers here unless the module already owns them.
- `:core:icons`
  - Compose icon assets and icon wrappers.
- `:core:local-provider`
  - CompositionLocal-style app providers and cross-cutting Compose integrations.
  - App-wide URI handling helpers belong here when they depend on Compose.
- `:core:navigation`
  - Navigation contracts and route definitions.
- `:core:resources`
  - Shared strings, drawables, XML resources, and localized values.
- `:core:settings`
  - Settings storage and setting-related shared APIs.
- `:core:shortcut`
  - Launcher shortcut and app-icon shortcut support.
- `:core:theme`
  - Material theme, colors, typography, and theme resources.

## Feature Modules

Path group: `feature/`

- `:feature:analog-clock-widget`
  - Analog clock app widget, widget configuration activity, widget XML metadata, and widget preview resources.
  - Important paths:
    - `feature/analog-clock-widget/src/main/java/com/dede/android_eggs/views/widget/`
    - `feature/analog-clock-widget/src/main/res/xml/analog_clock_widget_info.xml`
    - `feature/analog-clock-widget/src/main/res/layout*/`
- `:feature:cat-editor`
  - Neko cat editor UI and local cat database.
  - Room schemas live in `feature/cat-editor/schemas/`.
- `:feature:crash`
  - Crash test or crash display feature.
- `:feature:embedding-splits`
  - Android large-screen / split embedding support.
- `:feature:libraries-info`
  - Open source libraries information UI and generated/raw library metadata.
- `:feature:neko-controls-widget`
  - Neko controls app widget, RemoteViews layouts, widget resources, and widget theme resources.
  - Important paths:
    - `feature/neko-controls-widget/src/main/java/`
    - `feature/neko-controls-widget/src/main/res/layout/`
    - `feature/neko-controls-widget/src/main/res/values/`
    - `feature/neko-controls-widget/src/main/res/values-night/`
    - `feature/neko-controls-widget/src/main/res/xml/`

## Base Utility Modules

- `:basic`
  - Shared Android utility code.
  - Namespace: `com.dede.basic`.
- `:jvm-basic`
  - Shared JVM-only utility code used by JVM modules or scripts.

## Easter Egg Modules

Path group: `eggs/`

These modules preserve or adapt Android platform Easter egg implementations.
Prefer minimal changes when editing AOSP-derived code.

Included modules:

- `:eggs:Base`
- `:eggs:Gingerbread`
- `:eggs:Honeycomb`
- `:eggs:IceCreamSandwich`
- `:eggs:JellyBean`
- `:eggs:KitKat`
- `:eggs:Lollipop`
- `:eggs:Marshmallow`
- `:eggs:Nougat`
- `:eggs:Oreo`
- `:eggs:Pie`
- `:eggs:Q`
- `:eggs:R`
- `:eggs:S`
- `:eggs:Tiramisu`
- `:eggs:UpsideDownCake`
- `:eggs:VanillaIceCream`
- `:eggs:Baklava`
- `:eggs:CinnamonBun`
- `:eggs:AndroidNext`
- `:eggs:RocketLauncher`

Notes:

- `:eggs:AndroidNext` is conditionally added by the app convention plugin when
  `eggs.androidNext.enable=true`.
- `:eggs:RocketLauncher` contains launcher/dream-related legacy code used by
  older Easter eggs.
- Some eggs have local `README.md` files with version-specific context.

## Scripts And Assets

- `script/`
  - Repository maintenance scripts, changelog helpers, icon tools, Crowdin data,
    blurhash helpers, and Easter egg log tooling.
- `:script:compose-material-icons-generator`
  - JVM Gradle module for generating Compose Material icon code.
- `:script:emoji-svg-xml-convertor`
  - JVM Gradle module for converting emoji SVG/XML assets.
- `assets/`
  - Design, image, and shapeshifter source assets.
- `fastlane/`
  - Store metadata and release assets.
- `wiki/`
  - Documentation and localized wiki pages.

## Common Search Targets

Use these first for common tasks:

- Launcher icons and aliases:
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/res/mipmap-*`
  - `app/src/main/java/com/dede/android_eggs/views/settings/compose/prefs/AppIconPref.kt`
  - `basic/src/main/java/com/dede/basic/Utils.kt`
- Shortcuts:
  - `core/shortcut/`
  - `app/src/main/java/com/dede/android_eggs/views/settings/compose/prefs/EasterEggShortcutsHelp.kt`
- App navigation:
  - `app/src/main/java/com/dede/android_eggs/views/main/`
  - `core/navigation/`
- Shared Compose UI:
  - `core/composable/`
  - `core/theme/`
  - `core/local-provider/`
- Custom Tabs / URI handling:
  - `core/custom-tab-browser/`
  - `core/local-provider/`
  - `app/src/main/java/com/dede/android_eggs/views/main/EasterEggsNavHost.kt`
- Analog clock widget:
  - `feature/analog-clock-widget/`
- Neko controls widget:
  - `feature/neko-controls-widget/`
- Cat editor:
  - `feature/cat-editor/`
- Android version Easter egg code:
  - `eggs/<VersionName>/`

## Resource Conventions

- Android resources live under each module's `src/main/res`.
- Shared app strings and localization mostly live in `core/resources`.
- Theme resources live in `core/theme` and feature-local `values` /
  `values-night` folders when the feature needs isolated widget resources.
- For RemoteViews widgets, prefer concrete resource values over runtime-only
  Compose or theme assumptions.

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

