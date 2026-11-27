package com.dede.android_eggs.settings_ui.basic

import com.dede.android_eggs.preferences.AppSettings

object SettingPrefUtil {
    const val ON = PREF_ON
    const val OFF = PREF_OFF

    val iconVisualEffectsState = mutablePrefIntState(AppSettings.iconVisualEffects)

    /** Shared by the settings slider and the home list so slider drags recompose the home list. */
    val highestEggCountState = mutablePrefIntState(AppSettings.highestEggCount)

    val hazeBlurState = mutablePrefBoolState(AppSettings.hazeBlur)

    val eggItemBackdropBlurState = mutablePrefBoolState(AppSettings.eggItemBackdropBlur)
}
