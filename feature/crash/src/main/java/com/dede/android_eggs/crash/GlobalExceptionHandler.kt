package com.dede.android_eggs.crash

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import com.dede.android_eggs.util.AGPUtils
import com.dede.basic.Utils
import com.dede.basic.copy
import java.io.Serializable
import kotlin.system.exitProcess

internal class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val activityToBeLaunched: Class<out Activity>,
    private val defaultHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
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
            .putExtra(INTENT_DATA_NAME, e as Serializable)
            .addFlags(DEF_INTENT_FLAGS)
        applicationContext.startActivity(crashedIntent)
    }

    class Initializer : androidx.startup.Initializer<Unit> {

        override fun create(context: Context) {
            initialize(context)
        }

        override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> = emptyList()
    }

    companion object {

        internal const val INTENT_DATA_NAME = "extra_throwable"
        private const val DEF_INTENT_FLAGS = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK


        internal fun Throwable.getCrashTitle(): String {
            return "[Bug] App Crash: %s".format(this.toString())
        }

        internal fun Context.getDeviceInfo(): String {
            val (versionName, versionCode) = Utils.getAppVersionPair(this)
            return "Device: %s (%s - %s), SDK: %s (%d), App: %s (%d), VcsRevision: %s\n".format(
                Build.MODEL, Build.BRAND, Build.DEVICE,
                Build.VERSION.RELEASE, Build.VERSION.SDK_INT,
                versionName, versionCode,
                AGPUtils.getVcsRevision(7)
            )
        }

        internal fun Throwable.getStackTraceString(): String {
            return try {
                Log.getStackTraceString(this)
            } catch (ignore: Throwable) {
                this.toString()
            }
        }

        internal fun copyThrowablePlantText(context: Context, tr: Throwable) {
            val text = "%s\n\n%s\n%s".format(
                tr.getCrashTitle(),
                context.getDeviceInfo(),
                tr.getStackTraceString()
            )
            context.copy(text)
        }

        internal fun openNewIssue(context: Context, tr: Throwable) {
            val title = tr.getCrashTitle()
            val body = context.getDeviceInfo() + "\n" + tr.getStackTraceString()
            val uri = context.getString(R.string.url_github_issues).toUri()
                .buildUpon()
                .appendPath("new")
                .appendQueryParameter("title", title)
                .appendQueryParameter("body", "```\n%s\n```".format(body))
                .build()
            val intent = Intent(Intent.ACTION_VIEW, uri)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
            }
        }

        fun getUncaughtException(intent: Intent?): Throwable? {
            return IntentCompat.getSerializableExtra(
                intent ?: return null, INTENT_DATA_NAME, Throwable::class.java
            )
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
