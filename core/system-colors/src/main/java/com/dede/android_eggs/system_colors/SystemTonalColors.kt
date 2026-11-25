package com.dede.android_eggs.system_colors

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.dede.android_eggs.preferences.AppSettings
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.SchemeTonalSpot

/**
 * Runtime override of the framework dynamic color resources (`system_accent1_400` etc.),
 * generated from a source color with the Monet tonal spot style.
 *
 * The palettes come from materialkolor's [SchemeTonalSpot], the same scheme and spec
 * version the Compose color scheme uses, so a seed color produces the same hues here
 * and in the theme.
 *
 * The framework only provides these colors since API 31, so [Context.getSystemColor]
 * falls back to this palette on API 27-30. [syncWithSettings] also overrides them on
 * every API while the app color source is not the system one, keeping the eggs in
 * sync with the theme the user picked.
 */
object SystemTonalColors {

    private val SHADES = intArrayOf(0, 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)

    // Framework monet tone ladder for each shade. Note the direction: shade 0 is the
    // lightest end (tone 100) and shade 1000 the darkest (tone 0), matching the static
    // fallbacks in res/values/colors.xml.
    private val SHADE_TONES = doubleArrayOf(
        100.0, 99.0, 95.0, 90.0, 80.0, 70.0, 60.0, 49.6, 40.0, 30.0, 20.0, 10.0, 0.0,
    )

    private data class TonalState(
        val key: String?,
        val colors: Map<String, Int>,
    )

    @Volatile
    private var state = TonalState(key = null, colors = emptyMap())

    /**
     * Generate the five system tonal palettes from [sourceColor] and override the
     * `system_<group>_<tone>` colors resolved by [Context.getSystemColor].
     * Thread-safe, slow on the first call, prefer a background thread.
     *
     * [isDark] only changes the primary chroma of the tonal spot spec, pass the
     * current night mode so the palette matches the active theme.
     */
    fun updateSourceColor(sourceColor: Int, isDark: Boolean = false) {
        val generated = generatePalettes(sourceColor, isDark)
        state = TonalState(key = null, colors = generated)
    }

    private fun generatePalettes(sourceColor: Int, isDark: Boolean): Map<String, Int> {
        val scheme = SchemeTonalSpot(Hct.fromInt(sourceColor), isDark, 0.0)
        val palettes = mapOf(
            "accent1" to scheme.primaryPalette,
            "accent2" to scheme.secondaryPalette,
            "accent3" to scheme.tertiaryPalette,
            "neutral1" to scheme.neutralPalette,
            "neutral2" to scheme.neutralVariantPalette,
        )
        val generated = HashMap<String, Int>(SHADES.size * palettes.size)
        for ((group, palette) in palettes) {
            for (i in SHADES.indices) {
                generated["system_${group}_${SHADES[i]}"] = palette.getHct(SHADE_TONES[i]).toInt()
            }
        }
        return generated
    }

    /** Drop the override, so the framework colors or the static palette apply again. */
    fun clear() {
        state = TonalState(key = null, colors = emptyMap())
    }

    /**
     * Align the palette with the app's `colorSource` setting, the same source the
     * Compose color scheme uses:
     *
     * - [AppSettings.COLOR_SOURCE_CUSTOM] generates the palette from the user seed,
     *   [AppSettings.COLOR_SOURCE_DEFAULT] from the brand seed, so the eggs follow the
     *   theme even on API 31+.
     * - [AppSettings.COLOR_SOURCE_DYNAMIC] keeps the framework colors on API 31+ and
     *   the wallpaper palette on 27-30, where [WallpaperTonalColors] extracts the seed.
     *
     * Reads the settings on every call but regenerates only when the source, the seed
     * or the night mode changed, so [Context.getSystemColor] can call it directly.
     */
    @Synchronized
    fun syncWithSettings(context: Context) {
        val source = resolveColorSource(context)
        val seed = when (source) {
            AppSettings.COLOR_SOURCE_CUSTOM -> AppSettings.seedColor.get(context)
            AppSettings.COLOR_SOURCE_DEFAULT -> AppSettings.seedColor.default
            else -> WallpaperTonalColors.getSeedColor()
        }
        val isDark = isNightMode(context)
        val key = "$source:$seed:$isDark"
        if (key == state.key) {
            return
        }
        val nextColors = if (seed == null) emptyMap() else generatePalettes(seed, isDark)
        state = TonalState(key = key, colors = nextColors)
    }

    internal fun resolveColorSource(context: Context): Int {
        return when {
            AppSettings.colorSource.isSet(context) -> AppSettings.colorSource.get(context)
            isDynamicColorSourceSupported() -> AppSettings.COLOR_SOURCE_DYNAMIC
            else -> AppSettings.COLOR_SOURCE_DEFAULT
        }
    }

    private fun isDynamicColorSourceSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || isWallpaperColorSupported()
    }

    internal fun isNightMode(context: Context): Boolean {
        return when (AppSettings.nightMode.get(context)) {
            AppSettings.NIGHT_MODE_LIGHT -> false
            AppSettings.NIGHT_MODE_DARK,
            AppSettings.NIGHT_MODE_AMOLED -> true
            else -> {
                val uiMode = context.resources.configuration.uiMode
                (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    internal fun getColor(resName: String): Int? = state.colors[resName]
}
