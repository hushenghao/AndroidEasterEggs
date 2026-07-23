package com.dede.android_eggs.views.theme

import android.content.Context
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.dede.android_eggs.views.settings.compose.prefs.ColorSourcePrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.materialkolor.dynamicColorScheme

private const val TAG = "Theme"

@Composable
fun rememberEasterEggColorScheme(
    themeMode: Int,
    colorSource: Int,
    seedColor: Color,
): ColorScheme {
    val isDark = when (themeMode) {
        ThemePrefUtil.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemePrefUtil.AMOLED,
        ThemePrefUtil.DARK -> true
        ThemePrefUtil.LIGHT -> false
        else -> {
            Log.w(TAG, "rememberEasterEggColorScheme: Unknown theme mode $themeMode")
            false
        }
    }
    val context: Context = LocalContext.current
    return remember(isDark, colorSource, seedColor) {
        val colorScheme = when (colorSource) {
            ColorSourcePrefUtil.SOURCE_DEFAULT -> {
                dynamicColorScheme(seedColor = defaultSeedColor, isDark = isDark)
            }
            ColorSourcePrefUtil.SOURCE_CUSTOM -> {
                dynamicColorScheme(seedColor = seedColor, isDark = isDark)
            }
            ColorSourcePrefUtil.SOURCE_DYNAMIC -> {
                if (ColorSourcePrefUtil.isDynamicColorSupported()) {
                    if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                } else {
                    Log.w(TAG, "rememberEasterEggColorScheme: Dynamic color is not supported")
                    dynamicColorScheme(seedColor = defaultSeedColor, isDark = isDark)
                }
            }
            else -> {
                Log.w(TAG, "rememberEasterEggColorScheme: Unknown color source $colorSource")
                dynamicColorScheme(seedColor = defaultSeedColor, isDark = isDark)
            }
        }
        if (themeMode == ThemePrefUtil.AMOLED) {
            colorScheme.toAmoled()
        } else {
            colorScheme
        }
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

internal var currentColorScheme: ColorScheme = lightColorScheme()
    private set

@Composable
fun EasterEggsTheme(
    content: @Composable () -> Unit
) {
    val currentThemeMode by ThemePrefUtil.themeModeState
    val currentSource by ColorSourcePrefUtil.sourceState
    val currentSeedColor by ColorSourcePrefUtil.seedColorState
    EasterEggsTheme(
        themeMode = currentThemeMode,
        colorSource = currentSource,
        seedColor = currentSeedColor,
        content = content,
    )
}

@Composable
fun EasterEggsTheme(
    themeMode: Int,
    colorSource: Int,
    seedColor: Color,
    updateGlobalColorScheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colors = rememberEasterEggColorScheme(themeMode, colorSource, seedColor)

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
