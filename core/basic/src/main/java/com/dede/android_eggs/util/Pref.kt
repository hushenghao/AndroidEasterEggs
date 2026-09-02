package com.dede.android_eggs.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

fun makePreferencesName(packageName: String): String {
    return packageName + "_preferences"
}

val Context.pref: SharedPreferences
    get() {
        return applicationContext.getSharedPreferences(
            makePreferencesName(applicationContext.packageName),
            Context.MODE_PRIVATE
        )
    }

fun SharedPreferences.flushPendingWrites() {
    val entries = all.toMap()
    edit(commit = true) {
        clear()
        for ((key, value) in entries) {
            writeValue(this, key, value)
        }
    }
}

private fun writeValue(editor: SharedPreferences.Editor, key: String, value: Any?) {
    when (value) {
        is Boolean -> editor.putBoolean(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
        is Float -> editor.putFloat(key, value)
        is String -> editor.putString(key, value)
        is Set<*> -> {
            @Suppress("UNCHECKED_CAST")
            editor.putStringSet(key, value as Set<String>)
        }
    }
}
