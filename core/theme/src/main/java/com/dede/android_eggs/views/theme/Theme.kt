package com.dede.android_eggs.views.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.dede.android_eggs.views.settings.compose.prefs.ColorSourcePrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil


@Composable
fun resolveColorScheme(
    themeMode: Int,
    source: Int,
    seedColor: Int,
): ColorScheme {
    val isDark = if (themeMode == ThemePrefUtil.FOLLOW_SYSTEM) {
        isSystemInDarkTheme()
    } else {
        themeMode == ThemePrefUtil.DARK || themeMode == ThemePrefUtil.AMOLED
    }
    val colorScheme = when (source) {
        ColorSourcePrefUtil.SOURCE_CUSTOM -> {
            generateColorSchemeFromSeed(seedColor, isDark)
        }
        ColorSourcePrefUtil.SOURCE_DYNAMIC -> {
            if (ColorSourcePrefUtil.isDynamicColorSupported()) {
                val context: Context = LocalContext.current
                if (isDark) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            } else {
                if (isDark) darkScheme else lightScheme
            }
        }
        else -> {
            if (isDark) darkScheme else lightScheme
        }
    }
    return if (themeMode == ThemePrefUtil.AMOLED) {
        colorScheme.toAmoled()
    } else {
        colorScheme
    }
}

fun ColorScheme.toAmoled(): ColorScheme {
    fun Color.darken(fraction: Float = 0.5f): Color =
        Color(toArgb().blend(Color.Black.toArgb(), fraction))
    return copy(
        primary = primary.darken(0.3f),
        onPrimary = onPrimary.darken(0.3f),
        primaryContainer = primaryContainer.darken(0.3f),
        onPrimaryContainer = onPrimaryContainer.darken(0.3f),
        inversePrimary = inversePrimary.darken(0.3f),
        secondary = secondary.darken(0.3f),
        onSecondary = onSecondary.darken(0.3f),
        secondaryContainer = secondaryContainer.darken(0.3f),
        onSecondaryContainer = onSecondaryContainer.darken(0.3f),
        tertiary = tertiary.darken(0.3f),
        onTertiary = onTertiary.darken(0.3f),
        tertiaryContainer = tertiaryContainer.darken(0.3f),
        onTertiaryContainer = onTertiaryContainer.darken(0.2f),
        background = Color.Black,
        onBackground = onBackground.darken(0.15f),
        surface = Color.Black,
        onSurface = onSurface.darken(0.15f),
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = surfaceTint,
        inverseSurface = inverseSurface.darken(),
        inverseOnSurface = inverseOnSurface.darken(0.2f),
        outline = outline.darken(0.2f),
        outlineVariant = outlineVariant.darken(0.2f),
    )
}

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

internal var currentColorScheme: ColorScheme = lightScheme
    private set

@Composable
fun EasterEggsTheme(
    content: @Composable () -> Unit
) {
    val currentThemeMode by ThemePrefUtil.themeModeState
    val currentColorSource by ColorSourcePrefUtil.colorSourceState
    EasterEggsTheme(
        themeMode = currentThemeMode,
        colorSourcePacked = currentColorSource,
        content = content,
    )
}

@Composable
fun EasterEggsTheme(
    themeMode: Int,
    colorSourcePacked: Int,
    updateGlobalColorScheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorSource = ColorSourcePrefUtil.decodeSource(colorSourcePacked)
    val customSeedColor = ColorSourcePrefUtil.decodeSeedColor(colorSourcePacked)
    val colors = resolveColorScheme(themeMode, colorSource, customSeedColor)

    LaunchedEffect(updateGlobalColorScheme, colors) {
        if (updateGlobalColorScheme) {
            currentColorScheme = colors.copy()
        }
    }
    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
