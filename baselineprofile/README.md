# Baseline Profile & Benchmark Module

This module (`:baselineprofile`) is a self-instrumenting Android test module (`com.android.test`) that drives the app purely from the outside via UiAutomator. It does not depend on internal app components, Hilt, Compose, or shared core libraries.

It serves three core functions:
1. **Baseline Profile & Startup Profile Generation**: Generates AOT (Ahead-Of-Time) pre-compilation rules and DEX layout optimization profiles to boost cold startup speed and scroll performance.
2. **Startup Macrobenchmarks**: Measures and verifies startup time improvements comparing unoptimized runs against Baseline Profile-enabled runs.
3. **Release Build Smoke Testing**: Runs black-box integration tests on the minified `benchmarkRelease` build to catch issues caused by R8 optimization (such as stripped reflection targets, missing keep rules, or broken deep links).

---

## Key Classes & Architecture

| Class / File | Purpose |
|--------------|---------|
| `BaselineProfileGenerator.kt` | JUnit test collecting runtime rules for startup, main screen browsing, search, settings drawer, and core Easter eggs. |
| `StartupBenchmarks.kt` | Macrobenchmark comparing cold start performance with and without Baseline Profiles (`CompilationMode.None` vs `CompilationMode.Partial(Require)`). |
| `SmokeTest.kt` | Black-box smoke test verifying that the home UI, search, settings, and all supported `egg://easter_egg/api/<level>` deep links launch without crashing. |
| `Journeys.kt` | Reusable UIAutomator interaction flows (first-launch welcome acceptance, home wait, egg list scrolling, search, settings drawer). |
| `Eggs.kt` | Static mapping table of all Easter egg API levels (API 9–37) to their target entry Activities. |
| `Shell.kt` | Shell command utilities (resolving launcher activities, reading resumed activity from `dumpsys`, checking `logcat -b crash`). |
| `EnglishAppRule.kt` | JUnit rule pinning app locale to English (`cmd locale set-app-locales`) during testing so UIAutomator can locate controls by English text. |

---

## Profile Generation Workflow

Profile generation requires root access or Android 13+ (API 33+). The generated rules are saved to `app/src/main/generated/baselineProfiles/` and merged into the main source set (`mergeIntoMain = true`).

### 1. Generating Profiles

#### Option A: Using Gradle Managed Device (Recommended / Automatic)
The build file defines a managed device `pixel6Api33` using an AOSP system image:
```sh
./gradlew :app:generateBaselineProfile
```
Gradle will automatically download the AOSP system image, launch the emulator, record the profiles, and shut it down.

#### Option B: Using a Connected Device / Emulator
Run on a connected physical device or emulator running **Android 13+ (API 33+)**, or an API 28+ device with root access:

1. **Clear app data** (important so the first-launch welcome dialog path is captured):
   ```sh
   adb shell pm clear com.dede.android_eggs
   ```
2. **Execute profile generation**:
   ```sh
   ./gradlew :app:generateBaselineProfile -Peggs.baselineProfile.connected=true
   ```

### 2. Output Files
The updated rules will be written to:
* `app/src/main/generated/baselineProfiles/baseline-prof.txt`
* `app/src/main/generated/baselineProfiles/startup-prof.txt`

Review the diff with `git diff` and commit the updated files alongside relevant version updates.

---

## Running Benchmarks and Smoke Tests

Both tests run against the minified release build (`benchmarkRelease`) on a connected physical device:

### Smoke Test
Verifies home screen features and launches every Easter egg Activity up to the device's SDK level:
```sh
./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.dede.android_eggs.baselineprofile.SmokeTest
```

### Startup Macrobenchmark
Measures 10 cold-start iterations and prints timing metrics comparing uncompiled vs. baseline-profile compilation:
```sh
./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.dede.android_eggs.baselineprofile.StartupBenchmarks
```

---

## Maintenance Guidelines

- **When to Regenerate Profiles**:
  - Before tagged releases (e.g. before bumping version code/name).
  - After major changes to app startup logic, application initialization, or the main home screen.
  - When new high-priority Easter eggs are added to the active sampling list (`PROFILED_EGG_LEVELS` in `BaselineProfileGenerator.kt`).
- **When Adding / Modifying Easter Eggs**:
  - Update `EGGS` in `baselineprofile/src/main/kotlin/com/dede/android_eggs/baselineprofile/Eggs.kt` whenever an egg's entry Activity is added, renamed, or repointed.
- **Flavors & Variants**:
  - The profile generator uses `missingDimensionStrategy("app", "foss")` and `missingDimensionStrategy("track", "product")`. Because profile rules are class- and method-based, one unified profile serves all variants (FOSS, Market, Beta, Product).
