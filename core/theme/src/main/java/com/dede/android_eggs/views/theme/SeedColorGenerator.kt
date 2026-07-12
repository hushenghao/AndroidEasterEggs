package com.dede.android_eggs.views.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils

private val PRIMARY_TONES_LIGHT = doubleArrayOf(40.0, 100.0, 90.0, 10.0)
private val PRIMARY_TONES_DARK = doubleArrayOf(80.0, 20.0, 30.0, 90.0)
private val SECONDARY_TONES_LIGHT = doubleArrayOf(40.0, 100.0, 90.0, 10.0)
private val SECONDARY_TONES_DARK = doubleArrayOf(80.0, 20.0, 30.0, 90.0)
private val TERTIARY_TONES_LIGHT = doubleArrayOf(40.0, 100.0, 90.0, 10.0)
private val TERTIARY_TONES_DARK = doubleArrayOf(80.0, 20.0, 30.0, 90.0)
private val ERROR_TONES_LIGHT = doubleArrayOf(40.0, 100.0, 90.0, 10.0)
private val ERROR_TONES_DARK = doubleArrayOf(80.0, 20.0, 30.0, 90.0)
private val NEUTRAL_TONES_LIGHT = doubleArrayOf(98.0, 10.0)
private val NEUTRAL_TONES_DARK = doubleArrayOf(6.0, 90.0)
private val NEUTRAL_VARIANT_TONES_LIGHT = doubleArrayOf(90.0, 30.0, 50.0, 80.0)
private val NEUTRAL_VARIANT_TONES_DARK = doubleArrayOf(30.0, 80.0, 60.0, 30.0)

fun generateColorSchemeFromSeed(
    seedArgb: Int,
    isDark: Boolean,
): ColorScheme {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(seedArgb, hsv)
    val hue = hsv[0]
    val sat = hsv[1]

    val primarySat = maxOf(sat, 0.35f).toDouble()
    val secondarySat = sat.toDouble() * 0.3.coerceIn(0.1, 0.5)
    val tertiaryHue = ((hue + 60) % 360).toDouble()
    val tertiarySat = sat.toDouble() * 0.5.coerceIn(0.2, 0.6)
    val neutralSat = 0.02
    val neutralVariantSat = 0.04

    fun color(h: Double, s: Double, tone: Double): Color {
        val lightness = toneToLightness(tone)
        val argb = ColorUtils.HSLToColor(floatArrayOf(h.toFloat(), s.toFloat(), lightness.toFloat()))
        return Color(argb)
    }

    fun tonalColors(
        h: Double,
        s: Double,
        tones: DoubleArray,
    ): List<Color> = tones.map { color(h, s, it) }

    val primary = tonalColors(hue.toDouble(), primarySat, if (isDark) PRIMARY_TONES_DARK else PRIMARY_TONES_LIGHT)
    val secondary = tonalColors(hue.toDouble(), secondarySat, if (isDark) SECONDARY_TONES_DARK else SECONDARY_TONES_LIGHT)
    val tertiary = tonalColors(tertiaryHue, tertiarySat, if (isDark) TERTIARY_TONES_DARK else TERTIARY_TONES_LIGHT)
    val error = tonalColors(25.0, 0.84, if (isDark) ERROR_TONES_DARK else ERROR_TONES_LIGHT)
    val neutral = tonalColors(hue.toDouble(), neutralSat, if (isDark) NEUTRAL_TONES_DARK else NEUTRAL_TONES_LIGHT)
    val neutralVariant = tonalColors(hue.toDouble(), neutralVariantSat, if (isDark) NEUTRAL_VARIANT_TONES_DARK else NEUTRAL_VARIANT_TONES_LIGHT)

    return if (isDark) {
        darkColorScheme(
            primary = primary[0],
            onPrimary = primary[1],
            primaryContainer = primary[2],
            onPrimaryContainer = primary[3],
            secondary = secondary[0],
            onSecondary = secondary[1],
            secondaryContainer = secondary[2],
            onSecondaryContainer = secondary[3],
            tertiary = tertiary[0],
            onTertiary = tertiary[1],
            tertiaryContainer = tertiary[2],
            onTertiaryContainer = tertiary[3],
            error = error[0],
            onError = error[1],
            errorContainer = error[2],
            onErrorContainer = error[3],
            background = neutral[0],
            onBackground = neutral[1],
            surface = neutral[0],
            onSurface = neutral[1],
            surfaceVariant = neutralVariant[0],
            onSurfaceVariant = neutralVariant[1],
            outline = neutralVariant[2],
            outlineVariant = neutralVariant[3],
            inverseSurface = color(hue.toDouble(), neutralSat, 90.0),
            inverseOnSurface = color(hue.toDouble(), neutralSat, 20.0),
            inversePrimary = color(hue.toDouble(), primarySat, 40.0),
            surfaceTint = primary[0],
        )
    } else {
        lightColorScheme(
            primary = primary[0],
            onPrimary = primary[1],
            primaryContainer = primary[2],
            onPrimaryContainer = primary[3],
            secondary = secondary[0],
            onSecondary = secondary[1],
            secondaryContainer = secondary[2],
            onSecondaryContainer = secondary[3],
            tertiary = tertiary[0],
            onTertiary = tertiary[1],
            tertiaryContainer = tertiary[2],
            onTertiaryContainer = tertiary[3],
            error = error[0],
            onError = error[1],
            errorContainer = error[2],
            onErrorContainer = error[3],
            background = neutral[0],
            onBackground = neutral[1],
            surface = neutral[0],
            onSurface = neutral[1],
            surfaceVariant = neutralVariant[0],
            onSurfaceVariant = neutralVariant[1],
            outline = neutralVariant[2],
            outlineVariant = neutralVariant[3],
            inverseSurface = color(hue.toDouble(), neutralSat, 20.0),
            inverseOnSurface = color(hue.toDouble(), neutralSat, 95.0),
            inversePrimary = color(hue.toDouble(), primarySat, 80.0),
            surfaceTint = primary[0],
        )
    }
}

private fun toneToLightness(tone: Double): Double {
    return tone / 100.0
}
