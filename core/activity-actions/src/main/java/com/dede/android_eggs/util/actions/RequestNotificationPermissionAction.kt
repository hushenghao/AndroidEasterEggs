package com.dede.android_eggs.util.actions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.dede.android_eggs.util.ActivityActionDispatcher

internal class RequestNotificationPermissionAction : ActivityActionDispatcher.ActivityAction {

    private val pagers = arrayOf(
        com.android_baklava.egg.landroid.MainActivity::class,// auto pilot
        com.android_t.egg.ComponentActivationActivity::class,
        com.android_s.egg.ComponentActivationActivity::class,
        com.android_r.egg.neko.NekoActivationActivity::class,
        com.android_n.egg.neko.NekoActivationActivity::class,
    )

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    override fun isEnabled(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(activity: Activity) {
        if (pagers.contains(activity.javaClass.kotlin)) {
            val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            if (!checkPermissions(activity, *permissions)) {
                ActivityCompat.requestPermissions(activity, permissions, 0)
            }
        }
    }

    private fun checkPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (
                ActivityCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}