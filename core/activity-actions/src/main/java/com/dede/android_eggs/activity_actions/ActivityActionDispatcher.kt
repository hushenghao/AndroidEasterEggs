@file:Suppress("PackageDirectoryMismatch")

package com.dede.android_eggs.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.startup.Initializer
import com.dede.android_eggs.util.actions.PermissionRequestAction
import com.dede.android_eggs.util.actions.PlatLogoActivityAction
import com.dede.android_eggs.util.actions.WarningDialogAction

internal class ActivityActionDispatcher : Application.ActivityLifecycleCallbacks,
    Initializer<Unit> {

    override fun create(context: Context) {
        (context as Application).registerActivityLifecycleCallbacks(ActivityActionDispatcher())
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    interface ActivityAction {

        fun onPreCreate(activity: Activity) {}

        fun onCreate(activity: Activity) {}

        fun onStart(activity: Activity) {}

        fun onResume(activity: Activity) {}

        fun onPause(activity: Activity) {}

        fun onStop(activity: Activity) {}

        fun onDestroyed(activity: Activity) {}
    }

    private val actions: List<ActivityAction> = arrayListOf(
        PlatLogoActivityAction(),
        WarningDialogAction(),
        PermissionRequestAction(),
    )

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        for (action in actions) {
            action.onPreCreate(activity)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        for (action in actions) {
            action.onCreate(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        for (action in actions) {
            action.onStart(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        for (action in actions) {
            action.onResume(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        for (action in actions) {
            action.onPause(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        for (action in actions) {
            action.onStop(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        for (action in actions) {
            action.onDestroyed(activity)
        }
    }
}