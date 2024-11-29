package com.dede.android_eggs.util

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

typealias EventCallback = (intent: Intent) -> Unit

object LocalEvent {

    private val localEventLiveDataMap = HashMap<String, MutableLiveData<Intent?>>()

    private fun getKey(action: String): String {
        return action
    }

    private fun getOrCreateLocalEventLiveData(action: String): MutableLiveData<Intent?> {
        val key = getKey(action)
        val liveData = localEventLiveDataMap.getOrPut(key) {
            MutableLiveData()
        }
        return liveData
    }

    fun poster(): Poster {
        return Poster()
    }

    fun receiver(owner: LifecycleOwner): Receiver {
        return Receiver(owner)
    }

    class Receiver(private val owner: LifecycleOwner) {

        private class OnlyUpdateDispatchValueObserver(
            private val oldValue: Intent?,
            private val observer: Observer<Intent>,
        ) : Observer<Intent?> {
            override fun onChanged(value: Intent?) {
                if (value == null || oldValue === value) {
                    return
                }
                observer.onChanged(value)
            }
        }

        fun register(action: String, eventCallback: EventCallback) {
            val liveData = getOrCreateLocalEventLiveData(action)
            val oldValue = if (liveData.isInitialized) liveData.value else null
            liveData.observe(owner, OnlyUpdateDispatchValueObserver(oldValue, eventCallback))
        }
    }

    class Poster {
        fun post(action: String, extras: Bundle? = null) {
            val intent = Intent(action)
            if (extras != null) {
                intent.putExtras(extras)
            }
            getOrCreateLocalEventLiveData(action).postValue(intent)
        }
    }

}
