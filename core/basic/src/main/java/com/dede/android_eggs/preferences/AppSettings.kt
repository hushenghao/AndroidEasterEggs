package com.dede.android_eggs.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.dede.android_eggs.preferences.AppSettings.COLOR_SOURCE_DYNAMIC
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Single source of truth for the settings persisted in the `app_settings`
 * Preferences DataStore (migrated on first read from `<packageName>_preferences`
 * via a key whitelist so AOSP egg unlock timestamps in `SpUtils` remain untouched).
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

    const val NIGHT_MODE_AMOLED = -2
    const val NIGHT_MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val NIGHT_MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES
    const val NIGHT_MODE_FOLLOW_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    /** Night mode, one of `ThemePrefUtil.AMOLED` / `LIGHT` / `DARK` / `FOLLOW_SYSTEM`. */
    val nightMode = PrefKey.int("pref_key_night_mode", NIGHT_MODE_FOLLOW_SYSTEM)

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

    /**
     * Real-time glassmorphic Haze blur on app bars, bottom bars, and full-screen overlays.
     */
    val hazeBlur = PrefKey.boolean("pref_key_haze_blur", false)

    // Easter eggs ------------------------------------------------------------

    /**
     * Sentinel value: every egg gets the large highlighted card. The other
     * values are literal card counts.
     */
    const val HIGHEST_EGG_COUNT_ALL = 4

    /**
     * Number of eggs rendered as the large highlighted card at the top of the
     * home list, `0`-`4`; `0` means every egg uses the small card, and
     * [HIGHEST_EGG_COUNT_ALL] highlights every egg.
     */
    val highestEggCount = PrefKey.int("pref_key_highest_egg_count", 1)

    /**
     * Blurred decorative logo backdrop on the trailing edge of home screen Easter egg cards.
     */
    val eggItemBackdropBlur = PrefKey.boolean("pref_key_egg_item_backdrop_blur", false)

    /**
     * Global "Material You" coloring for the AOSP-derived legacy eggs: they draw
     * with the wallpaper-seeded system palette instead of their hardcoded original
     * colors. Currently read only by `:eggs:JellyBean`.
     */
    val dynamicEggColors = PrefKey.boolean("pref_key_dynamic_egg_colors", false)

    /** One of `RocketLauncherPrefUtil.VALUE_EASTER_EGG_ICONS` / `VALUE_ALL_APP_ICONS` / `VALUE_ALL_ICONS`. */
    val rocketLauncherIconsSource = PrefKey.int("pref_key_rocket_launcher_icons_source", 0)

    /** Owned by `:eggs:S` and by the Tiramisu beta variant, which shares this key. */
    val sTrypophobiaWarning = PrefKey.boolean("key_s_trypophobia_warning", false)

    /** Owned by `:eggs:Tiramisu`. */
    val tTrypophobiaWarning = PrefKey.boolean("key_t_trypophobia_warning", false)

    // Cat editor, owned by :feature:cat-editor -------------------------------

    val catEditorGridVisible = PrefKey.boolean("cat_editor_grid_visible", true)

    val catEditorMoreOptionGuide = PrefKey.boolean("cat_editor_more_option_guide", true)

    // First run --------------------------------------------------------------

    val privacyPolicyAgreed = PrefKey.boolean("key_welcome_status", false)

    // Internal bookkeeping ---------------------------------------------------

    val animatorDisabledDialogDontShowAgain =
        PrefKey.boolean("animator_disabled_alert_dialog_dont_show_again", false)

    /** Sessions counted since the last rating prompt; reset when the app asks for one. */
    val launchReviewCount = PrefKey.int("key_launch_review_count", 0)

    /** Epoch millis of the last rating prompt, `0` when the app has never asked. */
    val lastRatingPromptTime = PrefKey.long("key_last_rating_prompt_time", 0L)

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
        hazeBlur,
        highestEggCount,
        eggItemBackdropBlur,
        dynamicEggColors,
        rocketLauncherIconsSource,
        sTrypophobiaWarning,
        tTrypophobiaWarning,
        catEditorGridVisible,
        catEditorMoreOptionGuide,
        privacyPolicyAgreed,
        animatorDisabledDialogDontShowAgain,
        launchReviewCount,
        lastRatingPromptTime,
        savedVcsRevision,
        lastVcsRevision,
    )

    init {
        val duplicates = all.groupBy { it.name }.filterValues { it.size > 1 }.keys
        require(duplicates.isEmpty()) { "Duplicate setting keys: $duplicates" }
    }

    fun preload(context: Context) {
        AppSettingsStore.snapshot(context)
    }

    fun snapshot(context: Context): AppSettingsSnapshot {
        return AppSettingsSnapshot(AppSettingsStore.snapshot(context))
    }

    fun snapshotFlow(context: Context): Flow<AppSettingsSnapshot> {
        return AppSettingsStore.dataFlow(context).map(::AppSettingsSnapshot)
    }

    fun edit(context: Context, block: AppSettingsEditor.() -> Unit) {
        AppSettingsStore.enqueueEdit(context) { prefs ->
            AppSettingsEditor(prefs).block()
        }
    }

    suspend fun flush() {
        AppSettingsStore.flush()
    }

    /**
     * Drops every registered setting in one transaction, so each of them reads
     * back its declared [PrefKey.default] again.
     *
     * Awaits disk commit on purpose: the settings screen restarts the process
     * right after a reset, and an unawaited write would die with it.
     * Settings stored outside this store (launcher icon, language, the eggs'
     * neko state) need their own reset.
     */
    suspend fun reset(context: Context) {
        AppSettingsStore.editAndAwait(context) { prefs ->
            for (key in all) {
                key.removeFrom(prefs)
            }
        }
    }
}
