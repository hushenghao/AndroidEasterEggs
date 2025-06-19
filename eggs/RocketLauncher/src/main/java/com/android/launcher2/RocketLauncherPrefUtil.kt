package com.android.launcher2

import android.content.Context
import com.dede.android_eggs.util.pref

object RocketLauncherPrefUtil {

    const val VALUE_EASTER_EGG_ICONS = 0//SettingPrefUtil.OFF
    const val VALUE_ALL_APP_ICONS = 1
    const val VALUE_ALL_ICONS = 2

    const val VALUE_DEFAULT = VALUE_EASTER_EGG_ICONS

    const val KEY_ROCKET_LAUNCHER_ICONS_SOURCE = "pref_key_rocket_launcher_icons_source"

    fun getCurrentIconsSourceValue(context: Context): Int {
        return context.pref.getInt(KEY_ROCKET_LAUNCHER_ICONS_SOURCE, VALUE_DEFAULT)
    }
}
