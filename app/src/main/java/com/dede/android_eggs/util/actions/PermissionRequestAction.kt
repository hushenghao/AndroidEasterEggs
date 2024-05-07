package com.dede.android_eggs.util.actions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.dede.android_eggs.util.ActivityActionDispatcher


class PermissionRequestAction : ActivityActionDispatcher.ActivityAction {

    companion object {

        private val EMPTY = emptyArray<String>()

        private val NOTIFICATION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arrayOf(Manifest.permission.POST_NOTIFICATIONS) else EMPTY

        private val mapping = mapOf(
            com.android_t.egg.ComponentActivationActivity::class to NOTIFICATION,
            com.android_s.egg.ComponentActivationActivity::class to NOTIFICATION,
            com.android_r.egg.neko.NekoActivationActivity::class to NOTIFICATION,
            com.android_n.egg.neko.NekoActivationActivity::class to NOTIFICATION,
        )
    }

    override fun onCreate(activity: Activity) {
        val permissions = mapping[activity.javaClass.kotlin]
        if (!permissions.isNullOrEmpty() && !checkPermissions(activity, *permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, 0)
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