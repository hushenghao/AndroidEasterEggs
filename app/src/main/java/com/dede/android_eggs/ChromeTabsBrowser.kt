package com.dede.android_eggs

import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.*
import com.google.android.material.color.MaterialColors

/**
 * CustomTabs Help
 *
 * @author hsh
 * @since 2021/11/19 2:14 下午
 */
object ChromeTabsBrowser {

    // Package name for the Chrome channel the client wants to connect to. This depends on the channel name.
    // Stable = com.android.chrome
    // Beta = com.chrome.beta
    // Dev = com.chrome.dev
    private const val CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome"
    private const val CUSTOM_SESSION_ID = 10

    private var mayLaunchUrl: Uri? = null
    private val customTabsCallback = CustomTabsCallback()
    private var customTabsSession: CustomTabsSession? = null

    private val customTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onServiceDisconnected(name: ComponentName?) {
            customTabsSession = null
        }

        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            val result = client.warmup(0)
            if (result) {
                val session = client.newSession(customTabsCallback, CUSTOM_SESSION_ID)
                if (session != null) {
                    customTabsSession = session
                    if (mayLaunchUrl != null) {
                        session.mayLaunchUrl(mayLaunchUrl, null, null)
                    }
                }
            }
        }
    }

    /**
     * 预热并预加载
     */
    fun warmup(context: Context, mayLaunchUrl: Uri? = null) {
        if (customTabsSession != null) return
        this.mayLaunchUrl = mayLaunchUrl
        val appContext = context.applicationContext
        CustomTabsClient.bindCustomTabsService(
            appContext,
            CUSTOM_TAB_PACKAGE_NAME,
            customTabsServiceConnection
        )
    }

    fun launchUrl(context: Context, uri: Uri) {
        val colorScheme =
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                CustomTabsIntent.COLOR_SCHEME_DARK else CustomTabsIntent.COLOR_SCHEME_LIGHT

        val color = MaterialColors.getColor(context, android.R.attr.colorPrimary, Color.WHITE)
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(color)
            .build()

        val builder = CustomTabsIntent.Builder()
            .setColorScheme(colorScheme)
            .setDefaultColorSchemeParams(params)
        val session = customTabsSession
        if (session != null) {
            builder.setSession(session)
        }
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, uri)
    }
}