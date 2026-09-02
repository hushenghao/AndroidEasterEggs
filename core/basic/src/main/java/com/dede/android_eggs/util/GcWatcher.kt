package com.dede.android_eggs.util

import java.lang.ref.WeakReference

/**
 * GC Watcher
 * 
 * @see [com.android.internal.os.BinderInternal]
 */
class GcWatcher private constructor() {

    companion object {

        private val sInstance = GcWatcher()

        private val sGcWatchers: ArrayList<Runnable> = ArrayList()
        private var sTempWatchers: Array<Runnable?> = emptyArray()

        private var sGcWatcherRef: WeakReference<GcWatcherObj> = WeakReference(GcWatcherObj())

        fun get(): GcWatcher {
            return sInstance
        }
    }

    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    private class GcWatcherObj : Object() {
        override fun finalize() {
            synchronized(sGcWatchers) {
                sTempWatchers = sGcWatchers.toArray(sTempWatchers)
            }
            for (gcWatcher in sTempWatchers) {
                gcWatcher?.run()
            }
            sGcWatcherRef = WeakReference(GcWatcherObj())
        }
    }

    fun addWatcher(watcher: Runnable) {
        synchronized(sGcWatchers) {
            sGcWatchers.add(watcher)
        }
    }

    fun removeWatcher(watcher: Runnable) {
        synchronized(sGcWatchers) {
            sGcWatchers.remove(watcher)
        }
    }
}
