package com.dede.android_eggs.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.toArgb
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.android_eggs.views.theme.currentColorScheme

const val PREF_ON = 1
const val PREF_OFF = 0

object ThemeUtils {

    private fun isSystemNightMode(resources: Resources): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
    }

    fun isDarkMode(context: Context): Boolean {
        val themeMode = ThemePrefUtil.getThemeModeValue(context)
        return themeMode == ThemePrefUtil.DARK ||
                themeMode == ThemePrefUtil.AMOLED ||
                (themeMode == ThemePrefUtil.FOLLOW_SYSTEM && isSystemNightMode(context.resources))
    }

    @ColorInt
    fun getThemedSurfaceColor(): Int {
        return currentColorScheme.surface.toArgb()
    }
}
