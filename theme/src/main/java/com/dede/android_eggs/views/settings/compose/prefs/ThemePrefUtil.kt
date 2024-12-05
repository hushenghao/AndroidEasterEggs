package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import com.dede.android_eggs.util.pref


object ThemePrefUtil {

    const val AMOLED = -2
    const val LIGHT = 1         //AppCompatDelegate.MODE_NIGHT_NO
    const val DARK = 2          //AppCompatDelegate.MODE_NIGHT_YES
    const val FOLLOW_SYSTEM = -1//AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    const val KEY_NIGHT_MODE = "pref_key_night_mode"

    const val ACTION_NIGHT_MODE_CHANGED = "action_night_mode_changed"

    fun getThemeModeValue(context: Context): Int {
        return context.pref.getInt(KEY_NIGHT_MODE, FOLLOW_SYSTEM)
    }

}
