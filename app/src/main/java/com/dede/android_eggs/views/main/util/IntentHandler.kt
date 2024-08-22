package com.dede.android_eggs.views.main.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.Calendar
import javax.inject.Inject

class IntentHandler @Inject constructor(@ActivityContext val context: Context) {

    companion object {

        private const val TAG = "IntentHandler"

        const val EXTRA_FROM_WIDGET = "extra_from_widget"
    }

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards EasterEgg>

    private val eggHandlers: Array<EggHandler> = arrayOf(FromWidgetHandler(), UriHandler())

    fun handleIntent(intent: Intent?): Boolean {
        if (intent == null) return false

        val eggIntent = EggIntent(context, easterEggs, intent)
        for (callback in eggHandlers) {
            if (callback.handleEggIntent(eggIntent)) {
                return true
            }
        }
        return false
    }

    private class FromWidgetHandler : EggHandler {

        companion object {
            private val hourApiLevelArray: SparseIntArray = SparseIntArray().apply {
                put(1, Build.VERSION_CODES.CUPCAKE)
                put(2, Build.VERSION_CODES.GINGERBREAD)
                put(3, Build.VERSION_CODES.HONEYCOMB)
                put(4, Build.VERSION_CODES.KITKAT)
                put(5, Build.VERSION_CODES.LOLLIPOP)
                put(6, Build.VERSION_CODES.M)
                put(7, Build.VERSION_CODES.N)
                put(8, Build.VERSION_CODES.O)
                put(9, Build.VERSION_CODES.P)
                put(10, Build.VERSION_CODES.Q)
                put(11, Build.VERSION_CODES.R)
                put(12, Build.VERSION_CODES.S)
            }
        }

        override fun handleEggIntent(eggIntent: EggIntent): Boolean {
            val appWidgetId = eggIntent.extras.getInt(EXTRA_FROM_WIDGET, -1)
            if (appWidgetId == -1) {
                return false
            }

            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) % 12
            val apiLevel = hourApiLevelArray.get(hour)
            val egg = eggIntent.easterEggs.find { apiLevel in it.apiLevel }
            if (egg != null) {
                EggActionHelp.launchEgg(eggIntent.context, egg)
            }
            return egg != null
        }
    }

    private class UriHandler : EggHandler {

        companion object {
            private const val SCHEME = "egg"
            private const val HOST = "easter_egg"

            private const val PATH_API_LEVEL = "api"
        }

        private fun filterUri(uri: Uri?): Boolean {
            return uri != null && uri.scheme == SCHEME && uri.host == HOST
        }

        private fun handleApiPath(eggIntent: EggIntent, uri: Uri): Boolean {
            // egg://easter_egg/api/34
            val levelStr = uri.pathSegments.getOrNull(1) ?: return false
            val level = levelStr.toIntOrNull() ?: return false
            val egg = eggIntent.easterEggs.find { level in it.apiLevel }
            if (egg != null) {
                EggActionHelp.launchEgg(eggIntent.context, egg)
            }
            return egg != null
        }

        override fun handleEggIntent(eggIntent: EggIntent): Boolean {
            val uri = eggIntent.uri ?: return false
            if (!filterUri(uri)) {
                return false
            }
            Log.i(TAG, "handleScheme: $uri")
            return when (uri.pathSegments.firstOrNull()) {
                PATH_API_LEVEL -> handleApiPath(eggIntent, uri)
                else -> false
            }
        }
    }

    private class EggIntent(
        val context: Context,
        val easterEggs: List<EasterEgg>,
        intent: Intent,
    ) {
        val uri: Uri? = intent.data
        val extras: Bundle = intent.extras ?: Bundle.EMPTY
    }

    private interface EggHandler {
        fun handleEggIntent(eggIntent: EggIntent): Boolean
    }
}