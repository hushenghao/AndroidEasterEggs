package com.dede.android_eggs.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager

typealias EventCallback = (intent: Intent) -> Unit

object LocalEvent {

    fun poster(context: Context): Poster {
        return Poster(context)
    }

    fun receiver(owner: LifecycleOwner): Receiver {
        return Receiver(owner)
    }

    @Composable
    fun receiver(): ComposeReceiver {
        val context = LocalContext.current
        return remember { ComposeReceiver(context) }
    }

    class ComposeReceiver(private val context: Context) {

        private class DisposedReceiver(val callback: EventCallback) : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                callback.invoke(intent)
            }
        }

        @Composable
        fun register(action: String, eventCallback: EventCallback) {
            DisposableEffect(key1 = action) {
                val receiver = DisposedReceiver(eventCallback)
                context.localBroadcastManager.registerReceiver(receiver, IntentFilter(action))
                onDispose {
                    context.localBroadcastManager.unregisterReceiver(receiver)
                }
            }
        }
    }

    class Receiver(private val owner: LifecycleOwner) {

        private class LifecycleReceiver(val callback: EventCallback) :
            BroadcastReceiver(), DefaultLifecycleObserver {
            override fun onReceive(context: Context?, intent: Intent) {
                callback.invoke(intent)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                owner.context.localBroadcastManager.unregisterReceiver(this)
            }
        }

        fun register(action: String, eventCallback: EventCallback) {
            val receiver = LifecycleReceiver(eventCallback)
            owner.lifecycle.addObserver(receiver)
            owner.context.localBroadcastManager.registerReceiver(receiver, IntentFilter(action))
        }
    }

    class Poster(private val context: Context) {
        fun post(action: String, extras: Bundle? = null) {
            val intent = Intent(action)
            if (extras != null) {
                intent.putExtras(extras)
            }
            context.localBroadcastManager.sendBroadcast(intent)
        }
    }

}

private val LifecycleOwner.context: Context
    get() = when (this) {
        is ComponentActivity -> this
        is Fragment -> requireContext()
        else -> throw IllegalArgumentException(this.toString())
    }

private val Context.localBroadcastManager: LocalBroadcastManager
    get() = LocalBroadcastManager.getInstance(this)