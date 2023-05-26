package com.dede.android_eggs.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.dede.android_eggs.R
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors

/**
 * CustomTabs Help
 *
 * @author hsh
 * @since 2021/11/19 2:14 下午
 */
object CustomTabsBrowser {

    fun launchUrl(context: Context, uri: Uri) {
        val colorScheme = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> CustomTabsIntent.COLOR_SCHEME_DARK
            AppCompatDelegate.MODE_NIGHT_NO -> CustomTabsIntent.COLOR_SCHEME_LIGHT
            else -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
        }

        val dynamicContext = DynamicColors.wrapContextIfAvailable(context)
        val color = MaterialColors.getColor(
            dynamicContext,
            com.google.android.material.R.attr.colorSurface,
            Color.WHITE
        )
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(color)
            .build()

        val builder = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setColorScheme(colorScheme)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .setDefaultColorSchemeParams(params)

        val customTabsIntent = builder.build()
        customTabsIntent.intent.putExtra(
            Intent.EXTRA_REFERRER,
            Uri.parse("android-app://%s".format(context.packageName))
        )
        try {
            customTabsIntent.launchUrl(context, uri)
        } catch (e: Exception) {
            launchUrlByBrowser(context, uri)
        }
    }

    fun launchUrlByBrowser(context: Context, uri: Uri) {
        val target = Intent(Intent.ACTION_VIEW, uri)
        val intent = Intent.createChooser(target, context.getString(R.string.title_open_with))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            ContextCompat.startActivity(context, intent, null)
        } catch (e: ActivityNotFoundException) {
        }
    }
}