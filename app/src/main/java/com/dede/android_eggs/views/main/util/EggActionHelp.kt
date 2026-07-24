package com.dede.android_eggs.views.main.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.getSystemService
import com.dede.android_eggs.R
import com.dede.android_eggs.util.SplitUtils
import com.dede.android_eggs.views.settings.compose.prefs.RetainInRecentsPrefUtil
import com.dede.basic.provider.EasterEgg
import com.dede.basic.toast
import com.dede.basic.uiHandler
import java.lang.ref.WeakReference
import com.dede.android_eggs.resources.R as StringR


object EggActionHelp {

    private const val PROXY_COUNT = 5

    private val proxyClasses = listOf(
        EggProxyActivity0::class.java,
        EggProxyActivity1::class.java,
        EggProxyActivity2::class.java,
        EggProxyActivity3::class.java,
        EggProxyActivity4::class.java,
    )

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

        if (!retainInRecents) {
            context.startActivity(Intent(context, targetClass))
            return
        }

        val proxyClass = findOrPickProxy(context, targetClass.name)
        context.startActivity(
            Intent(context, proxyClass)
                .putExtra(EggProxyActivity.EXTRA_TARGET_EGG_CLASS, targetClass.name)
                .addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS
                )
        )
    }

    /**
     * Pick a proxy activity for [targetClassName].
     *
     * Queries [ActivityManager.appTasks] directly as the single source of truth:
     *
     * 1. If a proxy task is already running [targetClassName] → reuse it.
     * 2. Otherwise, pick the first unused proxy slot.
     * 3. If all slots are occupied, recycle the least-recently-used proxy
     *    (last in `appTasks`, which is most-recent-first).
     */
    private fun findOrPickProxy(context: Context, targetClassName: String): Class<out Activity> {
        val activityManager = context.getSystemService<ActivityManager>()
            ?: return proxyClasses[0]

        val appTasks = activityManager.appTasks
        val usedIndices = mutableListOf<Int>()

        for (task in appTasks) {
            val info = task.taskInfo ?: continue
            val className = info.baseIntent.component?.className ?: continue
            val index = proxyClasses.indexOfFirst { it.name == className }
            if (index < 0) continue

            usedIndices.add(index)

            val runningEgg = info.baseIntent.getStringExtra(EggProxyActivity.EXTRA_TARGET_EGG_CLASS)
            if (targetClassName == runningEgg) {
                return proxyClasses[index]
            }
        }

        // No matching proxy — prefer an unused slot.
        val usedSet = usedIndices.toSet()
        for (i in 0 until PROXY_COUNT) {
            if (i !in usedSet) {
                return proxyClasses[i]
            }
        }

        // All proxies are in use — recycle the least recently used one.
        // appTasks returns most-recent-first, so the last entry is the oldest.
        return proxyClasses[usedIndices.lastOrNull() ?: 0]
    }

    fun cleanupProxyTasks(context: Context) {
        val activityManager = context.getSystemService<ActivityManager>() ?: return
        val proxyClassNames = proxyClasses.map { it.name }.toHashSet()
        for (task in activityManager.appTasks) {
            val className = task.taskInfo?.baseIntent?.component?.className ?: continue
            if (className in proxyClassNames) {
                task.finishAndRemoveTask()
            }
        }
    }

}
