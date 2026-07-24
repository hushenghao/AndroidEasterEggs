package com.dede.android_eggs.views.settings.compose.basic

import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil

object SettingPrefUtil {
    const val ON = PREF_ON
    const val OFF = PREF_OFF

    const val KEY_PRIVACY_POLICY_AGREED = "key_welcome_status"

    const val KEY_EGG_ITEM_NEED_GUIDE_SWIPE = "key_egg_item_need_guide_swipe"

    const val KEY_ICON_VISUAL_EFFECTS = "pref_key_icon_visual_effects"

    val iconShapeValueState = mutablePrefIntState(IconShapePrefUtil.KEY_ICON_SHAPE, OFF)
    val iconVisualEffectsState = mutablePrefIntState(KEY_ICON_VISUAL_EFFECTS, OFF)
}
