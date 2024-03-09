package com.dede.android_eggs.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

val Context.pref: SharedPreferences
    get() {
        return applicationContext.getSharedPreferences(
            applicationContext.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
//        return PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

fun <T : Preference> PreferenceFragmentCompat.requirePreference(key: String): T {
    return checkNotNull(findPreference(key))
}