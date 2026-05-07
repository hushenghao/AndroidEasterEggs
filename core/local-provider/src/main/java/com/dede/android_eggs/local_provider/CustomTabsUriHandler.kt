package com.dede.android_eggs.local_provider

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import com.dede.android_eggs.util.CustomTabsBrowser

/**
 * A [UriHandler] that uses Custom Tabs to open URLs.
 */
private class CustomTabsUriHandler(
    private val context: Context,
) : UriHandler {

    override fun openUri(uri: String) {
        CustomTabsBrowser.launchUrl(context, uri)
    }
}

@Composable
fun rememberCustomTabsUriHandler(): UriHandler {
    val context = LocalContext.current
    return remember(context) {
        CustomTabsUriHandler(context)
    }
}
