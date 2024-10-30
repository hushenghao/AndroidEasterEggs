package com.dede.android_eggs.crash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.IntentCompat
import java.io.Serializable
import kotlin.system.exitProcess

class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val activityToBeLaunched: Class<out Activity>,
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

    private fun Context.launchCrashActivity(activity: Class<out Activity>, e: Throwable) {
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

        fun testCrash() {
            throw IllegalStateException("Test Crash!")
        }

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
