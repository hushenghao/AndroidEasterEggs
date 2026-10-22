package com.dede.android_eggs.crash

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.util.AGPUtils
import com.dede.android_eggs.util.pref
import com.dede.basic.Utils
import com.dede.basic.copy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object Utilities {

    private const val TAG = "CrashUtilities"

    private const val VCS_REVISION_LENGTH = 7
    private const val MAX_ISSUE_TITLE_LENGTH = 256
    private const val MAX_ISSUE_BODY_LENGTH = 6000

    fun saveVcsRevision(context: Context) {
        val vcsRevision = AGPUtils.getVcsRevision(VCS_REVISION_LENGTH)
        val savedVersion = AppSettings.savedVcsRevision.get(context)
        if (savedVersion == vcsRevision) {
            return
        }
        context.pref.edit {
            AppSettings.savedVcsRevision.set(this, vcsRevision)
            AppSettings.lastVcsRevision.set(this, savedVersion)
        }
    }

    const val EXTRA_CRASH_TITLE = "extra_crash_title"
    const val EXTRA_CRASH_DETAILS = "extra_crash_details"
    const val EXTRA_SCREENSHOT_PATH = "extra_screenshot"

    private const val NOTIFICATION_ID = 1
    private const val NOTIFICATION_CHANNEL = "crash_notification_channel"

    fun hasPostNotificationPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun tryPostCrashNotification(context: Context, title: String, details: String) {
        // Never let the notification break the crash flow: PendingIntent registration can
        // fail with a huge details payload (binder transaction limit), while the crash
        // screenshot and the crash screen must still run.
        runCatching {
            postCrashNotification(context, title, details)
        }.onFailure {
            Log.e(TAG, "Failed to post the crash notification", it)
        }
    }

    private fun postCrashNotification(context: Context, title: String, details: String) {
        if (!hasPostNotificationPermission(context)) {
            return
        }

        val notificationManager = NotificationManagerCompat.from(context)
        if (!notificationManager.areNotificationsEnabled()) {
            return
        }

        val notificationChannel = NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName("App crash notification")
            .setShowBadge(true)
            .setLightsEnabled(true)
            .build()
        notificationManager.createNotificationChannel(notificationChannel)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(context.getDeviceInfo() + details)

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val copyIntent = PendingIntent.getBroadcast(
            context, 0, CrashNotificationActionsReceiver.copyActionIntent(context, title, details), flags
        )
        val newIssueIntent = PendingIntent.getActivity(
            context, 0, createNewIssueIntent(context, title, details), flags
        )
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(
                IconCompat.createWithResource(context, R.drawable.round_sentiment_dissatisfied)
            )
            .setStyle(bigTextStyle)
            .setContentTitle(title)
            .setContentText(details)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .addAction(-1, context.getString(android.R.string.copy), copyIntent)
            .addAction(-1, "New issue", newIssueIntent)
            .build()
        @Suppress("MissingPermission")
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun Throwable.getCrashTitle(): String {
        return "[Bug] App Crash: %s".format(this.toString())
    }

    fun getCrashDetails(thread: Thread, tr: Throwable): String {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US).format(Date())
        return "Thread: %s, Time: %s,\n%s".format(
            thread.name, time, tr.getStackTraceString()
        )
    }

    fun Context.getDeviceInfo(): String {
        val (versionName, versionCode) = Utils.getAppVersionPair(this)
        return "Device: %s (%s - %s), SDK: %s (%d), App: %s (%d), VcsRevision: %s, Last VcsRevision: %s,\n".format(
            Build.MODEL, Build.BRAND, Build.DEVICE,
            Build.VERSION.RELEASE, Build.VERSION.SDK_INT,
            versionName, versionCode,
            AGPUtils.getVcsRevision(VCS_REVISION_LENGTH),
            AppSettings.lastVcsRevision.get(this),
        )
    }

    fun Throwable.getStackTraceString(): String {
        return try {
            Log.getStackTraceString(this)
        } catch (_: Throwable) {
            this.toString()
        }
    }

    fun copyCrashReport(context: Context, title: String, details: String) {
        val text = "%s\n\n%s\n%s".format(
            title,
            context.getDeviceInfo(),
            details
        )
        context.copy(text)
    }

    fun createNewIssueIntent(context: Context, title: String, details: String): Intent {
        val body = buildString {
            append(context.getDeviceInfo())
            append('\n')
            append(details)
        }
        // Keep the final URL within common browser limits: deep stacks make the
        // query too long for the browser to open. The copy button carries the
        // full text as the fallback.
        val clippedBody = if (body.length > MAX_ISSUE_BODY_LENGTH) {
            body.substring(0, MAX_ISSUE_BODY_LENGTH) + "\n... [truncated]"
        } else {
            body
        }
        val clippedTitle = if (title.length > MAX_ISSUE_TITLE_LENGTH) {
            title.substring(0, MAX_ISSUE_TITLE_LENGTH) + "..."
        } else {
            title
        }
        val uri = context.getString(R.string.url_github_issues).toUri()
            .buildUpon()
            .appendPath("new")
            .appendQueryParameter("title", clippedTitle)
            .appendQueryParameter("body", "```\n%s\n```".format(clippedBody))
            .build()
        return Intent(Intent.ACTION_VIEW, uri)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
