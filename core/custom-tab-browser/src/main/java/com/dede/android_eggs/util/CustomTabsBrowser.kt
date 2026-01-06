package com.dede.android_eggs.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.basic.createChooser
import com.dede.basic.getConfigurationLocales

/**
 * CustomTabs Help
 *
 * @author hsh
 * @since 2021/11/19 2:14 下午
 */
object CustomTabsBrowser {

    @JvmStatic
    fun launchUrl(context: Context, @StringRes urlId: Int) {
        launchUrl(context, context.getString(urlId).toUri())
    }

    @JvmStatic
    fun launchUrl(context: Context, uri: Uri) {
        val colorScheme = when (ThemePrefUtil.getThemeModeValue(context)) {
            ThemePrefUtil.DARK, ThemePrefUtil.AMOLED -> CustomTabsIntent.COLOR_SCHEME_DARK
            ThemePrefUtil.LIGHT -> CustomTabsIntent.COLOR_SCHEME_LIGHT
            else -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
        }

        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ThemeUtils.getThemedSurfaceColor())
            .build()

        val builder = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .setColorScheme(colorScheme)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .setDefaultColorSchemeParams(params)

        val customTabsIntent = builder.build()
        with(customTabsIntent.intent) {
            val applicationId = context.packageName
            putExtra(
                Intent.EXTRA_REFERRER,
                "android-app://%s".format(applicationId).toUri()
            )
            val headers = bundleOf(
                // https://developer.android.google.cn/guide/topics/resources/app-languages?hl=zh-cn#consider-header
                // https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Accept-Language
                "Accept-Language" to context.getConfigurationLocales().toLanguageTags()
            )
            putExtra(Browser.EXTRA_HEADERS, headers)
            putExtra(Browser.EXTRA_APPLICATION_ID, applicationId)
        }
        try {
            customTabsIntent.launchUrl(context, uri)
        } catch (_: Exception) {
            launchUrlByBrowser(context, uri)
        }
    }

    @JvmStatic
    fun launchUrlByBrowser(context: Context, uri: Uri) {
        val target = Intent(Intent.ACTION_VIEW, uri)
        val intent = context.createChooser(target)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
        }
    }
}