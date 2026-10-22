package com.dede.android_eggs.crash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CrashNotificationActionsReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_COPY = "com.dede.android_eggs.crash.action.COPY"

        fun copyActionIntent(context: Context, title: String, details: String): Intent {
            return Intent(ACTION_COPY)
                .setPackage(context.packageName)
                .putExtra(Utilities.EXTRA_CRASH_TITLE, title)
                .putExtra(Utilities.EXTRA_CRASH_DETAILS, details)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val title = intent.getStringExtra(Utilities.EXTRA_CRASH_TITLE) ?: return
        val details = intent.getStringExtra(Utilities.EXTRA_CRASH_DETAILS) ?: return
        when (action) {
            ACTION_COPY -> {
                Utilities.copyCrashReport(context, title, details)
            }
        }
    }
}
