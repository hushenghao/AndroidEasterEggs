package com.dede.android_eggs.views.settings.prefs

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons.Rounded.brightness_4
import com.dede.android_eggs.ui.Icons.Rounded.brightness_7
import com.dede.android_eggs.ui.Icons.Rounded.brightness_auto
import com.dede.android_eggs.ui.Icons.Rounded.brightness_low
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.settings.SettingPref
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM as SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO as NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES as YES


class NightModePref : SettingPref(
    "pref_key_night_mode",
    listOf(
        Op(NO, titleRes = R.string.summary_theme_light_mode, iconUnicode = brightness_7),
        Op(YES, titleRes = R.string.summary_theme_dark_mode, iconUnicode = brightness_4),
        Op(OLED, title = "OLED", iconUnicode = brightness_low),
        Op(SYSTEM, titleRes = R.string.summary_system_default, iconUnicode = brightness_auto)
    ),
    SYSTEM
) {

    companion object {
        private const val OLED = -2
        const val ACTION_NIGHT_MODE_CHANGED = "action_night_mode_changed"

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

    override fun apply(context: Context, option: Op) {
        val mode = option.toAppCompatNightMode()
        if (mode == AppCompatDelegate.getDefaultNightMode()) {
            if ((option.value == OLED) != ThemeUtils.isOLEDTheme(context)) {
                recreateActivityIfPossible(context)
                LocalEvent.poster(context).post(ACTION_NIGHT_MODE_CHANGED)
            }
            return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        LocalEvent.poster(context).post(ACTION_NIGHT_MODE_CHANGED)
    }
}