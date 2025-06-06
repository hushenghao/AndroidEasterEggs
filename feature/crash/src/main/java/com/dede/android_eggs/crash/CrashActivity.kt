package com.dede.android_eggs.crash

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.dede.android_eggs.crash.GlobalExceptionHandler.Companion.INTENT_DATA_NAME
import com.dede.android_eggs.crash.GlobalExceptionHandler.Companion.getCrashTitle
import com.dede.android_eggs.crash.GlobalExceptionHandler.Companion.getDeviceInfo
import com.dede.android_eggs.crash.GlobalExceptionHandler.Companion.getStackTraceString
import com.dede.android_eggs.util.applyIf
import com.dede.android_eggs.views.theme.EasterEggsTheme

/**
 * App crash report
 */
class CrashActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_CHANNEL = "crash_notification_channel"
    }

    private fun tryPostCrashNotification(context: Context, tr: Throwable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // post notification at next time
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0
            )
        }

        val notificationManager = NotificationManagerCompat.from(context)

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
            .bigText(getDeviceInfo() + tr.getStackTraceString())

        val actionFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val copyIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(CrashNotificationActionsReceiver.ACTION_COPY)
                .setPackage(packageName)
                .putExtra(INTENT_DATA_NAME, tr),
            actionFlags
        )
        val newIssueIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(CrashNotificationActionsReceiver.ACTION_NEW_ISSUE)
                .setPackage(packageName)
                .putExtra(INTENT_DATA_NAME, tr),
            actionFlags
        )
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .applyIf(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                @Suppress("NewApi")
                setSmallIcon(
                    IconCompat.createWithResource(context, R.drawable.round_sentiment_dissatisfied)
                )
            }
            .setStyle(bigTextStyle)
            .setContentTitle(tr.getCrashTitle())
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .addAction(-1, getString(android.R.string.copy), copyIntent)
            .addAction(-1, "New issue", newIssueIntent)
            .build()
        notificationManager.notify(1, notification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val tr: Throwable? = GlobalExceptionHandler.getUncaughtException(intent)
        if (tr == null) {
            finish()
            return
        }

        tryPostCrashNotification(this, tr)

        setContent {
            EasterEggsTheme {
                Surface {
                    CrashScreen(tr)
                }
            }
        }
    }
}
