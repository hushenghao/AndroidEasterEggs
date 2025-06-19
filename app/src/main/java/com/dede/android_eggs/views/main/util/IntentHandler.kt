package com.dede.android_eggs.views.main.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.main.ACTION_CAT_EDITOR
import com.dede.android_eggs.views.settings.compose.prefs.launchRocketLauncher
import com.dede.basic.delay
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.Calendar
import javax.inject.Inject

class IntentHandler @Inject constructor(@ActivityContext val context: Context) {

    companion object {

        private const val TAG = "IntentHandler"

    }

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards EasterEgg>

    private val eggHandlers: Array<EggHandler> = arrayOf(
        StaticShortcutHandler(),
        FromWidgetHandler(),
        UriHandler(),
    )

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

    private class StaticShortcutHandler : EggHandler {

        companion object {
            // from static shortcut
            const val EXTRA_STATIC_SHORTCUT_ACTION = "extra_static_shortcut_action"

            private const val ACTION_ROCKET_LAUNCHER = "com.dede.android_eggs.action.ROCKET_LAUNCHER"
        }

        override fun handleEggIntent(eggIntent: EggIntent): Boolean {
            val action = eggIntent.extras.getString(EXTRA_STATIC_SHORTCUT_ACTION)
            when (action) {
                ACTION_CAT_EDITOR -> {
                    delay(500L) {
                        // await for the app to be ready
                        LocalEvent.poster().post(ACTION_CAT_EDITOR)
                    }
                    return true
                }
                ACTION_ROCKET_LAUNCHER -> {
                    launchRocketLauncher(eggIntent.context)
                    return true
                }
            }
            return false
        }
    }

    private class FromWidgetHandler : EggHandler {

        companion object {
            // from widget module
            private const val EXTRA_FROM_WIDGET = "extra_from_widget"

            private val hourApiLevelArray: IntArray = intArrayOf(
                // Calendar.HOUR [0-11]
                Build.VERSION_CODES.S,// 0:00
                // [1-11]
                Build.VERSION_CODES.CUPCAKE,
                Build.VERSION_CODES.GINGERBREAD,
                Build.VERSION_CODES.HONEYCOMB,
                Build.VERSION_CODES.KITKAT,
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.M,
                Build.VERSION_CODES.N,
                Build.VERSION_CODES.O,
                Build.VERSION_CODES.P,
                Build.VERSION_CODES.Q,
                Build.VERSION_CODES.R
            )
        }

        override fun handleEggIntent(eggIntent: EggIntent): Boolean {
            val appWidgetId = eggIntent.extras.getInt(EXTRA_FROM_WIDGET, -1)
            if (appWidgetId == -1) {
                return false
            }

            val hour = Calendar.getInstance().get(Calendar.HOUR)
            val apiLevel = hourApiLevelArray[hour]
            val egg = eggIntent.easterEggs.find { apiLevel in it.apiLevelRange }
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
            val egg = eggIntent.easterEggs.find { level in it.apiLevelRange }
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