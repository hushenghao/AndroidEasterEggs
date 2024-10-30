package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StyleRes
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.android_eggs.views.theme.R
import com.google.android.material.color.ThemeUtils
import com.google.android.material.internal.ContextUtils
import com.google.android.material.resources.MaterialAttributes
import com.google.android.material.R as M3R


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

    @SuppressLint("RestrictedApi", "PrivateResource")
    fun isMaterial3Theme(context: Context): Boolean {
        return MaterialAttributes.resolveBoolean(context, M3R.attr.isMaterial3Theme, false)
    }

    fun tryApplyOLEDTheme(context: Context) {
        if (ThemePrefUtil.isAmoledMode(context)) {
            applyThemeOverlay(context, R.style.ThemeOverlay_OLED)
        }
    }

    @SuppressLint("RestrictedApi")
    fun recreateActivityIfPossible(context: Context) {
        val activity = ContextUtils.getActivity(context)
        activity?.recreate()
    }
}