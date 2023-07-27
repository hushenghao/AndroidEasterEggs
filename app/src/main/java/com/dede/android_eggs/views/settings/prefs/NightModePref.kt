package com.dede.android_eggs.views.settings.prefs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons.Rounded.brightness_4
import com.dede.android_eggs.ui.Icons.Rounded.brightness_7
import com.dede.android_eggs.ui.Icons.Rounded.brightness_auto
import com.dede.android_eggs.ui.Icons.Rounded.brightness_low
import com.dede.android_eggs.util.getActivity
import com.dede.android_eggs.views.settings.SettingPref
import com.google.android.material.resources.MaterialAttributes
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM as SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO as NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES as YES


class NightModePref : SettingPref(
    "pref_key_night_mode",
    listOf(
        Op(SYSTEM, titleRes = R.string.summary_follow_system, iconUnicode = brightness_auto),
        Op(NO, titleRes = R.string.summary_theme_light_mode, iconUnicode = brightness_7),
        Op(YES, titleRes = R.string.summary_theme_dark_mode, iconUnicode = brightness_4),
        Op(OLED, title = "OLED", iconUnicode = brightness_low)
    ),
    SYSTEM
) {

    companion object {
        private const val OLED = -2

        fun isOLEDMode(context: Context): Boolean {
            return NightModePref().getValue(context, SYSTEM) == OLED
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_theme

    private fun Op.toAppCompatNightMode(): Int {
        if (value == OLED) return YES
        return value
    }

    @SuppressLint("RestrictedApi")
    private fun isOLEDTheme(context: Context): Boolean {
        return MaterialAttributes.resolveBoolean(context, R.attr.isOLEDTheme, false)
    }

    override fun onOptionSelected(context: Context, option: Op) {
        val mode = option.toAppCompatNightMode()
        if (mode == AppCompatDelegate.getDefaultNightMode()) {
            if ((option.value == OLED) != isOLEDTheme(context)) {
                context.getActivity<Activity>()?.recreate()
            }
            return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}