package com.dede.android_eggs.crash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dede.android_eggs.crash.Utilities.getStackTraceString
import java.io.Serializable
import kotlin.system.exitProcess
import androidx.startup.Initializer as AndroidxInitializer

private class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val activityToBeLaunched: Class<out Activity>,
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        Utilities.tryPostCrashNotification(applicationContext, e)

        runCatching {
            val stackTraceMsg = e.getStackTraceString()
            Log.e(this.toString(), stackTraceMsg)

            applicationContext.launchCrashActivity(activityToBeLaunched, e)
            exitProcess(-1)
        }.getOrElse {
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun Context.launchCrashActivity(activity: Class<out Activity>, e: Throwable) {
        val crashedIntent = Intent(applicationContext, activity)
            .putExtra(Utilities.INTENT_DATA_NAME, e as Serializable)
            .addFlags(DEF_INTENT_FLAGS)
        applicationContext.startActivity(crashedIntent)
    }

    class Initializer : AndroidxInitializer<Unit> {

        override fun create(context: Context) {
            initialize(context)
        }

        override fun dependencies(): List<Class<out AndroidxInitializer<*>>> = emptyList()
    }

    companion object {

        private const val DEF_INTENT_FLAGS = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK

        fun initialize(
            applicationContext: Context,
            activityToBeLaunched: Class<out Activity> = CrashActivity::class.java,
        ) = Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(
                applicationContext, activityToBeLaunched,
                Thread.getDefaultUncaughtExceptionHandler(),
            )
        )
    }
}
