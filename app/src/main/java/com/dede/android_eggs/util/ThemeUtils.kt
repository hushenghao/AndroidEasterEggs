package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StyleRes
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.prefs.NightModePref
import com.google.android.material.color.ThemeUtils
import com.google.android.material.resources.MaterialAttributes


object ThemeUtils {

    @SuppressLint("RestrictedApi")
    fun applyThemeOverlay(context: Context, @StyleRes theme: Int) {
        ThemeUtils.applyThemeOverlay(context, theme)
    }

    @SuppressLint("RestrictedApi")
    fun isOLEDTheme(context: Context): Boolean {
        return MaterialAttributes.resolveBoolean(context, R.attr.isOLEDTheme, false)
    }

    fun tryApplyOLEDTheme(context: Context) {
        if (NightModePref.isOLEDMode(context)) {
            applyThemeOverlay(context, R.style.ThemeOverlay_EasterEggs_OLED)
        }
    }
}