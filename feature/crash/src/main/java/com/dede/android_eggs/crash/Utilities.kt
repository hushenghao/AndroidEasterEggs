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
import androidx.core.content.IntentCompat
import androidx.core.content.edit
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.dede.android_eggs.util.AGPUtils
import com.dede.android_eggs.util.pref
import com.dede.basic.Utils
import com.dede.basic.copy

internal object Utilities {

    private const val KEY_SAVE_VCS_REVISION = "pref_save_vcs_revision"
    private const val KEY_LAST_VCS_REVISION = "pref_last_vcs_revision"

    private const val VCS_REVISION_LENGTH = 7

    fun saveVcsRevision(context: Context) {
        val vcsRevision = AGPUtils.getVcsRevision(VCS_REVISION_LENGTH)
        with(context.pref) {
            val savedVersion = getString(KEY_SAVE_VCS_REVISION, null)
            if (savedVersion == vcsRevision) {
                return
            }
            edit {
                putString(KEY_SAVE_VCS_REVISION, vcsRevision)
                putString(KEY_LAST_VCS_REVISION, savedVersion)
            }
        }
    }

    const val EXTRA_THROWABLE = "extra_throwable"
    const val EXTRA_SCREENSHOT_PATH = "extra_screenshot"

    private const val NOTIFICATION_ID = 1
    private const val NOTIFICATION_CHANNEL = "crash_notification_channel"

    fun hasPostNotificationPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun tryPostCrashNotification(context: Context, tr: Throwable?) {
        if (tr == null || !hasPostNotificationPermission(context)) {
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
            .bigText(context.getDeviceInfo() + tr.getStackTraceString())

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val copyIntent = PendingIntent.getBroadcast(
            context, 0, CrashNotificationActionsReceiver.copyActionIntent(context, tr), flags
        )
        val newIssueIntent = PendingIntent.getActivity(
            context, 0, createNewIssueIntent(context, tr), flags
        )
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(
                IconCompat.createWithResource(context, R.drawable.round_sentiment_dissatisfied)
            )
            .setStyle(bigTextStyle)
            .setContentTitle(tr.getCrashTitle())
            .setContentText(tr.getStackTraceString())
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

    fun Context.getDeviceInfo(): String {
        val (versionName, versionCode) = Utils.getAppVersionPair(this)
        return "Device: %s (%s - %s), SDK: %s (%d), App: %s (%d), VcsRevision: %s, Last VcsRevision: %s,\n".format(
            Build.MODEL, Build.BRAND, Build.DEVICE,
            Build.VERSION.RELEASE, Build.VERSION.SDK_INT,
            versionName, versionCode,
            AGPUtils.getVcsRevision(VCS_REVISION_LENGTH),
            pref.getString(KEY_LAST_VCS_REVISION, null),
        )
    }

    fun Throwable.getStackTraceString(): String {
        return try {
            Log.getStackTraceString(this)
        } catch (ignore: Throwable) {
            this.toString()
        }
    }

    fun copyThrowablePlantText(context: Context, tr: Throwable) {
        val text = "%s\n\n%s\n%s".format(
            tr.getCrashTitle(),
            context.getDeviceInfo(),
            tr.getStackTraceString()
        )
        context.copy(text)
    }

    fun createNewIssueIntent(context: Context, tr: Throwable): Intent {
        val title = tr.getCrashTitle()
        val body = context.getDeviceInfo() + "\n" + tr.getStackTraceString()
        val uri = context.getString(R.string.url_github_issues).toUri()
            .buildUpon()
            .appendPath("new")
            .appendQueryParameter("title", title)
            .appendQueryParameter("body", "```\n%s\n```".format(body))
            .build()
        return Intent(Intent.ACTION_VIEW, uri)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun getUncaughtException(intent: Intent?): Throwable? {
        return IntentCompat.getSerializableExtra(
            intent ?: return null, EXTRA_THROWABLE, Throwable::class.java
        )
    }
}
