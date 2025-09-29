package com.dede.android_eggs.views.main.util

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.AppTask
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.content.getSystemService
import com.dede.android_eggs.R
import com.dede.android_eggs.util.SplitUtils
import com.dede.android_eggs.util.applyIf
import com.dede.android_eggs.views.settings.compose.prefs.RetainInRecentsPrefUtil
import com.dede.basic.Utils
import com.dede.basic.provider.EasterEgg
import com.dede.basic.toast
import com.dede.basic.uiHandler
import java.lang.ref.WeakReference
import com.dede.android_eggs.resources.R as StringR


object EggActionHelp {

    private const val MAX_APP_TASK_COUNT = 5

    /**
     * android:documentLaunchMode="intoExisting" and Retain in recents.
     * * [Retain finished tasks](https://developer.android.google.cn/guide/components/activities/recents#retain-finished)
     * * [documentLaunchMode intoExisting](https://developer.android.google.cn/guide/components/activities/recents#attr-doclaunch)
     *
     * @see [Intent.FLAG_ACTIVITY_NEW_DOCUMENT](https://developer.android.google.cn/reference/android/content/Intent#FLAG_ACTIVITY_NEW_DOCUMENT)
     * @see [Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS](https://developer.android.google.cn/reference/android/content/Intent#FLAG_ACTIVITY_RETAIN_IN_RECENTS)
     */
    private const val RETAIN_TASK_AND_LAUNCH_MODE_INTO_EXISTING =
        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS

    fun createIntent(
        context: Context,
        targetClass: Class<out Activity>,
        retainInRecents: Boolean = true,
    ): Intent {
        return Intent(Intent.ACTION_VIEW)
            .setClass(context, targetClass)
            .applyIf(retainInRecents) {
                addFlags(RETAIN_TASK_AND_LAUNCH_MODE_INTO_EXISTING)
            }
    }

    private object NoEggAction : Function1<Context, Unit>, Runnable {
        private const val RESET_DELAY = 3000L

        private var showDetail = false
        private var toastRef: WeakReference<Toast>? = null

        override fun invoke(context: Context) {
            if (!showDetail) {
                toastRef = WeakReference(context.toast(R.string.toast_no_egg))
                showDetail = true
                return
            }
            // show detail toast, for rookies
            toastRef?.get()?.cancel()
            context.toast(StringR.string.toast_no_egg_detail)

            uiHandler.removeCallbacks(this)
            uiHandler.postDelayed(this, RESET_DELAY)
        }

        override fun run() {
            showDetail = false
        }
    }

    fun launchEgg(context: Context, egg: EasterEgg) {
        val targetClass = egg.actionClass
        if (targetClass == null) {
            if (!egg.onEasterEggAction(context)) {
                NoEggAction.invoke(context)
            }
            return
        }

        val retainInRecents = !SplitUtils.isActivityEmbedded(context) &&
                RetainInRecentsPrefUtil.isRetainInRecentsEnabled(context)
        val intent = createIntent(context, targetClass, retainInRecents)
        val task: AppTask? = findTaskWithTrim(context, targetClass)
        if (task != null) {
            if (!retainInRecents) {
                // finish retained task
                task.finishAndRemoveTask()
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                // android M Recent task list:
                // Not following `android:documentLaunchMode="intoExisting"` behavior.
                // ???
                // remove it
                task.finishAndRemoveTask()
            }
        }
        context.startActivity(intent)
    }

    private fun AppTask.isThisTask(component: ComponentName): Boolean {
        return taskInfo.baseIntent.component?.className == component.className
    }

    private fun AppTask.isThisTask(target: Class<out Activity>): Boolean {
        return taskInfo.baseIntent.component?.className == target.name
    }

    private fun findTaskWithTrim(context: Context, target: Class<out Activity>): AppTask? {
        val mainComponent = Utils.getLaunchIntent(context)?.component ?: return null

        val activityManager = context.getSystemService<ActivityManager>() ?: return null
        var targetTask: AppTask? = null
        val appTasks = ArrayList<AppTask>()
        for (task in activityManager.appTasks) {
            if (task.isThisTask(mainComponent)) {
                // exclude main task
                continue
            }
            if (task.isThisTask(target)) {
                // exclude about to task
                targetTask = task
                continue
            }
            appTasks.add(task)
        }
        // trim app task
        val count = MAX_APP_TASK_COUNT - 1 // minus about to task
        if (appTasks.size > count) {
            val subList = appTasks.subList(count, appTasks.size)
            for (task in subList) {
                task.finishAndRemoveTask()
            }
        }
        return targetTask
    }

}
