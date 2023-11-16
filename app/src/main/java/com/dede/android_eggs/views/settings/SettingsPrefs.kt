package com.dede.android_eggs.views.settings

import android.content.Context
import com.dede.android_eggs.views.settings.prefs.DynamicColorPref
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.android_eggs.views.settings.prefs.IconVisualEffectsPref
import com.dede.android_eggs.views.settings.prefs.LanguagePref
import com.dede.android_eggs.views.settings.prefs.NightModePref

/**
 * All Setting Prefs
 */
object SettingsPrefs {

    const val ACTION_CLOSE_SETTING = "com.dede.easter_eggs.CloseSetting"

    private val dynamicColorPref = DynamicColorPref()
    private val nightModePref = NightModePref()

    fun providerPrefs(): List<SettingPref> {
        return listOf(
            nightModePref,
            LanguagePref(),
            IconShapePref(),
            IconVisualEffectsPref(),
            dynamicColorPref,
        )
    }

    fun apply(context: Context) {
        nightModePref.apply(context)
        dynamicColorPref.apply(context)
    }
}