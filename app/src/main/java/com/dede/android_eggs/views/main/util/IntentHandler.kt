package com.dede.android_eggs.views.main.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.dede.android_eggs.egg_stats.EggLaunchSource
import com.dede.android_eggs.navigation.DeepLink
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.views.settings.compose.prefs.launchRocketLauncher
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.findEggByPublicApiLevel
import dagger.hilt.android.qualifiers.ActivityContext
import java.time.LocalTime
import javax.inject.Inject

private const val TAG = "IntentHandler"

class IntentHandler @Inject constructor(@ActivityContext val context: Context) {

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards EasterEgg>

    private val eggHandlers: Array<EggHandler> = arrayOf(
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

    private class FromWidgetHandler : EggHandler {

        companion object {
            // from widget module
            private const val EXTRA_FROM_ANALOG_CLOCK_WIDGET_ACTION =
                "extra_from_analog_clock_widget_action"

            private val hourApiLevelArray: IntArray = intArrayOf(
                // 12-hour clock [0-11]
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
            val action = eggIntent.extras.getInt(EXTRA_FROM_ANALOG_CLOCK_WIDGET_ACTION, -1)
            if (action == 1) {
                // com.dede.android_eggs.views.widget.AnalogClockWidgetClickAction.OPEN_APP
                return true
            }
            if (action == 0) {
                // com.dede.android_eggs.views.widget.AnalogClockWidgetClickAction.OPEN_EGG
                val hour = LocalTime.now().hour % 12
                val apiLevel = hourApiLevelArray[hour]
                val egg = eggIntent.easterEggs.findEggByPublicApiLevel(apiLevel)
                if (egg != null) {
                    EggActionHelp.launchEgg(eggIntent.context, egg, EggLaunchSource.WIDGET)
                }
                return egg != null
            }
            return false
        }
    }

    private class UriHandler : EggHandler {

        private fun filterUri(uri: Uri?): Boolean {
            return uri != null && uri.scheme == SCHEME && uri.host == HOST
        }

        private fun handleApiPath(eggIntent: EggIntent, uri: Uri): Boolean {
            // egg://easter_egg/api/34
            val levelStr = uri.pathSegments.getOrNull(1) ?: return false
            val level = levelStr.toIntOrNull() ?: return false
            val egg = eggIntent.easterEggs.findEggByPublicApiLevel(level)
            if (egg != null) {
                EggActionHelp.launchEgg(eggIntent.context, egg, EggLaunchSource.DEEP_LINK)
            }
            return egg != null
        }

        private fun handleCatEditorPath(uri: Uri): Boolean {
            // egg://easter_egg/cat_editor/12345
            val seed = uri.pathSegments.getOrNull(1)?.toLongOrNull()
            DeepLink.setNavKey(EasterEggsDestination.CatEditor(seed))
            return true
        }

        override fun handleEggIntent(eggIntent: EggIntent): Boolean {
            val uri = eggIntent.uri ?: return false
            if (!filterUri(uri)) {
                return false
            }
            Log.i(TAG, "handleScheme: $uri")
            return when (uri.pathSegments.firstOrNull()) {
                PATH_API_LEVEL -> handleApiPath(eggIntent, uri)
                PATH_CAT_EDITOR -> handleCatEditorPath(uri)
                PATH_ROCKET_LAUNCHER -> {
                    launchRocketLauncher(eggIntent.context)
                    true
                }
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

    companion object {
        private const val SCHEME = "egg"
        private const val HOST = "easter_egg"
        private const val PATH_API_LEVEL = "api"
        private const val PATH_ROCKET_LAUNCHER = "rocket_launcher"
        private const val PATH_CAT_EDITOR = "cat_editor"

        /** egg://easter_egg/api/34 */
        internal fun createEggUri(apiLevel: Int): Uri {
            return Uri.Builder()
                .scheme(SCHEME)
                .authority(HOST)
                .appendPath(PATH_API_LEVEL)
                .appendPath(apiLevel.toString())
                .build()
        }

        /** egg://easter_egg/cat_editor/12345 */
        internal fun createCatEditorUri(seed: Long): Uri {
            return Uri.Builder()
                .scheme(SCHEME)
                .authority(HOST)
                .appendPath(PATH_CAT_EDITOR)
                .appendPath(seed.toString())
                .build()
        }
    }
}