@file:JvmName("Utils")
@file:JvmMultifileClass

package com.dede.basic

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.provider.Settings

object Utils {

    fun getDevOptsAnimatorDurationScaleIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        intent.putExtra(":settings:fragment_args_key", "animator_duration_scale")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    fun areAnimatorEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ValueAnimator.areAnimatorsEnabled()
        } else {
            getAnimatorDurationScale(context) != 0f
        }
    }

    fun getAnimatorDurationScale(context: Context): Float {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ValueAnimator.getDurationScale()
        } else {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            )
        }
    }

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

    fun getAppVersionPair(context: Context): Pair<String?, Long> {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (ignore: NameNotFoundException) {
        }
        if (packageInfo != null) {
            val versionName = packageInfo.versionName
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
            return versionName to versionCode
        }
        return null to -1L
    }

    val Activity.isPlatLogoActivity: Boolean
        get() = javaClass.simpleName == "PlatLogoActivity"

}
