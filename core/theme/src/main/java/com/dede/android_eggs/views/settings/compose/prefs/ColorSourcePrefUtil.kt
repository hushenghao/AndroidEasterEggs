package com.dede.android_eggs.views.settings.compose.prefs

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import com.dede.android_eggs.views.settings.compose.basic.mutablePrefColorState
import com.dede.android_eggs.views.settings.compose.basic.mutablePrefIntState
import com.dede.android_eggs.views.theme.defaultSeedColor

object ColorSourcePrefUtil {

    const val SOURCE_DEFAULT = 0
    const val SOURCE_DYNAMIC = 1
    const val SOURCE_CUSTOM = 2

    const val KEY_COLOR_SOURCE = "pref_key_color_source_1"
    const val KEY_SEED_COLOR = "pref_key_seed_color"

    val DEFAULT_SOURCE = if (isDynamicColorSupported()) SOURCE_DYNAMIC else SOURCE_DEFAULT

    val colorSourceState = mutablePrefIntState(KEY_COLOR_SOURCE, DEFAULT_SOURCE)
    val seedColorState = mutablePrefColorState(KEY_SEED_COLOR, defaultSeedColor)

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isDynamicColorSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}
