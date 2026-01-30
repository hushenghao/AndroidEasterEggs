package com.dede.android_eggs.views.settings.compose.basic

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.core.content.edit
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil

object SettingPrefUtil {
    const val ON = 1
    const val OFF = 0

    const val EXTRA_VALUE = "extra_value"

    const val ACTION_CLOSE_SETTING = "com.dede.easter_eggs.CloseSetting"

    fun getValue(context: Context, key: String, default: Int): Int {
        return context.pref.getInt(key, default)
    }

    fun setValue(context: Context, key: String, value: Int) {
        context.pref.edit { putInt(key, value) }
    }

    val iconShapeValueState = mutableIntStateOf(OFF)

    fun setup(context: Context) {
        iconShapeValueState.intValue = getValue(context, IconShapePrefUtil.KEY_ICON_SHAPE, OFF)
    }
}
