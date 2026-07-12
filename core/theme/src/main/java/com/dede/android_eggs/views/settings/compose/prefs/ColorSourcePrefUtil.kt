package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.toArgb
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.theme.seed

/**
 * Manages color source preference using a single int encoding:
 * - High 8 bits: source type (SOURCE_DEFAULT, SOURCE_DYNAMIC, SOURCE_CUSTOM)
 * - Low 24 bits: RGB color value (only meaningful when source is CUSTOM)
 */
object ColorSourcePrefUtil {

    const val SOURCE_DEFAULT = 0
    const val SOURCE_DYNAMIC = 1
    const val SOURCE_CUSTOM = 2

    const val KEY_COLOR_SOURCE = "pref_key_color_source"

    val DEFAULT_SEED_COLOR = seed.toArgb()
    val DEFAULT_SOURCE = if (isDynamicColorSupported()) SOURCE_DYNAMIC else SOURCE_DEFAULT

    val colorSourceState = mutableIntStateOf(encode(DEFAULT_SOURCE, DEFAULT_SEED_COLOR))

    fun encode(source: Int, seedColor: Int): Int {
        return (source and 0xFF shl 24) or (seedColor and 0xFFFFFF)
    }

    fun decodeSource(packed: Int): Int {
        return (packed shr 24) and 0xFF
    }

    fun decodeSeedColor(packed: Int): Int {
        return (packed and 0xFFFFFF) or (0xFF000000.toInt())
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isDynamicColorSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    fun getPackedValue(context: Context): Int {
        return context.pref.getInt(KEY_COLOR_SOURCE, encode(DEFAULT_SOURCE, DEFAULT_SEED_COLOR))
    }

    fun apply(context: Context) {
        colorSourceState.intValue = getPackedValue(context)
    }
}
