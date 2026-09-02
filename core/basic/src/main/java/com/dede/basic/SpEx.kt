@file:JvmName("SpUtils")
@file:JvmMultifileClass

package com.dede.basic


import android.content.Context
import androidx.core.content.edit
import com.dede.android_eggs.util.pref

/**
 * SharedPreferences Utils
 *
 * @author hsh
 * @since 2020/10/20 3:01 PM
 */

@JvmOverloads
fun Context.getString(key: String, default: String? = null): String? {
    return pref.getString(key, default)
}

fun Context.putString(key: String, value: String?) {
    pref.edit { putString(key, value) }
}

fun Context.getLong(key: String, default: Long): Long {
    return pref.getLong(key, default)
}

fun Context.putLong(key: String, value: Long) {
    pref.edit { putLong(key, value) }
}

fun Context.getBoolean(key: String, default: Boolean): Boolean {
    return pref.getBoolean(key, default)
}

fun Context.putBoolean(key: String, value: Boolean) {
    pref.edit { putBoolean(key, value) }
}
