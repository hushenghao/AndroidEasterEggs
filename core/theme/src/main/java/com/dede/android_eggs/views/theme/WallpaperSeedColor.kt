package com.dede.android_eggs.views.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import com.dede.android_eggs.system_colors.WallpaperTonalColors
import com.dede.android_eggs.system_colors.isWallpaperColorSupported

/**
 * The seed color extracted from the system wallpaper by [WallpaperTonalColors]
 * during app startup, falls back to [defaultColor] below API 27 or when the system
 * has not computed the wallpaper colors yet, e.g. live wallpapers without
 * [android.service.wallpaper.WallpaperService.Engine.onComputeColors].
 */
@Composable
fun rememberWallpaperSeedColor(defaultColor: Color): Color {
    if (!isWallpaperColorSupported()) {
        return defaultColor
    }
    val seedColor by produceState(defaultColor) {
        WallpaperTonalColors.getSeedColor()?.let { value = Color(it) }
        val unregister = WallpaperTonalColors.addSeedColorListener { value = Color(it) }
        awaitDispose(unregister)
    }
    return seedColor
}
