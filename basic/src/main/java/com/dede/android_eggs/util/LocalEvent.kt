package com.dede.android_eggs.util

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.collection.ArrayMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

typealias EventCallback = (intent: Intent) -> Unit

object LocalEvent {

    private val localEventLiveDataMap = ArrayMap<String, MutableLiveData<Intent?>>()

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

    fun registerTrimMemoryCallback(application: Application) {
        application.registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
            }

            override fun onTrimMemory(level: Int) {
                if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
                    trimToSize(0)
                } else if (level <= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
                    trimToSize(localEventLiveDataMap.size / 2)
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onLowMemory() {
            }
        })
    }

    fun trimToSize(size: Int) {
        var trimCount = 0
        val keys = HashSet(localEventLiveDataMap.keys)
        for (key in keys) {
            val liveData = localEventLiveDataMap[key]
            if (liveData == null) {
                localEventLiveDataMap.remove(key)
                continue
            }
            if (!liveData.hasObservers()) {
                localEventLiveDataMap.remove(key)
                trimCount++
            }
            if (size > 0 && size >= localEventLiveDataMap.size) {
                break
            }
        }
        Log.i("LocalEvent", "trimToSize, trim: %d".format(trimCount))
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
