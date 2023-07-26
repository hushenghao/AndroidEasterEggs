package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.Window
import androidx.annotation.StyleRes
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.prefs.NightModePref
import com.google.android.material.color.ThemeUtils
import com.google.android.material.internal.EdgeToEdgeUtils


object EdgeUtils {

    @SuppressLint("RestrictedApi")
    fun applyEdge(window: Window?) {
        if (window == null) return
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)
    }

    @SuppressLint("RestrictedApi")
    fun applyThemeOverlay(context: Context, @StyleRes theme: Int) {
        ThemeUtils.applyThemeOverlay(context, theme)
    }

    @SuppressLint("RestrictedApi")
    fun tryApplyOLEDTheme(context: Context) {
        if (NightModePref.isOLEDMode(context)) {
            ThemeUtils.applyThemeOverlay(context, R.style.ThemeOverlay_EasterEggs_OLED)
        }
    }
}