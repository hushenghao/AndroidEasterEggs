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

        private val LIST = arrayOf(
            com.android_i.egg.Nyandroid::class.java,
            com.android_j.egg.BeanBag::class.java,
            com.android_k.egg.DessertCase::class.java,
            com.android_l.egg.LLandActivity::class.java,
            com.android_m.egg.MLandActivity::class.java,
            com.android_o.egg.octo.Ocquarium::class.java,
            com.android_p.egg.paint.PaintActivity::class.java,
            com.android_q.egg.quares.QuaresActivity::class.java,
        )

        private val instance = AppTaskManager()

        fun getInstance(): AppTaskManager {
            return instance
        }
    }

    override fun onCreate(activity: Activity) {
        tryTrimTaskCount(activity)
    }

    override fun onDestroyed(activity: Activity) {
        if (LIST.contains(activity.javaClass)) {
            moveMainToFront(activity)
        }
    }

    private fun tryTrimTaskCount(context: Context) {
        val activityManager = context.getSystemService<ActivityManager>() ?: return
        val maxCount = MAX_APP_TASK_COUNT
        val appTasks = activityManager.appTasks.filter { !it.isTask(EasterEggsActivity::class.java) }
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

    private fun moveMainToFront(activity: Activity) {
        val task = findActivityTask(activity, EasterEggsActivity::class.java)
        if (task != null) {
            task.moveToFront()
            return
        }

        // relaunch
        val intent = activity.packageManager
            .getLaunchIntentForPackage(activity.packageName) ?: return
        activity.startActivity(intent)
    }

    private fun ActivityManager.AppTask.isTask(clazz: Class<out Activity>): Boolean {
        return this.taskInfo.baseIntent.component?.className == clazz.name
    }
}