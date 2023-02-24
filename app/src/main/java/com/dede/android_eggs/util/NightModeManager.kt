package com.dede.android_eggs.util

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

/**
 * Created by shhu on 2023/2/3 13:41.
 *
 * @author shhu
 * @since 2023/2/3
 */
class NightModeManager(val context: Context) {

    companion object {
        const val KEY_NIGHT_MODE = "key_night_mode"

        fun applyNightMode(context: Context) {
            NightModeManager(context).apply {
                setNightMode(isNightMode())
            }
        }

        fun isSystemNightMode(context: Context): Boolean {
            return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    }

    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    fun isNightMode(): Boolean {
        return pref.getBoolean(KEY_NIGHT_MODE, false)
    }

    fun setNightMode(nightMode: Boolean) {
        val mode = if (nightMode) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        if (mode == AppCompatDelegate.getDefaultNightMode()) {
            return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
        pref.edit().putBoolean(KEY_NIGHT_MODE, nightMode).apply()
    }

    fun isSystemNightMode(): Boolean {
        return Companion.isSystemNightMode(context)
    }

}