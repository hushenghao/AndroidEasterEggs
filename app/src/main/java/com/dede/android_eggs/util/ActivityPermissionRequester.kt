package com.dede.android_eggs.util

import android.Manifest
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat

class ActivityPermissionRequester : Application.ActivityLifecycleCallbacks {

    private object Permissions {

        private val empty = emptyArray<String>()

        val NOTIFICATION = if (Build.VERSION.SDK_INT >= 33)
            arrayOf(Manifest.permission.POST_NOTIFICATIONS) else empty

    }

    private val mapping = mapOf(
        com.android_t.egg.ComponentActivationActivity::class to Permissions.NOTIFICATION,
        com.android_s.egg.ComponentActivationActivity::class to Permissions.NOTIFICATION,
        com.android_r.egg.neko.NekoActivationActivity::class to Permissions.NOTIFICATION,
        com.android_n.egg.neko.NekoActivationActivity::class to Permissions.NOTIFICATION,
    )

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        val permissions = mapping[activity.javaClass.kotlin]
        if (permissions != null && permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions, 0)
        }
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}