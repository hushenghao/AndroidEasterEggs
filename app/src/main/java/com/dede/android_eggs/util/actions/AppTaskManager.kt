package com.dede.android_eggs.util.actions

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import androidx.core.content.getSystemService
import com.dede.android_eggs.main.EasterEggsActivity
import com.dede.android_eggs.util.ActivityActionDispatcher


class AppTaskManager : ActivityActionDispatcher.ActivityAction {

    companion object {

        private const val MAX_APP_TASK_COUNT = 5

        private val instance = AppTaskManager()

        fun getInstance(): AppTaskManager {
            return instance
        }
    }

    override fun onCreate(activity: Activity) {
        tryTrimTaskCount(activity)
    }

    private fun tryTrimTaskCount(context: Context) {
        val activityManager = context.getSystemService<ActivityManager>() ?: return
        val maxCount = MAX_APP_TASK_COUNT
        val appTasks = activityManager.appTasks
            .filter { !it.isTask(EasterEggsActivity::class.java) }
        if (appTasks.size <= maxCount) {
            return
        }
        val subList = appTasks.subList(maxCount, appTasks.size)
        for (task in subList) {
            task.finishAndRemoveTask()
        }
    }

    fun findActivityTask(context: Context, clazz: Class<out Activity>?): ActivityManager.AppTask? {
        if (clazz == null || !Activity::class.java.isAssignableFrom(clazz)) return null
        val activityManager = context.getSystemService<ActivityManager>() ?: return null
        for (task in activityManager.appTasks) {
            if (task.isTask(clazz)) {
                return task
            }
        }
        return null
    }

    private fun ActivityManager.AppTask.isTask(clazz: Class<out Activity>): Boolean {
        return this.taskInfo.baseIntent.component?.className == clazz.name
    }
}