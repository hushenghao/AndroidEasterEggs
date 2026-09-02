package com.dede.android_eggs.util

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

typealias EventCallback = (intent: Intent) -> Unit

object LocalEvent {

    private val localEventFlow = MutableSharedFlow<Intent>(
        replay = 0,
        extraBufferCapacity = 3,
    )

    fun poster(): Poster {
        return Poster()
    }

    fun receiver(owner: LifecycleOwner): Receiver {
        return Receiver(owner)
    }

    internal fun flow(action: String): Flow<Intent> {
        return localEventFlow.filter { it.action == action }
    }

    class Receiver internal constructor(private val owner: LifecycleOwner) {
        fun register(action: String, eventCallback: EventCallback): Job {
            return owner.lifecycleScope.launch {
                owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    flow(action).collect(eventCallback)
                }
            }
        }
    }

    class Poster internal constructor() {
        fun post(action: String, extras: Bundle? = null) {
            val intent = Intent(action)
            if (extras != null) {
                intent.putExtras(extras)
            }
            localEventFlow.tryEmit(intent)
        }
    }

}
