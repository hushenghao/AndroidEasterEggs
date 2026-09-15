package com.android.launcher2

import android.content.Context
import com.dede.android_eggs.preferences.AppSettings

object RocketLauncherPrefUtil {

    const val VALUE_EASTER_EGG_ICONS = 0//SettingPrefUtil.OFF
    const val VALUE_ALL_APP_ICONS = 1
    const val VALUE_ALL_ICONS = 2

    fun getCurrentIconsSourceValue(context: Context): Int {
        return AppSettings.rocketLauncherIconsSource.get(context)
    }
}
