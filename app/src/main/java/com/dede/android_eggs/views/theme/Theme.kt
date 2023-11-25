@file:Suppress("PrivatePropertyName")

package com.dede.android_eggs.views.theme

import android.content.Context
import android.os.Build
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.ColorUtils
import com.dede.android_eggs.views.settings.prefs.DynamicColorPref
import com.dede.android_eggs.views.settings.prefs.NightModePref


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
        outlineVariant = outlineVariant.darken(0.2f)
    )
}

fun Int.blend(
    color: Int,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.5f,
): Int = ColorUtils.blendARGB(this, color, fraction)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val context: Context = LocalContext.current
    var nightModeValue = NightModePref.getNightModeValue(context)
    if (nightModeValue == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
        nightModeValue = if (isSystemInDarkTheme())
            AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    }

    val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        DynamicColorPref.isDynamicEnable(context)
    ) {
        when (nightModeValue) {
            NightModePref.OLED -> dynamicDarkColorScheme(context).toAmoled()
            AppCompatDelegate.MODE_NIGHT_YES -> dynamicDarkColorScheme(context)
            else -> dynamicLightColorScheme(context)
        }
    } else {
        when (nightModeValue) {
            NightModePref.OLED -> DarkColorScheme.toAmoled()
            AppCompatDelegate.MODE_NIGHT_YES -> DarkColorScheme
            else -> LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}