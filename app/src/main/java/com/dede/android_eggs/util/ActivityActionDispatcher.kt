package com.dede.android_eggs.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.dede.android_eggs.util.actions.PermissionRequestAction
import com.dede.android_eggs.util.actions.AppTaskManager
import com.dede.android_eggs.util.actions.WarningDialogAction

class ActivityActionDispatcher : Application.ActivityLifecycleCallbacks {

    interface ActivityAction {

        fun onCreate(activity: Activity) {}

        fun onStart(activity: Activity) {}

        fun onResume(activity: Activity) {}

        fun onPause(activity: Activity) {}
        fun onDestroyed(activity: Activity) {}
    }

    companion object {
        fun register(application: Application) {
            application.registerActivityLifecycleCallbacks(ActivityActionDispatcher())
        }
    }

    private val actions = arrayListOf(
        PermissionRequestAction(),
        WarningDialogAction(),
        AppTaskManager(),
    )

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
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        for (action in actions) {
            action.onDestroyed(activity)
        }
    }
}