@file:JvmName("SpUtils")
@file:JvmMultifileClass

package com.dede.basic


import android.content.Context
import androidx.core.content.edit
import com.dede.android_eggs.util.pref

/**
 * SharedPreferences Utils
 *
 * Only the string-keyed Long accessors live here: the AOSP-derived
 * `PlatLogoActivity` classes reach them through `SpUtils.getLong` / `putLong`.
 * Everything else goes through `PrefKey.get` / `PrefKey.set` in
 * `com.dede.android_eggs.preferences`.
 *
 * @author hsh
 * @since 2020/10/20 3:01 PM
 */

fun Context.getLong(key: String, default: Long): Long {
    return pref.getLong(key, default)
}

fun Context.putLong(key: String, value: Long) {
    pref.edit { putLong(key, value) }
}
