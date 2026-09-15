package com.dede.android_eggs.settings_ui.basic

import com.dede.android_eggs.preferences.AppSettings

object SettingPrefUtil {
    const val ON = PREF_ON
    const val OFF = PREF_OFF

    val iconVisualEffectsState = mutablePrefIntState(AppSettings.iconVisualEffects)
}
