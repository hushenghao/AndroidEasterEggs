package com.dede.basic

import android.os.Handler
import android.os.Looper
import androidx.core.os.ExecutorCompat
import androidx.core.os.HandlerCompat


val uiHandler = Handler(Looper.getMainLooper())

fun delay(delayMillis: Long, token: Any? = null, r: Runnable) {
    HandlerCompat.postDelayed(uiHandler, r, token, delayMillis)
}

fun cancel(r: Runnable) {
    uiHandler.removeCallbacks(r)
}

fun cancel(token: Any) {
    uiHandler.removeCallbacksAndMessages(token)
}

val uiExecutor = ExecutorCompat.create(uiHandler)