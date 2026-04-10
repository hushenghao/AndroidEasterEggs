package com.dede.android_eggs.views.settings.compose.basic

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import com.dede.android_eggs.util.PREF_OFF
import com.dede.android_eggs.util.PREF_ON
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil

object SettingPrefUtil {
    const val ON = PREF_ON
    const val OFF = PREF_OFF

    const val KEY_PRIVACY_POLICY_AGREED = "key_welcome_status"

    const val KEY_EGG_ITEM_NEED_GUIDE_SWIPE = "key_egg_item_need_guide_swipe"

    const val KEY_ICON_VISUAL_EFFECTS = "pref_key_icon_visual_effects"

    fun getValue(context: Context, key: String, default: Int): Int {
        return context.pref.getInt(key, default)
    }

    internal fun getBooleanValue(context: Context, key: String, default: Int): Boolean {
        return getValue(context, key, default) == ON
    }

    fun setValue(context: Context, key: String, value: Int) {
        context.pref.edit { putInt(key, value) }
    }

    val iconShapeValueState = mutableIntStateOf(OFF)
    val iconVisualEffectsState = mutableStateOf(false)

    fun MutableState<Boolean>.setBooleanValue(int: Int) {
        value = int == ON
    }

    fun setup(context: Context) {
        iconShapeValueState.intValue = getValue(context, IconShapePrefUtil.KEY_ICON_SHAPE, OFF)
        iconVisualEffectsState.value = getBooleanValue(context, KEY_ICON_VISUAL_EFFECTS, OFF)
    }
}
