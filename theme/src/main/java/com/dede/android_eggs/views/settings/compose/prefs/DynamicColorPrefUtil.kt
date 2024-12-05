package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil

object DynamicColorPrefUtil {

    val DEFAULT = if (isSupported()) SettingPrefUtil.ON else SettingPrefUtil.OFF
    const val KEY_DYNAMIC_COLOR = "pref_key_dynamic_color"

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isDynamicColorEnable(context: Context): Boolean {
        return isSupported() &&
                SettingPrefUtil.getValue(context, KEY_DYNAMIC_COLOR, DEFAULT) == SettingPrefUtil.ON
    }

}