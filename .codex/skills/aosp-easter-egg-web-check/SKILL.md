---
name: aosp-easter-egg-web-check
description: Inspect public AOSP easter egg changes with cs.android.com, android.googlesource.com, and source.android.com. Use when checking whether PlatLogoActivity, frameworks/base/packages/EasterEgg, related resources, or Android release tags show public easter egg updates without a full AOSP checkout.
---

# Aosp Easter Egg Web Check

## Workflow

1. Confirm the latest public Android release or tag on `source.android.com`.
2. Check `frameworks/base/core/java/com/android/internal/app/PlatLogoActivity.java`.
3. Check `frameworks/base/packages/EasterEgg/AndroidManifest.xml` and `frameworks/base/packages/EasterEgg/src/com/android/egg/landroid/MainActivity.kt`.
4. Follow only the `R.*` resources already referenced by `PlatLogoActivity`.
5. Search for weak signals: new dessert codename, new `egg_mode_*`, new `platlogo`, new release branch, or new release tag.
6. If a local repo exists, compare only against the latest complete local egg module and ignore preview-only modules.
7. Output results in four buckets using the user's language: confirmed updates, suspected new-version signals, no public change found, and file-level diff not attempted under web-only constraints.

## Rules

- Prefer `source.android.com` for release truth, `android.googlesource.com` for file truth, and `cs.android.com` for discovery.
- Do not claim file-level diffs or first-introduced commits unless the web evidence clearly proves them.
- Treat preview modules such as `AndroidNext` as signals, not full easter egg releases.
