---
name: sync-android-egg
description: Use ONLY when syncing a new Android version Easter egg from AOSP source. Covers fetching PlatLogoActivity and landroid game code from android.googlesource.com, adapting package names, adding minSdk compatibility guards, updating manifests, registering with Hilt/DI, and handling permission requests. Triggered by phrases like "sync android 18 egg", "add new android easter egg", or "基于android XX源码同步彩蛋".
---

# Sync Android Easter Egg from AOSP

## Overview

This skill documents the end-to-end workflow for syncing a new Android platform
Easter egg from AOSP source into this project. The goal is to produce a module
under `eggs/<VersionName>/` that matches the style of existing modules like
`eggs/Baklava`.

**Always copy or read an existing recent egg module (e.g. `eggs/Baklava` or
`eggs/VanillaIceCream`) and mimic its package structure, resource naming, and
Compose/Hilt wiring. Do not invent new conventions.**

## minSdk Compatibility (CRITICAL)

The project's **minSdk is 23** (Android 6.0 Marshmallow). All AOSP upstream
code targets the current platform API level and contains **no compatibility
guards**. Every file synced from upstream MUST be checked against the API
reference below.

### High-risk APIs requiring SDK guards

| API | Min SDK | Guard pattern |
|---|---|---|
| `NotificationChannel` | 26 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)` |
| `WindowInsets.Type.systemBars()` | 30 | Use AndroidX `WindowInsetsControllerCompat` instead |
| `getWindowInsetsController()` | 30 | Use `WindowCompat.getInsetsController()` |
| `setColorMode(COLOR_MODE_HDR)` | 26 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)` |
| `ColorSpace.get(EXTENDED_SRGB)` | 26 | Lazy-init with `@RequiresApi`, fallback to `packColor()` SDR |
| `VibratorManager` | 31 | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)` + null guard |
| `VibrationEffect.startComposition()` | 31 | Same guard as VibratorManager |
| `Handler.createAsync()` | 28 | Use `HandlerCompat.createAsync()` from AndroidX |
| `RELEASE_OR_CODENAME` | 30 | OK if only in `when` else-branch (only reached on new devices) |
| `Notification.Builder(context, channelId)` | 26 | Fallback to deprecated `Notification.Builder(context)` |
| `setFlag(FLAG_ONLY_ALERT_ONCE)` | 30 | `if (R)` else `setOnlyAlertOnce(true)` |
| `setRequestPromotedOngoing()` | BAKLAVA_1 | Guard with `SDK_INT_FULL` check |

### Compatibility verification checklist

After syncing each file, run these checks:

```sh
# Scan for unguarded high-API calls
grep -nE "NotificationChannel|ColorSpace|COLOR_MODE_HDR|setColorMode|VibratorManager|VibrationEffect|PRIMITIVE_SPIN|CombinedVibration|WindowInsets\.Type|setDecorFitsSystemWindows|getWindowInsetsController|Handler\.createAsync|RELEASE_OR_CODENAME" <file>

# Check imports for AndroidX compat (should use these, not raw framework)
grep -nE "WindowCompat|WindowInsetsControllerCompat|WindowInsetsCompat|HandlerCompat" <file>
```

If the file uses any high-API call without a `Build.VERSION.SDK_INT >=` guard,
copy the guard pattern from `eggs/Baklava` or `eggs/VanillaIceCream`.

### Critical file: UniverseProgressNotifier.kt

**Do NOT simply copy from the existing egg module.** The upstream version may
contain new features or logic changes. Follow this process:

1. Fetch the upstream version as part of Phase 2.1.
2. Compare it against the most recent existing egg module's
   `UniverseProgressNotifier.kt` (e.g. `eggs/Baklava`):
   ```sh
   diff eggs/<ExistingEgg>/.../UniverseProgressNotifier.kt eggs/<NewEgg>/.../UniverseProgressNotifier.kt
   ```
3. The existing version has compatibility guards (SDK checks on
   `NotificationChannel`, `Notification.Builder`, `setFlag`, `setStyle`,
   `isProgressNotifierSupported`, etc.) that are **not** in the upstream
   source. Preserve these guards while merging any NEW logic from upstream.
4. If the diff shows only minor upstream changes (comments, formatting),
   prefer the existing module's version and adapt the package/prefix.
5. If the upstream has significant new features, port them into the existing
   version's guarded structure.

---

## Phase 0: Determine version info

1. Identify the **Android version name** (e.g. CinnamonBun), **API level**,
   **AOSP branch name** (e.g. `android17-release`).

2. Decide on a **module namespace** (`com.android_<lowercamel>_egg`), **resource
   prefix** (`<lowercamel>_`), and **setting key** (`egg_mode_<lowercamel>`).

3. Confirm the branch exists on
   `https://android.googlesource.com/platform/frameworks/base/+/refs/heads/<branch>/`

4. **Scan upstream for new content**: Fetch the upstream `EasterEgg/AndroidManifest.xml`
   and `EasterEgg/src/com/android/egg/` directory listing. Identify any new
   subdirectories or declared components beyond what the previous egg module has.
   This prevents missing new egg content that was added in this release.

---

## Phase 1: Fetch and adapt PlatLogoActivity.java

1. Fetch the upstream source:
   ```sh
   export https_proxy=http://127.0.0.1:7890 http_proxy=http://127.0.0.1:7890 all_proxy=socks5://127.0.0.1:7890
   curl -sL "https://android.googlesource.com/platform/frameworks/base/+/refs/heads/<BRANCH>/core/java/com/android/internal/app/PlatLogoActivity.java?format=TEXT" | base64 -d > PlatLogoActivity.java
   ```

2. Adapt the file:
   - **Package**: `com.android.internal.app` → `com.android_<name>_egg`
   - **Resource**: `R.drawable.platlogo` → `R.drawable.<prefix>_platlogo`
   - **Starfield class**: change `private static class` → `static class` (package-private for Kotlin interop)
   - **`shouldWriteSettings()`**: return `true` (standalone app)

3. Add **API compatibility guards** — refer to the [minSdk Compatibility](#minsdk-compatibility-critical)
   table at the top of this skill for all required guards. Key points:
   - Window insets: use AndroidX `WindowInsetsControllerCompat` instead of raw `WindowInsets`
   - HDR color mode: wrap in `Build.VERSION.SDK_INT >= Build.VERSION_CODES.O`
   - VibratorManager: guard with `Build.VERSION.SDK_INT >= Build.VERSION_CODES.S`
   - Handler: use `HandlerCompat.createAsync()` instead of `Handler.createAsync()`
   - HDR color rendering: add `@ChecksSdkIntAtLeast isSRgbExtSupported` flag with `packColor` SDR fallback
   - `ColorSpace.get`: lazy-init with `@RequiresApi`

   After applying guards, run the grep scan from the compatibility section to verify.

4. Remove unnecessary AOSP internals:
   - Delete `measureTouchPressure` / `syncTouchPressure` methods and related fields
   - Delete `onStart()` / `onStop()` overrides if they only call touch pressure
   - Remove `import org.json.JSONObject`, `import static java.lang.Math.max`
   - Remove `ContentResolver` usage if replaced by SpUtils

5. Replace `Settings.System.putLong` → `SpUtils.putLong(this, key, value)` from `com.dede.basic.SpUtils`

6. Check the upstream PlatLogoActivity for any Heptadecagram-related fixes (e.g. commit `350b4843` for the swap-to-platlogo timing fix)

---

## Phase 2: Scan and sync EasterEgg game code

### 2.0 Discover ALL new content

**Do NOT assume the upstream only contains `landroid/`.** Each Android release
may add, remove, or rename subdirectories. First, scan the upstream
`EasterEgg/src/com/android/egg/` directory to discover all content:

```sh
# List all subdirectories in the upstream EasterEgg source
curl -sL "$BASE/../?format=TEXT" | base64 -d | grep -oP '(?<=href=")[^"]+(?=")' | grep -E '^[a-z]+/$'
```

Known historical subdirectories (for reference):

| Directory | Android version | Content type |
|---|---|---|
| `landroid/` | S+ | Space exploration game |
| `neko/` | N | Cat collector game |
| `paint/` | P | Paint widget |
| `quares/` | Q | Number puzzle |
| `widget/` | S | Paint chips widget |

**For each NEW subdirectory not present in the previous egg module:**
- Determine if it's a core feature (should be synced) or legacy relayed game
  (only sync if directly referenced by the new version's code).
- Check the upstream `AndroidManifest.xml` for any declared activities or
  services in that package.

### 2.1 Sync landroid game code (if present)

1. Fetch all files from `EasterEgg/src/com/android/egg/landroid/`:
   ```sh
   for f in MainActivity.kt Assets.kt Autopilot.kt Colors.kt ComposeTools.kt \
            DreamUniverse.kt Maths.kt Namer.kt PathTools.kt Physics.kt \
            Randomness.kt Universe.kt UniverseProgressNotifier.kt Vec2.kt \
            VisibleUniverse.kt; do
     curl -sL "$BASE/$f?format=TEXT" | base64 -d > "$DEST/$f"
   done
   ```
   **Note**: `Assets.kt` was added in Android 17. Files may vary by version.

2. Adapt package names:
   - `com.android.egg.landroid` → `com.android_<name>_egg.landroid`
   - `com.android.egg.flags` → `com.android_<name>_egg.flags`
   - `com.android.egg.R` → `com.android_<name>_egg.R`
   - `com.android.egg.ComponentActivationActivity` → `com.android_<name>_egg.ComponentActivationActivity`

3. Prefix ALL `R.array.*`, `R.string.*`, `R.drawable.*` references with `<prefix>_`:
   - `R.array.planet_descriptors` → `R.array.<prefix>_planet_descriptors`
   - `R.drawable.ic_spacecraft_filled` → `R.drawable.<prefix>_ic_spacecraft_filled`

4. Replace `getDessertCode()` body with delegation to `com.dede.basic.utils.DessertUtils.getDessertCode()`

5. Copy `UniverseProgressNotifier.kt` from the most recent existing egg (not upstream) to preserve compatibility guards for minSdk 23

### 2.2 Sync other new content (if discovered)

For any subdirectory found in step 2.0 that is **not** `landroid/` and is
**not** present in the previous egg module:

1. Compare the upstream and local `AndroidManifest.xml` to identify new
   activities, services, or receivers declared in that package.

2. Fetch all `.java` and `.kt` files from the new subdirectory, applying the
   same package/prefix adaptations as Phase 1 and 2.1.

3. Fetch any new resources (`res/drawable/`, `res/layout/`, `res/values/`,
   `res/xml/`) that are referenced by the new code.

4. If the new content has its own unlock/activation flow, add the necessary
   `ComponentActivationActivity` entries or `ComponentProvider` registrations.

5. Check with the user whether the new content should be synced or is
   unnecessary legacy content carried over from the AOSP module that won't be
   used in this project.

---

## Phase 3: Add support Java files

Copy from the most recent existing egg (e.g. `eggs/Baklava`) and adapt:

1. **`flags/Flags.java`** — package `com.android_<name>_egg.flags`, always returns `true`
2. **`ComponentActivationActivity.java`** — enables DreamUniverse after egg unlock

---

## Phase 4: Resources

1. Fetch the upstream `platlogo.xml` from:
   ```
   frameworks/base/+/<BRANCH>/core/res/res/drawable-nodpi/platlogo.xml
   ```
   Save as `res/drawable-nodpi/<prefix>_platlogo.xml`

2. Copy planet/spacecraft icon drawables from the most recent existing egg,
   renaming the prefix:
   ```
   ic_planet_large, ic_planet_medium, ic_planet_small, ic_planet_tiny,
   ic_spacecraft, ic_spacecraft_filled, ic_spacecraft_rotated
   ```

3. Create `res/values/landroid_strings.xml` from the most recent egg,
   renaming all `baklava_` prefixes (or equivalent) to `<prefix>_`.

   **IMPORTANT**: Remove any duplicate string (e.g. `<prefix>_egg_name`) that
   already exists in `res/values/strings.xml`.

4. Create `res/values/themes.xml` with `<prefix>_Theme.Landroid` style.

5. Create `res/xml/<prefix>_landroid_dream.xml` referencing `@drawable/<prefix>_platlogo`.

---

## Phase 5: AndroidManifest.xml

Following the Baklava module pattern, declare:

```xml
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.POST_PROMOTED_NOTIFICATIONS"/>

<activity android:name=".PlatLogoActivity" ... />
<service android:name=".landroid.DreamUniverse" android:enabled="false" ... />
<activity android:name=".landroid.MainActivity" ... />
```

---

## Phase 6: Register with Hilt/DI

Create `Android<VersionName>EasterEgg.kt` as an `@Module @InstallIn(SingletonComponent::class)`
object implementing both `EasterEggProvider` and `ComponentProvider`.

- `provideEasterEgg()`: register with icon, name, API level, `PlatLogoActivity::class.java`
- `provideComponent()`: provide enable/disable logic for `DreamUniverse`
- `provideTimelineEvents()`: add release timeline entries
- `SnapshotProvider`: create a preview using `Starfield` + plat logo

---

## Phase 7: Permission handling (optional)

Only required if the module's `AndroidManifest.xml` declares runtime permissions
such as `POST_NOTIFICATIONS`. Check the manifest first:

```sh
grep -E "uses-permission.*android:name" eggs/<VersionName>/src/main/AndroidManifest.xml
```

Runtime permissions (e.g. `POST_NOTIFICATIONS` on API 33+) need the app to
request them when the relevant Activity opens. Install-time permissions
(e.g. `POST_PROMOTED_NOTIFICATIONS`, `VIBRATE`) are granted automatically.

If runtime permissions are declared:

1. Add the new module as a dependency in `core/activity-actions/build.gradle.kts`:
   ```kotlin
   implementation(project(":eggs:<VersionName>"))
   ```

2. Add the new `MainActivity` class to the `pagers` list in
   `core/activity-actions/src/main/java/com/dede/android_eggs/util/actions/RequestNotificationPermissionAction.kt`:
   ```kotlin
   com.android_<name>_egg.landroid.MainActivity::class, // auto pilot
   ```

Skip this phase if no runtime permissions are declared.

---

## Phase 8: PlatLogoActivity → MainActivity navigation

Change `launchNextStage()` to launch `MainActivity` directly instead of using
the PLATLOGO category intent:

```java
import com.android_<name>_egg.landroid.MainActivity;

// in launchNextStage:
final Intent eggActivity = new Intent(this, MainActivity.class);
startActivity(eggActivity);
```

---

## Phase 9: Compile, fix, and verify compatibility

```sh
./gradlew :eggs:<VersionName>:compileDebugKotlin
./gradlew :eggs:<VersionName>:compileDebugJavaWithJavac
./gradlew :core:activity-actions:compileDebugKotlin
```

Common errors:
- **Duplicate resources**: remove duplicates from `landroid_strings.xml`
- **Unresolved R.array.***: ensure all resource references have the `<prefix>_` prefix
- **Unresolved `com.android.egg.*`**: fix any remaining package references
- **API compatibility**: ensure `UniverseProgressNotifier` has SDK guards

After compilation succeeds, **re-run the compatibility checks from the
[minSdk Compatibility](#minsdk-compatibility-critical) section** on all synced
files. The Kotlin compiler will not catch runtime API level violations — only
`grep`-style scan or lint can detect them.

```sh
# Scan all new files for unguarded high-API calls
grep -rnE "NotificationChannel|ColorSpace|VibratorManager|VibrationEffect\.start|CombinedVibration|WindowInsets\.Type\b|setDecorFitsSystemWindows|getWindowInsetsController|Handler\.createAsync" eggs/<VersionName>/src/
```

---

## Known fixes not in upstream AOSP

These fixes were applied to existing eggs and are **not present** in the
upstream AOSP source. Apply them when syncing a new egg.

### Heptadecagram swap timing fix

**File**: `PlatLogoActivity.java`  
**Commit**: `350b4843`  
**Issue**: `swapToPlatlogo()` was called immediately when the 17-dot path was
complete, but the user's finger is still down (tracking). This could cause the
swap during a `MOVE` event before `UP`.  
**Fix**: Set a `isPendingSwapToPlatlogo` flag when path completes, and only
call `swapToPlatlogo()` on `ACTION_UP`:

```java
private boolean isPendingSwapToPlatlogo = false;

// In mHeptaDecaView.setOnTouchListener:
if (heptadecagram.getPathLength() > Heptadecagram.MAX_DOTS) {
    isPendingSwapToPlatlogo = true;
    return true;
}
if (event.getAction() == MotionEvent.ACTION_UP && isPendingSwapToPlatlogo) {
    swapToPlatlogo();
    isPendingSwapToPlatlogo = false;
}
```

### Spacecraft may enter planet interior

**File**: `landroid/Universe.kt`  
**Gerrit**: `3743150`  
**Issue**: When landing on a planet with residual thrust, the spacecraft could
penetrate the planet's interior.  
**Fix**: Kill thrust before creating the landing:

```kotlin
val landing = Landing(ship, planet, a, namer.describeActivity(rng, planet))
if (ship.thrust != Vec2.Zero) {
    // kill the power
    ship.thrust = Vec2.Zero
}
ship.landing = landing
```

### DYNAMIC_ZOOM global state not reset on destroy

**File**: `landroid/MainActivity.kt` and `landroid/DreamUniverse.kt`  
**Issue**: `DYNAMIC_ZOOM` is a top-level `var` that persists across Activity/DreamService
lifecycles. When autopilot is toggled ON (or screensaver starts), it is set to `true`,
but never reset to `false` on destroy. Re-entering the page causes the camera zoom
to still dynamically adjust based on distance.  
**Fix**: Reset `DYNAMIC_ZOOM = false` in `onDestroy()` of both classes:

```kotlin
// In MainActivity.kt:
override fun onDestroy() {
    notifier?.cancel()
    DYNAMIC_ZOOM = false
    super.onDestroy()
}

// In DreamUniverse.kt:
override fun onDestroy() {
    notifier?.cancel()
    DYNAMIC_ZOOM = false
    super.onDestroy()
}
```

### UniverseProgressNotifier live-update notification

**File**: `landroid/UniverseProgressNotifier.kt`  
**Commit**: `85c032fd` (Baklava)  
**Issue**: Missing `setRequestPromotedOngoing(true)` for promoted live-update
notifications; `.setColorized(true)` removed to avoid conflicts.  
**Fix**: Add in the `.apply {}` block:

```kotlin
if (Build.VERSION.SDK_INT_FULL >= Build.VERSION_CODES_FULL.BAKLAVA_1) {
    setRequestPromotedOngoing(true)
}
```

---

## Reference: Key files in an egg module (may vary by version)

```
eggs/<VersionName>/
├── build.gradle.kts                              # plugin: easter.eggs.compose.library
├── src/main/AndroidManifest.xml                  # permissions + components
├── src/main/java/com/android_<name>_egg/
│   ├── PlatLogoActivity.java                     # splash / starfield + Heptadecagram
│   ├── ComponentActivationActivity.java          # enables unlockable components
│   ├── Android<Name>EasterEgg.kt                 # Hilt registration
│   ├── flags/Flags.java                          # feature flag (always true)
│   └── landroid/                                 # space exploration game
│       ├── MainActivity.kt
│       ├── Assets.kt                             # SVG paths (new in A17+)
│       ├── Autopilot.kt / Colors.kt / ComposeTools.kt
│       ├── DreamUniverse.kt                      # screensaver
│       ├── Maths.kt / Namer.kt / PathTools.kt
│       ├── Physics.kt / Randomness.kt
│       ├── Universe.kt / Vec2.kt / VisibleUniverse.kt
│       └── UniverseProgressNotifier.kt           # notification progress
│   └── <other>/                                  # any new content discovered in Phase 2.0
└── src/main/res/
    ├── drawable-nodpi/<prefix>_platlogo.xml
    ├── drawable/<prefix>_ic_planet_*.xml         # (if landroid present)
    ├── drawable/<prefix>_ic_spacecraft*.xml      # (if landroid present)
    ├── values/landroid_strings.xml               # (if landroid present)
    ├── values/strings.xml
    ├── values/themes.xml
    └── xml/<prefix>_landroid_dream.xml           # (if DreamUniverse present)
```

**Note**: The file tree varies by Android version. For example, `Assets.kt` was
introduced in Android 17 (CinnamonBun). Always compare with the upstream source
listing and the previous egg module to determine the correct set of files to sync.

---

## Reference: Common AOSP source locations

| Component | Path on android.googlesource.com |
|---|---|
| PlatLogoActivity | `platform/frameworks/base/+/<BRANCH>/core/java/com/android/internal/app/PlatLogoActivity.java` |
| platlogo drawable | `platform/frameworks/base/+/<BRANCH>/core/res/res/drawable-nodpi/platlogo.xml` |
| EasterEgg module | `platform/frameworks/base/+/<BRANCH>/packages/EasterEgg/` |
| landroid sources | `platform/frameworks/base/+/<BRANCH>/packages/EasterEgg/src/com/android/egg/landroid/` |
| landroid resources | `platform/frameworks/base/+/<BRANCH>/packages/EasterEgg/res/` |

Use `curl` with proxy to fetch files in `base64` format, then decode with `base64 -d`.
