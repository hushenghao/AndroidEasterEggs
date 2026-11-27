package com.dede.android_eggs.views.theme.settings

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.settings_state.rememberPrefColorState
import com.dede.android_eggs.settings_state.rememberPrefIntState
import com.dede.android_eggs.system_colors.isWallpaperColorSupported

object ColorSourcePrefUtil {

    const val SOURCE_DEFAULT = AppSettings.COLOR_SOURCE_DEFAULT
    const val SOURCE_DYNAMIC = AppSettings.COLOR_SOURCE_DYNAMIC
    const val SOURCE_CUSTOM = AppSettings.COLOR_SOURCE_CUSTOM

    val DEFAULT_SOURCE = if (isDynamicColorSourceSupported()) SOURCE_DYNAMIC else SOURCE_DEFAULT

    @Composable
    fun rememberColorSourceState(): MutableIntState {
        return rememberPrefIntState(AppSettings.colorSource, DEFAULT_SOURCE)
    }

    @Composable
    fun rememberSeedColorState(): MutableState<Color> {
        return rememberPrefColorState(AppSettings.seedColor)
    }

    fun isDynamicColorSourceSupported(): Boolean = isDynamicColorSupported() || isWallpaperColorSupported()

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isDynamicColorSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}
