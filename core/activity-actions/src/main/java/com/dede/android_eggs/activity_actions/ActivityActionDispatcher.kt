@file:Suppress("PackageDirectoryMismatch")

package com.dede.android_eggs.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.startup.Initializer
import com.dede.android_eggs.util.actions.EggActivityAction
import com.dede.android_eggs.util.actions.RequestNotificationPermissionAction
import com.dede.android_eggs.util.actions.WarningDialogAction

internal class ActivityActionDispatcher : Application.ActivityLifecycleCallbacks by noOpDelegate(),
    Initializer<Unit> {

    override fun create(context: Context) {
        (context as Application).registerActivityLifecycleCallbacks(ActivityActionDispatcher())
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    interface ActivityAction {

        fun isEnabled(): Boolean = true

        fun onPreCreate(activity: Activity) {}

        fun onCreate(activity: Activity) {}

        fun onStart(activity: Activity) {}

        fun onResume(activity: Activity) {}

    }

    private val actions: Array<ActivityAction> = arrayOf(
        EggActivityAction(),
        WarningDialogAction(),
        RequestNotificationPermissionAction(),
    )

    private fun filterPerformAction(action: ActivityAction.() -> Unit) {
        for (impl in actions) {
            if (!impl.isEnabled()) {
                continue
            }
            action(impl)
        }
    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        filterPerformAction { onPreCreate(activity) }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        filterPerformAction { onCreate(activity) }
    }

    override fun onActivityStarted(activity: Activity) {
        filterPerformAction { onStart(activity) }
    }

    override fun onActivityResumed(activity: Activity) {
        filterPerformAction { onResume(activity) }
    }

}
