package com.dede.android_eggs.preferences

import androidx.appcompat.app.AppCompatDelegate

/**
 * Single source of truth for the settings persisted in the default
 * `<packageName>_preferences` file (`Context.pref` in
 * `com.dede.android_eggs.util.Pref`).
 *
 * Every read and write goes through [PrefKey.get] and [PrefKey.set], every
 * absent value falls back to [PrefKey.default], so a setting's storage key and
 * its default are declared once instead of once per call site. Listing every key
 * here is also what makes storage-wide operations (backup, restore, reset)
 * possible without hand-maintained key lists.
 *
 * Keys are grouped by the module that owns the setting. The constant names
 * differ in style because they are the historical names already written to
 * disk; do not "tidy" them without a migration.
 */
object AppSettings {

    // Appearance, owned by :core:theme and :core:settings-ui --------------------

    /** Night mode, one of `ThemePrefUtil.AMOLED` / `LIGHT` / `DARK` / `FOLLOW_SYSTEM`. */
    val nightMode = PrefKey.int("pref_key_night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    const val COLOR_SOURCE_DEFAULT = 0
    const val COLOR_SOURCE_DYNAMIC = 1
    const val COLOR_SOURCE_CUSTOM = 2

    /**
     * Stored fallback only. Devices able to resolve a dynamic color source
     * default to [COLOR_SOURCE_DYNAMIC] instead, which `ColorSourcePrefUtil.DEFAULT_SOURCE`
     * decides at read time.
     */
    val colorSource = PrefKey.int("pref_key_color_source_1", COLOR_SOURCE_DEFAULT)

    /**
     * Custom seed color as ARGB, and the brand color used when wallpaper color
     * extraction fails (`defaultSeedColor` in `:core:theme`).
     */
    val seedColor = PrefKey.int("pref_key_seed_color", 0xFF4ADC8A.toInt())

    /** Index into `IconShapePrefUtil.shapeSpecs`, `0` meaning the system shape. */
    val iconShape = PrefKey.int("pref_key_override_icon_shape", 0)

    /** On/off, see `SettingPrefUtil.ON` and `OFF`. */
    val iconVisualEffects = PrefKey.int("pref_key_icon_visual_effects", 0)

    val retainInRecents = PrefKey.boolean("key_retain_in_recents", false)

    // Easter eggs ------------------------------------------------------------

    /** One of `RocketLauncherPrefUtil.VALUE_EASTER_EGG_ICONS` / `VALUE_ALL_APP_ICONS` / `VALUE_ALL_ICONS`. */
    val rocketLauncherIconsSource = PrefKey.int("pref_key_rocket_launcher_icons_source", 0)

    /** Owned by `:eggs:S` and by the Tiramisu beta variant, which shares this key. */
    val sTrypophobiaWarning = PrefKey.boolean("key_s_trypophobia_warning", false)

    /** Owned by `:eggs:Tiramisu`. */
    val tTrypophobiaWarning = PrefKey.boolean("key_t_trypophobia_warning", false)

    // Cat editor, owned by :feature:cat-editor -------------------------------

    val catEditorGridVisible = PrefKey.boolean("cat_editor_grid_visible", false)

    val catEditorMoreOptionGuide = PrefKey.boolean("cat_editor_more_option_guide", true)

    // First run --------------------------------------------------------------

    val privacyPolicyAgreed = PrefKey.boolean("key_welcome_status", false)

    // Internal bookkeeping ---------------------------------------------------

    val animatorDisabledDialogDontShowAgain =
        PrefKey.boolean("animator_disabled_alert_dialog_dont_show_again", false)

    val launchReviewCount = PrefKey.int("key_launch_review_count", 0)

    val savedVcsRevision = PrefKey.string("pref_save_vcs_revision", null)

    val lastVcsRevision = PrefKey.string("pref_last_vcs_revision", null)

    /**
     * SharedPreferences files the app writes apart from the default one, needed
     * to flush every pending write before backup reads the files from disk.
     *
     * The names belong to the AOSP-derived neko eggs and are declared in their
     * `PrefState.java`: `:eggs:Nougat`, `:eggs:R`, `:eggs:S` and `:eggs:Tiramisu`.
     */
    val nekoPrefFiles = listOf("N_mPrefs", "R_mPrefs", "S_mPrefs", "T_mPrefs")

    val all: List<PrefKey<*>> = listOf(
        nightMode,
        colorSource,
        seedColor,
        iconShape,
        iconVisualEffects,
        retainInRecents,
        rocketLauncherIconsSource,
        sTrypophobiaWarning,
        tTrypophobiaWarning,
        catEditorGridVisible,
        catEditorMoreOptionGuide,
        privacyPolicyAgreed,
        animatorDisabledDialogDontShowAgain,
        launchReviewCount,
        savedVcsRevision,
        lastVcsRevision,
    )

    init {
        val duplicates = all.groupBy { it.name }.filterValues { it.size > 1 }.keys
        require(duplicates.isEmpty()) { "Duplicate setting keys: $duplicates" }
    }
}
