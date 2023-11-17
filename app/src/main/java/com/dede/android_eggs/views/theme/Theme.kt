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

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val context: Context = LocalContext.current
    var nightModeValue = NightModePref.getNightModeValue(context)
    if (nightModeValue == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
        nightModeValue =
            if (isSystemInDarkTheme()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
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
            NightModePref.OLED -> darkColorScheme().toAmoled()
            AppCompatDelegate.MODE_NIGHT_YES -> darkColorScheme()
            else -> lightColorScheme()
        }
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}