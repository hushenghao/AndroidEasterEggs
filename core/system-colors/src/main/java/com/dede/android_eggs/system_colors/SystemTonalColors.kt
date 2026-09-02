package com.dede.android_eggs.system_colors

import android.content.Context
import com.materialkolor.hct.Hct
import com.materialkolor.palettes.TonalPalette

/**
 * Runtime override of the framework dynamic color resources (`system_accent1_400` etc.),
 * generated from a source color with the Monet tonal spot style.
 *
 * The framework only provides these colors since API 31, so [Context.getSystemColor]
 * resolves them from here on API 27-30, keeping the egg colors in sync with the
 * wallpaper based theme seed.
 */
object SystemTonalColors {

    private val SHADES = intArrayOf(0, 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)

    // Framework monet tone ladder for each shade. Note the direction: shade 0 is the
    // lightest end (tone 100) and shade 1000 the darkest (tone 0), matching the static
    // fallbacks in res/values/colors.xml.
    private val SHADE_TONES = doubleArrayOf(
        100.0, 99.0, 95.0, 90.0, 80.0, 70.0, 60.0, 49.6, 40.0, 30.0, 20.0, 10.0, 0.0,
    )

    // Chroma and hue offset of the Monet tonal spot style, the same values
    // materialkolor's dynamicColorScheme uses for the app color scheme.
    private const val PRIMARY_CHROMA = 36.0
    private const val SECONDARY_CHROMA = 16.0
    private const val TERTIARY_HUE_OFFSET = 60.0
    private const val TERTIARY_CHROMA = 24.0
    private const val NEUTRAL_CHROMA = 4.0
    private const val NEUTRAL_VARIANT_CHROMA = 8.0

    @Volatile
    private var colors: Map<String, Int> = emptyMap()

    /**
     * Generate the five system tonal palettes from [sourceColor] and override the
     * `system_<group>_<tone>` colors resolved by [Context.getSystemColor].
     * Thread-safe, slow on the first call, prefer a background thread.
     */
    fun updateSourceColor(sourceColor: Int) {
        val hue = Hct.fromInt(sourceColor).hue
        val palettes = mapOf(
            "accent1" to TonalPalette.fromHueAndChroma(hue, PRIMARY_CHROMA),
            "accent2" to TonalPalette.fromHueAndChroma(hue, SECONDARY_CHROMA),
            "accent3" to TonalPalette.fromHueAndChroma(hue + TERTIARY_HUE_OFFSET, TERTIARY_CHROMA),
            "neutral1" to TonalPalette.fromHueAndChroma(hue, NEUTRAL_CHROMA),
            "neutral2" to TonalPalette.fromHueAndChroma(hue, NEUTRAL_VARIANT_CHROMA),
        )
        val generated = HashMap<String, Int>(SHADES.size * palettes.size)
        for ((group, palette) in palettes) {
            for (i in SHADES.indices) {
                generated["system_${group}_${SHADES[i]}"] = palette.getHct(SHADE_TONES[i]).toInt()
            }
        }
        colors = generated
    }

    internal fun getColor(resName: String): Int? = colors[resName]
}
