package com.dede.android_eggs.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

val Context.pref: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(applicationContext)

fun <T : Preference> PreferenceFragmentCompat.requirePreference(key: String): T {
    return checkNotNull(findPreference(key))
}