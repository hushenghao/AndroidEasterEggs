package com.dede.android_eggs.activity_actions

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
import android.window.OnBackInvokedDispatcher.PRIORITY_OVERLAY

/**
 * fix wallpaper theme finish animation
 */
object WallpaperPlatLogoUtils {

    fun isShowWallpaper(activity: Activity): Boolean {
        val flags = activity.window.attributes.flags
        return (flags and FLAG_SHOW_WALLPAPER) == FLAG_SHOW_WALLPAPER
    }

    fun setupOnBackPressedViewAnimate(activity: Activity) {
        val decorView = activity.window.decorView
        val setupFlag = decorView.getTag(R.id.tag_wallpaper_platlogo_setup_flag) as? Boolean ?: false
        if (setupFlag) {
            return
        }

        val onBackPressedCallback = {
            finishWithAnimation(activity)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.onBackInvokedDispatcher
                .registerOnBackInvokedCallback(PRIORITY_OVERLAY, onBackPressedCallback)
        } else {
            OnBackPressedCallback.attach(activity, onBackPressedCallback)
        }
        decorView.setTag(R.id.tag_wallpaper_platlogo_setup_flag, true)
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
