@file:JvmName("Utils")
@file:JvmMultifileClass

package com.dede.basic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object Utils {

    fun getLaunchIntent(context: Context): Intent? {
        val pm = context.packageManager
        val packageName = context.packageName
        // https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/app/ApplicationPackageManager.java?q=symbol%3A%5Cbandroid.app.ApplicationPackageManager.getLaunchIntentForPackage%5Cb%20case%3Ayes
        return pm.getLaunchIntentForPackage(packageName)?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            } else {
                setFlags(0)
            }
        }
    }

    val Activity.isPlatLogoActivity: Boolean
        get() = javaClass.simpleName == "PlatLogoActivity"

    fun Activity.platLogoEdge2Edge() {
        if (this is ComponentActivity) {
            this.enableEdgeToEdge()
            return
        }

        @Suppress("DEPRECATION")
        with(window) {
            addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

            navigationBarColor = Color.TRANSPARENT
            statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isNavigationBarContrastEnforced = false
                isStatusBarContrastEnforced = false
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                attributes = attributes.apply {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }

            WindowCompat.setDecorFitsSystemWindows(this, false)
            val windowInsetsController = WindowCompat.getInsetsController(this, this.decorView)
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
