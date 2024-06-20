package com.dede.android_eggs.views.crash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.IntentCompat
import java.io.Serializable
import kotlin.system.exitProcess

class GlobalExceptionHandler<T : Activity> private constructor(
    private val applicationContext: Context,
    private val activityToBeLaunched: Class<T>,
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        runCatching {
            Log.e(this.toString(), e.stackTraceToString())
            applicationContext.launchCrashActivity(activityToBeLaunched, e)
            exitProcess(0)
        }.getOrElse {
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun <T : Activity> Context.launchCrashActivity(activity: Class<T>, e: Throwable) {
        val crashedIntent = Intent(applicationContext, activity)
            .putExtra(INTENT_DATA_NAME, e as Serializable)
            .addFlags(DEF_INTENT_FLAGS)
        applicationContext.startActivity(crashedIntent)
    }

    companion object {

        private const val INTENT_DATA_NAME = "extra_throwable"
        private const val DEF_INTENT_FLAGS = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK

        fun getUncaughtException(intent: Intent): Throwable? {
            return IntentCompat.getSerializableExtra(
                intent, INTENT_DATA_NAME, Throwable::class.java
            )
        }

        fun <T : Activity> initialize(
            applicationContext: Context,
            activityToBeLaunched: Class<T>,
        ) = Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(
                applicationContext, activityToBeLaunched,
                Thread.getDefaultUncaughtExceptionHandler(),
            )
        )
    }
}
