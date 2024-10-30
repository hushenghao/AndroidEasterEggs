package com.dede.android_eggs.views.settings.compose.prefs

import android.app.Activity
import android.app.Application
import android.content.Context
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions

object DynamicColorPrefUtil {

    val DEFAULT =
        if (DynamicColors.isDynamicColorAvailable()) SettingPrefUtil.ON else SettingPrefUtil.OFF
    const val KEY_DYNAMIC_COLOR = "pref_key_dynamic_color"
    const val ACTION_DYNAMIC_COLOR_CHANGED = "ACTION_DYNAMIC_COLOR_CHANGED"

    fun isSupported(): Boolean {
        return DynamicColors.isDynamicColorAvailable()
    }

    fun isDynamicColorEnable(context: Context): Boolean {
        return SettingPrefUtil.getValue(context, KEY_DYNAMIC_COLOR, DEFAULT) == SettingPrefUtil.ON
    }

    fun apply(context: Context) {
        if (!isSupported()) return

        val callback = Callback()
        DynamicColors.applyToActivitiesIfAvailable(
            context.applicationContext as Application,
            DynamicColorsOptions.Builder()
                .setPrecondition(callback)
                .setOnAppliedCallback(callback)
                .build()
        )
    }

    private class Callback : DynamicColors.Precondition, DynamicColors.OnAppliedCallback {
        override fun shouldApplyDynamicColors(activity: Activity, theme: Int): Boolean {
            return ThemeUtils.isMaterial3Theme(activity) && isDynamicColorEnable(activity)
        }

        override fun onApplied(activity: Activity) {
            HarmonizedColors.applyToContextIfAvailable(
                activity, HarmonizedColorsOptions.createMaterialDefaults()
            )
        }

    }
}