package com.dede.android_eggs.util

import android.content.Context
import androidx.compose.ui.platform.UriHandler

/**
 * A [UriHandler] that uses Custom Tabs to open URLs.
 */
class CustomTabsUriHandler(
    private val context: Context,
) : UriHandler {

    override fun openUri(uri: String) {
        CustomTabsBrowser.launchUrl(context, uri)
    }
}
