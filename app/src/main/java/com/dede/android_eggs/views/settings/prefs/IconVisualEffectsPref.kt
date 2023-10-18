package com.dede.android_eggs.views.settings.prefs

import android.content.Context
import androidx.core.os.bundleOf
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.settings.SettingPref
import com.dede.android_eggs.views.settings.SettingPref.Op.Companion.isEnable
import com.dede.android_eggs.views.settings.SettingsPrefs


class IconVisualEffectsPref : SettingPref(
    "pref_key_icon_visual_effects",
    listOf(
        Op(Op.ON, titleRes = R.string.preference_on, iconUnicode = Icons.Outlined.animation),
        Op(Op.OFF, titleRes = R.string.preference_off)
    ),
    Op.OFF
) {
    companion object {
        const val ACTION_CHANGED = "com.dede.android_eggs.IconVisualEffectsChanged"
        const val EXTRA_VALUE = "extra_value"

        fun isEnable(context: Context): Boolean {
            return IconVisualEffectsPref().getSelectedOption(context).isEnable()
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_icon_visual_effects

    override fun apply(context: Context, option: Op) {
        LocalEvent.poster(context).apply {
            post(SettingsPrefs.ACTION_CLOSE_SETTING)
            post(ACTION_CHANGED, bundleOf(EXTRA_VALUE to option.isEnable()))
        }
    }
}
