package com.dede.android_eggs.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
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
        else -> throw IllegalArgumentException()
    }

private val Context.localBroadcastManager: LocalBroadcastManager
    get() = LocalBroadcastManager.getInstance(this)