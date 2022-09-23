package com.dede.android_eggs

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import androidx.browser.customtabs.TrustedWebUtils
import androidx.browser.trusted.TrustedWebActivityIntent
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.google.androidbrowserhelper.trusted.TwaLauncher

/**
 * CustomTabs Help
 *
 * @author hsh
 * @since 2021/11/19 2:14 下午
 */
object ChromeTabsBrowser {

    fun launchUrl(context: Context, uri: Uri) {
        val colorScheme = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> CustomTabsIntent.COLOR_SCHEME_DARK
            AppCompatDelegate.MODE_NIGHT_NO -> CustomTabsIntent.COLOR_SCHEME_LIGHT
            else -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
        }

        val dynamicContext = DynamicColors.wrapContextIfAvailable(context)
        val color = MaterialColors.getColor(dynamicContext,
            com.google.android.material.R.attr.colorSurface,
            Color.WHITE)
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(color)
            .build()

        val builder = UnTrustedWebActivityIntentBuilder(uri)
            .setColorScheme(colorScheme)
            .setDefaultColorSchemeParams(params)

        val launcher = TwaLauncher(context.applicationContext)
        launcher.launch(builder, null, null, null)
    }

    private class UnTrustedWebActivityIntentBuilder(uri: Uri) :
        TrustedWebActivityIntentBuilder(uri) {
        override fun build(session: CustomTabsSession): TrustedWebActivityIntent {
            return super.build(session).apply {
                intent.putExtra(TrustedWebUtils.EXTRA_LAUNCH_AS_TRUSTED_WEB_ACTIVITY, false)
            }
        }
    }
}