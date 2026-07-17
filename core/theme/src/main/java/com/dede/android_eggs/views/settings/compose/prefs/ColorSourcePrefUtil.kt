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
 * - High 8 bits: source type [ColorSource]
 * - Low 24 bits: RGB color value (only meaningful when source is CUSTOM)
 */
object ColorSourcePrefUtil {

    enum class ColorSource(val value: Int) {
        DEFAULT(0),
        DYNAMIC(1),
        CUSTOM(2),
    }

    const val KEY_COLOR_SOURCE = "pref_key_color_source"

    val DEFAULT_SEED_COLOR = seed.toArgb()
    private val DEFAULT_SOURCE = if (isDynamicColorSupported()) ColorSource.DYNAMIC else ColorSource.DEFAULT

    val DEFAULT_VALUE = encode(DEFAULT_SOURCE, DEFAULT_SEED_COLOR)

    val colorSourceState = mutableIntStateOf(DEFAULT_VALUE)

    fun encode(source: ColorSource, seedColor: Int): Int {
        return (source.value and 0xFF shl 24) or (seedColor and 0xFFFFFF)
    }

    fun decodeSource(packed: Int): ColorSource {
        val value = (packed shr 24) and 0xFF
        return ColorSource.entries.firstOrNull { it.value == value } ?: DEFAULT_SOURCE
    }

    fun decodeSeedColor(packed: Int): Int {
        return (packed and 0xFFFFFF) or (0xFF000000.toInt())
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isDynamicColorSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    fun getPackedValue(context: Context): Int {
        return context.pref.getInt(KEY_COLOR_SOURCE, DEFAULT_VALUE)
    }

    fun apply(context: Context) {
        colorSourceState.intValue = getPackedValue(context)
    }
}
