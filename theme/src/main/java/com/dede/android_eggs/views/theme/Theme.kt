@file:Suppress("PrivatePropertyName")

package com.dede.android_eggs.views.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.basic.globalContext


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

private fun ColorScheme.toXml() {
    fun Color.toHexColor(): String {
        return toArgb().toUInt().toString(16)
    }

    fun item(name: String, color: Color): String {
        return "<item name=\"$name\">#${color.toHexColor()}</item>"
    }

    val methods = ColorScheme::class.java.methods
    for (method in methods) {
        var methodName = method.name
        if (method.returnType != Long::class.java || !methodName.startsWith("get")) {
            continue
        }
        // getOnPrimary-0d7_KjU(): long
        methodName = "color" + methodName.split("-")[0].replace("get", "")
        if (methodName == "colorScrim" || methodName == "colorSurfaceTint") {
            continue
        }
        if (methodName.contains("Inverse")) {
            methodName = methodName.replace("Inverse", "") + "Inverse"
        }
        if (methodName == "colorBackground") {
            methodName = "android:$methodName"
        }
        val color = Color((method.invoke(this) as Long).toULong())
        println(item(methodName, color))
        println()
    }
}

var themeMode by mutableIntStateOf(ThemePrefUtil.getThemeModeValue(globalContext))
var isDynamicColorEnable by mutableStateOf(DynamicColorPrefUtil.isDynamicColorEnable(globalContext))

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    var nightModeValue = themeMode
    if (nightModeValue == ThemePrefUtil.FOLLOW_SYSTEM) {
        nightModeValue = if (isSystemInDarkTheme()) ThemePrefUtil.DARK else ThemePrefUtil.LIGHT
    }

    val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDynamicColorEnable) {
        val context: Context = LocalContext.current
        when (nightModeValue) {
            ThemePrefUtil.AMOLED -> dynamicDarkColorScheme(context).toAmoled()
            ThemePrefUtil.DARK -> dynamicDarkColorScheme(context)
            else -> dynamicLightColorScheme(context)
        }
    } else {
        when (nightModeValue) {
            ThemePrefUtil.AMOLED -> darkScheme.toAmoled()
            ThemePrefUtil.DARK -> darkScheme
            else -> lightScheme
        }
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}