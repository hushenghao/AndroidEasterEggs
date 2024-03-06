package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StyleRes
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.compose.ThemePrefUtil
import com.google.android.material.color.ThemeUtils
import com.google.android.material.internal.ContextUtils
import com.google.android.material.resources.MaterialAttributes


object ThemeUtils {

    fun isSystemNightMode(context: Context): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
    }

    @SuppressLint("RestrictedApi")
    fun applyThemeOverlay(context: Context, @StyleRes theme: Int) {
        ThemeUtils.applyThemeOverlay(context, theme)
    }

    @SuppressLint("RestrictedApi")
    fun isOLEDTheme(context: Context): Boolean {
        return MaterialAttributes.resolveBoolean(context, R.attr.isOLEDTheme, false)
    }

    fun tryApplyOLEDTheme(context: Context) {
        if (ThemePrefUtil.isOLEDMode(context)) {
            applyThemeOverlay(context, R.style.ThemeOverlay_EasterEggs_OLED)
        }
    }

    @SuppressLint("RestrictedApi")
    fun recreateActivityIfPossible(context: Context) {
        val activity = ContextUtils.getActivity(context)
        activity?.recreate()
    }
}