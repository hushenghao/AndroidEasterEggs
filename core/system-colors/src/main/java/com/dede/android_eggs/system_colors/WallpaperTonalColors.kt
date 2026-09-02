package com.dede.android_eggs.system_colors

import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.startup.Initializer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

/**
 * Wallpaper seeded tonal palette service, initialized once during app startup on
 * Android 8.1-11 (API 27-30), where the framework dynamic colors are missing.
 *
 * It extracts the system wallpaper colors and feeds [SystemTonalColors], so both
 * the theme and the egg system colors ([Context.getSystemColor]) can resolve the
 * dynamic system colors without any UI interaction, mirroring the framework
 * dynamic colors that are always available on Android 12+.
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isWallpaperColorSupported(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

/**
 * Wallpaper seeded tonal palette service, initialized once during app startup on
 * Android 8.1-11 (API 27-30), where the framework dynamic colors are missing.
 *
 * It extracts the system wallpaper colors and feeds [SystemTonalColors], so both
 * the theme and the egg system colors ([Context.getSystemColor]) can resolve the
 * dynamic system colors without any UI interaction, mirroring the framework
 * dynamic colors that are always available on Android 12+.
 */
object WallpaperTonalColors {

    private const val TAG = "WallpaperTonalColors"

    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private val listeners = CopyOnWriteArrayList<(Int) -> Unit>()

    @Volatile
    private var seedColor: Int = 0

    /** Called once during app startup by [Initializer], the colors resolve in background. */
    fun initialize(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1
            || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ) {
            // API 23-26 has no WallpaperColors API, API 31+ uses the framework colors.
            return
        }
        val appContext = context.applicationContext
        executor.execute { fetchAndPush(appContext) }
        registerSystemColorsListener(appContext) { which ->
            if (which and WallpaperManager.FLAG_SYSTEM != 0) {
                executor.execute { fetchAndPush(appContext) }
            }
        }
    }

    /** The wallpaper seed color pushed at startup, null when not ready yet. */
    fun getSeedColor(): Int? = seedColor.takeIf { it != 0 }

    /** Listen for the wallpaper seed color updates, callbacks on the main thread. */
    fun addSeedColorListener(listener: (Int) -> Unit): () -> Unit {
        listeners += listener
        return { listeners.remove(listener) }
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    private fun fetchAndPush(context: Context) {
        // IPC call, slow on the first query, must not be called from the UI thread
        val colors = WallpaperManager.getInstance(context)
            .getWallpaperColors(WallpaperManager.FLAG_SYSTEM) ?: return
        val color = colors.primaryColor.toArgb()
        SystemTonalColors.updateSourceColor(color)
        seedColor = color
        Log.i(TAG, "Seed color: #$color")
        mainHandler.post {
            for (listener in listeners) {
                listener(color)
            }
        }
    }

    /** Process scoped listener, lives as long as the app process. */
    @RequiresApi(Build.VERSION_CODES.O_MR1)
    private fun registerSystemColorsListener(
        context: Context,
        onColorsChanged: (Int) -> Unit,
    ) {
        val manager = WallpaperManager.getInstance(context)
        val listener = WallpaperManager.OnColorsChangedListener { _, which ->
            onColorsChanged(which)
        }
        manager.addOnColorsChangedListener(listener, mainHandler)
    }

    /**
     * androidx.startup initializer, registered in this module's AndroidManifest.
     */
    class Initializer : androidx.startup.Initializer<Unit> {
        override fun create(context: Context) {
            initialize(context)
        }

        override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> = emptyList()
    }
}
