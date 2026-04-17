---
name: aosp-easter-egg-web-check
description: Inspect public AOSP easter egg changes with cs.android.com, android.googlesource.com, source.android.com, and related official Android web sources. Use when checking whether PlatLogoActivity, frameworks/base/packages/EasterEgg, related resources, Android release tags, or public branch names show easter egg updates without checking out the full AOSP source tree.
---

# Aosp Easter Egg Web Check

## Goal

Verify what is publicly visible about AOSP easter egg changes. Be sensitive to exact release tags, branch names, and public source domains. Distinguish confirmed source changes from weak signals and from local-project-only previews.

## Sources

Check official sources in this order:

1. `source.android.com`
   - Confirm release truth.
   - Check `android-latest-release`, build numbers, release dates, and public tag names.
2. `android.googlesource.com`
   - Confirm file truth.
   - Check exact file contents, tree pages, and when possible tag or branch-specific pages.
3. `cs.android.com`
   - Discover symbols, file paths, resource names, codenames, and branch-specific search hits.
4. `developer.android.com`
   - Use only as supporting evidence for public Android version naming, timeline pages, or release-facing material.

Do not use third-party mirrors as primary evidence.

## Branch And Tag Rules

Always record the exact names you found:

- release branch, such as `android16-qpr2-release`
- public tag, such as `android-16.0.0_r4`
- source branch, such as `main` or `master`

Treat these as different evidence levels:

- strongest: exact tag page or exact file on `android.googlesource.com`
- medium: official release/build pages on `source.android.com`
- weaker: `cs.android.com` discovery hits that still need confirmation

Never blur these questions together:

- latest official Android release line
- latest public AOSP source on `main/master`
- latest public tag in the Android family the user cares about

If the user asks for "latest", answer with exact tag or branch names and dates whenever available.

Prioritize version mapping over implementation minutiae:

- first identify the newest relevant release branch or public tag
- then map that branch or tag to the Android version it represents
- only then inspect whether the easter egg implementation appears to have changed
- treat `egg_mode_*` as a secondary signal unless it is explicitly one of the changed items

## Core Checks

Always check these paths first:

1. `frameworks/base/core/java/com/android/internal/app/PlatLogoActivity.java`
2. `frameworks/base/packages/EasterEgg/AndroidManifest.xml`

Use the manifest to identify the Activity that handles `com.android.internal.category.PLATLOGO`, then prioritize that Activity over other EasterEgg implementation files.

Then check supporting paths when discoverable:

- the `com.android.internal.category.PLATLOGO` target Activity resolved from the manifest
- `frameworks/base/packages/EasterEgg/src/com/android/egg/landroid/MainActivity.kt` only when it is clearly the next-stage implementation or the manifest points to it indirectly
- `frameworks/base/packages/EasterEgg/src/com/android/egg/ComponentActivationActivity.java`
- `frameworks/base/packages/EasterEgg/res/values/*.xml`
- `frameworks/base/packages/EasterEgg/res/drawable*/*platlogo*`
- `frameworks/base/packages/EasterEgg/res/drawable*/*patch*`
- `frameworks/base/packages/EasterEgg/easter_egg_flags.aconfig`

## Resource Checks

For `frameworks/base/core/res`, do not try to scan the whole tree. Instead:

1. Extract the current `R.*` references from `PlatLogoActivity`.
2. Follow only those referenced resources.
3. Check whether those resources appear renamed, replaced, or newly paired with fresh references.

## Search Terms

Use a fixed set of queries when the user asks for a broad check:

- current release branch
- current public tag
- Android version codename mapping
- `PlatLogoActivity`
- `frameworks/base/packages/EasterEgg`
- `egg_mode_`
- `platlogo`
- `patch adaptive`
- dessert codename candidates
- likely next-version identifiers such as `android-17`, `CinnamonBun`, or later user-specified names

When a likely next-version codename or branch appears, try to confirm it on at least one stronger source before calling it a confirmed change.

## New-Version Signals

Treat these as weak signals unless confirmed by source pages:

- a new Android release tag
- a new release branch
- a new dessert codename
- a new `platlogo` or `patch` resource name
- a new local module in a project repo

If a local repo contains a preview module such as `AndroidNext`, record it as a preview signal unless it contains a full easter egg implementation.

Treat a changed `egg_mode_*` as a meaningful signal only when it is explicitly visible in a confirmed source file.

## Local Project Comparison

When the user also has a local repo:

1. Identify the latest complete local egg module.
2. Ignore preview-only modules for the main comparison.
3. Compare the local complete module against confirmed public AOSP behavior.
4. Separate:
   - confirmed AOSP changes
   - local-only fixes or adaptations
   - preview signals for a newer Android version

## Reporting Rules

Output results in four buckets using the user's language:

- confirmed updates
- suspected new-version signals
- no public change found
- file-level diff not attempted under web-only constraints

Always include:

- exact source domain used
- exact tag or branch name when available
- exact date when available
- the Android version implied by that tag or branch when it is knowable from official sources

Do not claim:

- a complete file-level diff from web evidence alone
- the first introducing commit unless a public source page clearly proves it
- a next-generation easter egg release based only on preview assets, local modules, or unconfirmed search hits

## Example Requests

- `Use $aosp-easter-egg-web-check to see whether the latest public AOSP easter egg source has changed.`
- `Use $aosp-easter-egg-web-check to inspect PlatLogoActivity, the EasterEgg module, and current release tags.`
- `Use $aosp-easter-egg-web-check to look for public signs of an Android 17 or later easter egg.`
