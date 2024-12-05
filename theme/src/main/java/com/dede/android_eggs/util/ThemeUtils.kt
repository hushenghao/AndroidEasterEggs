package com.dede.android_eggs.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.toArgb
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.android_eggs.views.theme.isDynamicColorEnable
import com.dede.android_eggs.views.theme.surfaceDark
import com.dede.android_eggs.views.theme.surfaceLight
import com.dede.android_eggs.views.theme.themeMode


object ThemeUtils {

    private fun isSystemNightMode(resources: Resources): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
    }

    fun isDarkMode(resources: Resources): Boolean {
        val currentThemeMode = themeMode
        return currentThemeMode == ThemePrefUtil.DARK ||
                currentThemeMode == ThemePrefUtil.AMOLED ||
                (currentThemeMode == ThemePrefUtil.FOLLOW_SYSTEM && isSystemNightMode(resources))
    }

    fun enableEdgeToEdge(activity: ComponentActivity) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT, Color.TRANSPARENT
            ) { resources -> isDarkMode(resources) },
        )

        LocalEvent.receiver(activity).register(ThemePrefUtil.ACTION_NIGHT_MODE_CHANGED) {
            enableEdgeToEdge(activity)
        }
    }

    @ColorInt
    fun getThemedSurfaceColor(context: Context): Int {
        val themeModeValue = ThemePrefUtil.getThemeModeValue(context)
        if (DynamicColorPrefUtil.isSupported() && isDynamicColorEnable) {
            return when (themeModeValue) {
                ThemePrefUtil.LIGHT -> dynamicLightColorScheme(context).surface.toArgb()
                ThemePrefUtil.DARK -> dynamicDarkColorScheme(context).surface.toArgb()
                ThemePrefUtil.FOLLOW_SYSTEM -> {
                    if (isSystemNightMode(context.resources)) {
                        dynamicDarkColorScheme(context).surface.toArgb()
                    } else {
                        dynamicLightColorScheme(context).surface.toArgb()
                    }
                }
                else -> Color.WHITE
            }
        }

        return when (themeModeValue) {
            ThemePrefUtil.LIGHT -> surfaceLight.toArgb()
            ThemePrefUtil.DARK -> surfaceDark.toArgb()
            ThemePrefUtil.FOLLOW_SYSTEM -> {
                if (isSystemNightMode(context.resources)) {
                    surfaceDark.toArgb()
                } else {
                    surfaceLight.toArgb()
                }
            }
            else -> Color.WHITE
        }
    }
}
