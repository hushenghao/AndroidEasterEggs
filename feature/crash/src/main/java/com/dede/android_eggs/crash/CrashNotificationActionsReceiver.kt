package com.dede.android_eggs.crash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CrashNotificationActionsReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_COPY = "com.dede.android_eggs.crash.action.COPY"

        fun copyActionIntent(context: Context, tr: Throwable): Intent {
            return Intent(ACTION_COPY)
                .setPackage(context.packageName)
                .putExtra(Utilities.INTENT_DATA_NAME, tr)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val tr = Utilities.getUncaughtException(intent) ?: return
        when (action) {
            ACTION_COPY -> {
                Utilities.copyThrowablePlantText(context, tr)
            }
        }
    }
}
