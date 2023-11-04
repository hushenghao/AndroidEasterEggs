package com.dede.android_eggs.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.dede.android_eggs.main.entity.toEgg
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class SchemeHandler @Inject constructor(@ActivityContext val context: Context) {

    companion object {

        private const val TAG = "SchemeHandler"

        private const val SCHEME = "egg"
        private const val HOST = "easter_egg"

        private const val PATH_API_LEVEL = "api"
    }

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards EasterEgg>

    fun handleIntent(intent: Intent?): Boolean {
        val uri = filterUri(intent) ?: return false
        Log.i(TAG, "handleScheme: $uri")
        when (uri.pathSegments.firstOrNull()) {
            PATH_API_LEVEL -> {
                return handleApiPath(context, uri)
            }
        }
        return false
    }

    private fun filterUri(intent: Intent?): Uri? {
        val uri = intent?.data
        if (uri != null && uri.scheme == SCHEME && uri.host == HOST) {
            return uri
        }
        return null
    }

    private fun handleApiPath(context: Context, uri: Uri): Boolean {
        // egg://easter_egg/api/34
        val levelStr = uri.pathSegments.getOrNull(1) ?: return false
        val level = levelStr.toIntOrNull() ?: return false
        val egg = easterEggs.find { level in it.apiLevel }
        if (egg != null) {
            EggActionHelp.launchEgg(context, egg.toEgg())
        }
        return egg != null
    }
}