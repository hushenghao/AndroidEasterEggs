package com.dede.android_eggs.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LocalEvent.Receiver(action: String, eventCallback: EventCallback) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, action) {
        receiver(lifecycleOwner).register(action, eventCallback)
    }
}
