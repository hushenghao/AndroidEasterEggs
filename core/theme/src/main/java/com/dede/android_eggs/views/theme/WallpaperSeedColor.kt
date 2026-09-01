package com.dede.android_eggs.views.theme

import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val mainHandler = Handler(Looper.getMainLooper())

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isWallpaperColorSupported(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

/**
 * The seed color extracted from the system wallpaper, falls back to [defaultSeedColor]
 * below API 27 or when the system has not computed the wallpaper colors yet,
 * e.g. live wallpapers without [android.service.wallpaper.WallpaperService.Engine.onComputeColors].
 */
@Composable
fun rememberWallpaperSeedColor(): Color {
    if (!isWallpaperColorSupported()) {
        return defaultSeedColor
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val seedColor = remember { mutableStateOf(defaultSeedColor) }

    DisposableEffect(context) {
        fun refresh() {
            scope.launch(Dispatchers.IO) {
                val color = getWallpaperSeedColor(context) ?: return@launch
                withContext(Dispatchers.Main) {
                    seedColor.value = color
                    Log.i("WallpaperSeedColor", "refresh: $color")
                }
            }
        }
        refresh()
        val unregister = registerWallpaperColorsListener(context) { refresh() }
        onDispose { unregister.invoke() }
    }
    return seedColor.value
}

@RequiresApi(Build.VERSION_CODES.O_MR1)
private fun getWallpaperSeedColor(context: Context): Color? {
    // IPC call, slow on the first query, must not be called from the UI thread
    val colors = WallpaperManager.getInstance(context)
        .getWallpaperColors(WallpaperManager.FLAG_SYSTEM) ?: return null
    return Color(colors.primaryColor.toArgb())
}

@RequiresApi(Build.VERSION_CODES.O_MR1)
private fun registerWallpaperColorsListener(
    context: Context,
    onColorsChanged: () -> Unit,
): (() -> Unit) {
    val manager = WallpaperManager.getInstance(context)
    val listener = WallpaperManager.OnColorsChangedListener { _, which ->
        if (which and WallpaperManager.FLAG_SYSTEM != 0) {
            onColorsChanged()
        }
    }
    manager.addOnColorsChangedListener(listener, mainHandler)
    return { manager.removeOnColorsChangedListener(listener) }
}
