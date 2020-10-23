package com.dede.android_eggs

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.util.SparseArray

/**
 * @author hsh
 * @since 2020/10/23 11:42 AM
 */
typealias RIntent = (Context) -> Intent

class EasterEggsDispatcher(private val context: Context) {

    private val dispatcherMap = SparseArray<RIntent>()

    fun register(versionCode: Int, system: RIntent): EasterEggsDispatcher {
        dispatcherMap.put(versionCode, system)
        return this
    }

    fun dispatch(versionCode: Int): Boolean {
        val easterEggIntent = dispatcherMap[versionCode] ?: return false

        var success = false
        if (Build.VERSION.SDK_INT == versionCode) {
            success = startAct(context, easterEggIntent.invoke(context))
        }
        return success
    }

    private fun startAct(context: Context, intent: Intent): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("EasterEggsDispatcher", "Unable to start activity $intent");
            false
        }
    }
}