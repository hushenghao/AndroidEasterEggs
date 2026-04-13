package com.dede.android_eggs.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LocalEvent.Receiver(action: String, eventCallback: EventCallback) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentCallback by rememberUpdatedState(eventCallback)
    DisposableEffect(lifecycleOwner, action) {
        val job = LocalEvent.receiver(lifecycleOwner).register(action) {
            currentCallback(it)
        }
        onDispose {
            job.cancel()
        }
    }
}
