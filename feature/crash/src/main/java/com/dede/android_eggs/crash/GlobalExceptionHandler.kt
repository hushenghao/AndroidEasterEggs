package com.dede.android_eggs.crash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import com.dede.android_eggs.crash.Utilities.getCrashTitle
import com.dede.android_eggs.crash.Utilities.getStackTraceString
import com.dede.android_eggs.util.applyNotNull
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess
import androidx.startup.Initializer as AndroidxInitializer

private class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val activityToBeLaunched: Class<out Activity>,
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        if (e.isIgnoredException()) {
            Log.e(TAG, "Ignored system exception on ${t.name}:\n${e.getStackTraceString()}")
            // A dead main looper leaves the process unusable, any other thread just dies
            // and the app keeps running.
            if (t == Looper.getMainLooper().thread) {
                exitProcess(-1)
            }
            return
        }

        val title = e.getCrashTitle()
        val details = Utilities.getCrashDetails(t, e)

        Utilities.tryPostCrashNotification(applicationContext, title, details)

        val screenshotFile = runBlocking {
            CrashScreenshotCapture.tryCaptureScreenshot(applicationContext)
        }

        runCatching {
            val stackTraceMsg = e.getStackTraceString()
            Log.e(this.toString(), stackTraceMsg)

            applicationContext.launchCrashActivity(
                activityToBeLaunched, title, details, screenshotFile
            )
            exitProcess(-1)
        }.getOrElse {
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun Context.launchCrashActivity(
        activity: Class<out Activity>,
        title: String,
        details: String,
        screenshotFile: File?
    ) {
        val crashedIntent = Intent(applicationContext, activity)
            .putExtra(Utilities.EXTRA_CRASH_TITLE, title)
            .putExtra(Utilities.EXTRA_CRASH_DETAILS, details)
            .applyNotNull(screenshotFile) {
                putExtra(Utilities.EXTRA_SCREENSHOT_PATH, it.absolutePath)
            }
            .addFlags(DEF_INTENT_FLAGS)
        applicationContext.startActivity(crashedIntent)
    }

    class Initializer : AndroidxInitializer<Unit> {

        override fun create(context: Context) {
            initialize(context)
            CrashScreenshotCapture.initialize()
        }

        override fun dependencies(): List<Class<out AndroidxInitializer<*>>> = emptyList()
    }

    companion object {

        private const val TAG = "GlobalExceptionHandler"

        private const val DEF_INTENT_FLAGS = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK

        fun initialize(
            applicationContext: Context,
            activityToBeLaunched: Class<out Activity> = CrashActivity::class.java,
        ) {
            Thread.setDefaultUncaughtExceptionHandler(
                GlobalExceptionHandler(
                    applicationContext, activityToBeLaunched,
                    Thread.getDefaultUncaughtExceptionHandler(),
                )
            )
            Utilities.saveVcsRevision(applicationContext)
        }
    }
}
