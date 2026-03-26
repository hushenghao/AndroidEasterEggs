package com.dede.android_eggs.views.settings.compose.basic

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.core.content.edit
import com.dede.android_eggs.util.PREF_OFF
import com.dede.android_eggs.util.PREF_ON
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil

object SettingPrefUtil {
    const val ON = PREF_ON
    const val OFF = PREF_OFF

    const val EXTRA_VALUE = "extra_value"

    const val KEY_PRIVACY_POLICY_AGREED = "key_welcome_status"

    const val KEY_EGG_ITEM_NEED_GUIDE_SWIPE = "key_egg_item_need_guide_swipe"

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
