package com.dede.android_eggs.util.actions

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.activity.EdgeToEdgeCompat
import com.dede.android_eggs.activity_actions.DisableForceDarkUtils
import com.dede.android_eggs.activity_actions.WallpaperPlatLogoUtils
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp

internal class EggActivityAction : ActivityActionDispatcher.ActivityAction {

    private val edgeToEdgePagers: Set<Class<out Activity>> = setOf(
        com.android_o.egg.octo.Ocquarium::class.java,
        com.android_m.egg.MLandActivity::class.java,
        com.android_l.egg.LLandActivity::class.java,
        com.android_k.egg.DessertCase::class.java,
        com.android_j.egg.BeanBag::class.java,
        com.android_i.egg.Nyandroid::class.java,
    )

    /** Matches `PlatLogoActivity` as a class-name segment, covering nested classes too. */
    private val regexPlatLogoActivity = Regex("""(^|[.$])PlatLogoActivity($|[.$])""")

    private val Activity.isPlatLogoActivity: Boolean
        get() = regexPlatLogoActivity.containsMatchIn(javaClass.name)

    override fun onPreCreate(activity: Activity) {
        if (activity.isPlatLogoActivity || edgeToEdgePagers.contains(activity.javaClass)) {
            if (WallpaperPlatLogoUtils.isShowWallpaperTheme(activity)) {
                // Wallpaper Fullscreen themes do not set windowDrawsSystemBarBackgrounds, and
                // on API 35+ setNavigationBarColor no longer adds the flag implicitly, so the
                // system would paint an opaque black 3-button bar across the wallpaper.
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            EdgeToEdgeCompat.enable(activity)
        }
        // MIUI/HyperOS and other OEMs may force dark mode on apps regardless of theme
        // selection, causing white text in light mode (#884, #898).
        // Runtime theme.applyStyle() with force=true has higher priority than XML
        // declarations, better bypassing system-level dark mode enforcement.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            DisableForceDarkUtils.disableForceDark(activity)
        }
    }

    override fun onCreate(activity: Activity) {
        if (activity.isPlatLogoActivity) {
            EasterEggShortcutsHelp.autoReportShortcutUsed(activity, activity.intent)
        }
    }

    override fun onResume(activity: Activity) {
        if (activity.isPlatLogoActivity && WallpaperPlatLogoUtils.isShowWallpaper(activity)) {
            WallpaperPlatLogoUtils.setupOnBackPressedViewAnimate(activity)
        }
    }

}
