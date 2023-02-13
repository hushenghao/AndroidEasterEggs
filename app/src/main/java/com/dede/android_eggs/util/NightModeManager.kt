package com.dede.android_eggs.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.dede.basic.getBoolean
import com.dede.basic.putBoolean

/**
 * Created by shhu on 2023/2/3 13:41.
 *
 * @author shhu
 * @since 2023/2/3
 */
class NightModeManager(val context: Context) {

    companion object {
        private const val KEY_NIGHT_MODE = "key_night_mode"

        fun applyNightMode(context: Context) {
            NightModeManager(context).apply {
                setNightMode(isNightMode())
            }
        }
    }

    fun isNightMode(): Boolean {
        return context.getBoolean(KEY_NIGHT_MODE, false)
    }

    fun setNightMode(nightMode: Boolean) {
        val mode = if (nightMode) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        if (mode == AppCompatDelegate.getDefaultNightMode()) {
            return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        context.putBoolean(KEY_NIGHT_MODE, nightMode)
    }

    fun isSystemNightMode(): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

}