package com.dede.android_eggs.activity_actions

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
import android.window.OnBackInvokedDispatcher.PRIORITY_OVERLAY
import com.dede.basic.utils.DynamicObjectUtils

/**
 * fix wallpaper theme finish animation
 */
object WallpaperPlatLogoUtils {

    private fun getThemeResId(activity: Activity): Int {
        return DynamicObjectUtils.asDynamicObject(activity, ContextThemeWrapper::class)
            .invokeMethod("getThemeResId")
            .getValue() as? Int ?: -1
    }

    fun isShowWallpaper(activity: Activity): Boolean {
        val flags = activity.window.attributes.flags
        if ((flags and FLAG_SHOW_WALLPAPER) == FLAG_SHOW_WALLPAPER) {
            return true
        }

        // android 9
        val theme = getThemeResId(activity)
        val wallpaperThemes = intArrayOf(
            android.R.style.Theme_Wallpaper,
            android.R.style.Theme_Wallpaper_NoTitleBar,
            android.R.style.Theme_Wallpaper_NoTitleBar_Fullscreen,
            @Suppress("DEPRECATION") android.R.style.Theme_Holo_Wallpaper,
            @Suppress("DEPRECATION") android.R.style.Theme_Holo_Wallpaper_NoTitleBar,
            android.R.style.Theme_DeviceDefault_Wallpaper,
            android.R.style.Theme_DeviceDefault_Wallpaper_NoTitleBar,
        )
        return wallpaperThemes.contains(theme)
    }

    fun setupOnBackPressedViewAnimate(activity: Activity) {
        val onBackPressedCallback = {
            finishWithAnimation(activity)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.onBackInvokedDispatcher
                .registerOnBackInvokedCallback(PRIORITY_OVERLAY, onBackPressedCallback)
        } else {
            OnBackPressedCallback.attach(activity, onBackPressedCallback)
        }
    }

    @SuppressLint("ValidFragment")
    @Suppress("DEPRECATION")
    internal class OnBackPressedCallback : android.app.Fragment() {
        companion object {

            private const val TAG_FRAGMENT = "OnBackPressedCallback"

            fun attach(activity: Activity, onBackPressedCallback: () -> Unit) {
                val fm = activity.fragmentManager
                var fragment = fm.findFragmentByTag(TAG_FRAGMENT)
                if (fragment == null) {
                    fragment = OnBackPressedCallback()
                    fm.beginTransaction()
                        .add(fragment, TAG_FRAGMENT)
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                }
                fm.addOnBackStackChangedListener {
                    if (fm.backStackEntryCount == 0) {
                        onBackPressedCallback()
                    }
                }
            }
        }
    }

    fun finishWithAnimation(activity: Activity) {
        if (isShowWallpaper(activity)) {
            activity.findViewById<View>(android.R.id.content).animate()
                .alpha(0f)
                .setDuration(300L)
                .start()
        }

        activity.finishAfterTransition()
    }
}
