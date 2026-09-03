package com.dede.android_eggs.views.settings.compose.prefs

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.dede.android_eggs.views.settings.compose.basic.mutablePrefIntState


object ThemePrefUtil {

    const val AMOLED = -2
    const val LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val DARK = AppCompatDelegate.MODE_NIGHT_YES
    const val FOLLOW_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    const val KEY_NIGHT_MODE = "pref_key_night_mode"

    const val ACTION_NIGHT_MODE_CHANGED = "action_night_mode_changed"

    fun getThemeModeValue(): Int {
        return themeModeState.intValue
    }

    val themeModeState = mutablePrefIntState(KEY_NIGHT_MODE, FOLLOW_SYSTEM)

    fun apply(context: Context) {
        var mode = getThemeModeValue()
        if (mode == AMOLED) {
            mode = DARK
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        context.applyApplicationNightMode(mode)
    }
}

/**
 * Mirror the night mode to the system-level per-app night mode (Android 12+), so the
 * system splash screen resolves resources with the same night qualifier as the in-app
 * preference. [UiModeManager.MODE_NIGHT_AUTO] clears the per-app override to follow
 * the system setting.
 */
fun Context.applyApplicationNightMode(appCompatMode: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return
    }
    val uiModeManager = getSystemService(UiModeManager::class.java) ?: return
    val mode = when (appCompatMode) {
        AppCompatDelegate.MODE_NIGHT_YES -> UiModeManager.MODE_NIGHT_YES
        AppCompatDelegate.MODE_NIGHT_NO -> UiModeManager.MODE_NIGHT_NO
        else -> UiModeManager.MODE_NIGHT_AUTO
    }
    uiModeManager.setApplicationNightMode(mode)
}
