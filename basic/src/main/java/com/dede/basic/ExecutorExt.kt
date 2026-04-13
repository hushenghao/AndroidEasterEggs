@file:JvmName("ExecutorUtils")

package com.dede.basic

import android.os.Handler
import android.os.Looper
import androidx.core.os.ExecutorCompat
import androidx.core.os.HandlerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


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

val uiExecutor: Executor = ExecutorCompat.create(uiHandler)

//val singleExecutor: Executor = Executors.newSingleThreadExecutor()

val cachedExecutor: ExecutorService = Executors.newCachedThreadPool()

fun Executor.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    execute {
        runBlocking(context) {
            block()
        }
    }
}
