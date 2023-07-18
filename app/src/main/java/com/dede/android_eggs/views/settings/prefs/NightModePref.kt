package com.dede.android_eggs.views.settings.prefs

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons.Rounded.brightness_4
import com.dede.android_eggs.ui.Icons.Rounded.brightness_7
import com.dede.android_eggs.ui.Icons.Rounded.brightness_auto
import com.dede.android_eggs.views.settings.SettingPref
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM as SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO as NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES as YES


class NightModePref : SettingPref(
    "pref_key_night_mode",
    listOf(
        Op(SYSTEM, titleRes = R.string.summary_follow_system, iconUnicode = brightness_auto),
        Op(NO, titleRes = R.string.summary_theme_light_mode, iconUnicode = brightness_7),
        Op(YES, titleRes = R.string.summary_theme_dark_mode, iconUnicode = brightness_4)
    ),
    SYSTEM
) {

    override val titleRes: Int
        get() = R.string.pref_title_theme

    override fun onOptionSelected(context: Context, option: Op) {
        val mode = option.value
        if (mode == AppCompatDelegate.getDefaultNightMode()) {
            return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}