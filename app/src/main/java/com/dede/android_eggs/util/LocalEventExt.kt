package com.dede.android_eggs.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun LocalEvent.DisposableReceiver(action: String, eventCallback: EventCallback) {
    val context = LocalContext.current
    val receiver = remember { DisposedReceiver(eventCallback) }
    DisposableEffect(action) {
        context.localBroadcastManager.registerReceiver(receiver, IntentFilter(action))
        onDispose {
            context.localBroadcastManager.unregisterReceiver(receiver)
        }
    }
}

private class DisposedReceiver(val callback: EventCallback) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        callback.invoke(intent)
    }
}
