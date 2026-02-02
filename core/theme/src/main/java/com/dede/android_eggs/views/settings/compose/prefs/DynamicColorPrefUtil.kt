package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.runtime.mutableStateOf
import com.dede.android_eggs.util.PREF_OFF
import com.dede.android_eggs.util.PREF_ON
import com.dede.android_eggs.util.pref

object DynamicColorPrefUtil {

    val DEFAULT = if (isSupported()) PREF_ON else PREF_OFF
    const val KEY_DYNAMIC_COLOR = "pref_key_dynamic_color"

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isDynamicColorEnable(context: Context): Boolean {
        return isSupported() && context.pref.getInt(KEY_DYNAMIC_COLOR, DEFAULT) == PREF_ON
    }

    val isDynamicColorEnabledState = mutableStateOf(DEFAULT == PREF_ON)

    fun apply(context: Context) {
        isDynamicColorEnabledState.value = isDynamicColorEnable(context)
    }

}