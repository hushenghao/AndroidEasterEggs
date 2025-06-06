package com.dede.android_eggs.crash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dede.android_eggs.crash.GlobalExceptionHandler.Companion.copyThrowablePlantText
import com.dede.android_eggs.crash.GlobalExceptionHandler.Companion.getUncaughtException
import com.dede.android_eggs.crash.GlobalExceptionHandler.Companion.openNewIssue

class CrashNotificationActionsReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_COPY = "com.dede.android_eggs.crash.action.COPY"

        const val ACTION_NEW_ISSUE = "com.dede.android_eggs.crash.action.NEW_ISSUE"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val tr = getUncaughtException(intent) ?: return
        when (intent?.action) {
            ACTION_COPY -> {
                copyThrowablePlantText(context, tr)
            }
            ACTION_NEW_ISSUE -> {
                openNewIssue(context, tr)
                copyThrowablePlantText(context, tr)
            }
        }
    }
}
