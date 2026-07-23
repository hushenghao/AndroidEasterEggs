package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.theme.defaultSeedColor

object ColorSourcePrefUtil {

    const val SOURCE_DEFAULT = 0
    const val SOURCE_DYNAMIC = 1
    const val SOURCE_CUSTOM = 2

    const val KEY_COLOR_SOURCE = "pref_key_color_source"
    const val KEY_SEED_COLOR = "pref_key_seed_color"

    val DEFAULT_SOURCE = if (isDynamicColorSupported()) SOURCE_DYNAMIC else SOURCE_DEFAULT

    val sourceState = mutableIntStateOf(DEFAULT_SOURCE)
    val seedColorState = mutableStateOf(defaultSeedColor)

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isDynamicColorSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    fun getSourceValue(context: Context): Int {
        return context.pref.getInt(KEY_COLOR_SOURCE, DEFAULT_SOURCE)
    }

    fun getSeedColor(context: Context): Color {
        return Color(context.pref.getInt(KEY_SEED_COLOR, defaultSeedColor.toArgb()))
    }

    fun apply(context: Context) {
        sourceState.intValue = getSourceValue(context)
        seedColorState.value = getSeedColor(context)
    }
}
