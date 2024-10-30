package com.dede.android_eggs.views.settings.compose.basic

import android.content.Context
import com.dede.android_eggs.util.pref

object SettingPrefUtil {
    const val ON = 1
    const val OFF = 0

    const val EXTRA_VALUE = "extra_value"

    const val ACTION_CLOSE_SETTING = "com.dede.easter_eggs.CloseSetting"

    fun getValue(context: Context, key: String, default: Int): Int {
        return context.pref.getInt(key, default)
    }

    fun setValue(context: Context, key: String, value: Int) {
        context.pref.edit().putInt(key, value).apply()
    }
}
