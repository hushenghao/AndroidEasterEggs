@file:JvmName("ContextExt")

package com.dede.basic

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat


val globalContext: Context
    get() = GlobalContext.globalContext

val globalThemeContext: Context
    get() = GlobalContext.globalThemeContext

@SuppressLint("StaticFieldLeak")
object GlobalContext {

    internal lateinit var globalContext: Context
        private set

    internal val globalThemeContext by lazy { globalContext.createThemeWrapperContext() }

    class Initializer : androidx.startup.Initializer<Unit> {
        override fun create(context: Context) {
            globalContext = context
        }

        override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> = emptyList()
    }
}


fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resId, duration).show()
}

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.copy(text: String) {
    val service = getSystemService<ClipboardManager>() ?: return
    service.setPrimaryClip(ClipData.newPlainText(null, text))
    toast(android.R.string.copy)
}


fun Context.createThemeWrapperContext(): Context {
    if (this is AppCompatActivity) {
        return this
    }
    // androidx.appcompat.app.AppCompatDelegateImpl.attachBaseContext2
    val themeWrapper = ContextThemeWrapper(this, applicationInfo.theme)
    val mode = when (AppCompatDelegate.getDefaultNightMode()) {
        AppCompatDelegate.MODE_NIGHT_YES -> Configuration.UI_MODE_NIGHT_YES
        AppCompatDelegate.MODE_NIGHT_NO -> Configuration.UI_MODE_NIGHT_NO
        else -> {
            val appConfig = this.applicationContext.resources.configuration
            appConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        }
    }
    val config = Configuration()
    val locales = ContextCompat.getContextForLanguage(this).getConfigurationLocales()
    ConfigurationCompat.setLocales(config, locales)
    config.fontScale = 0f
    config.uiMode = mode or (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
    themeWrapper.applyOverrideConfiguration(config)
    return themeWrapper
}

fun Context.getLayoutDirection(): Int {
    return resources.configuration.layoutDirection
}

fun Context.createLocalesContext(locales: LocaleListCompat): Context {
    val config = Configuration(resources.configuration)
    ConfigurationCompat.setLocales(config, locales)
    return this.createConfigurationContext(config)
}

fun Context.getConfigurationLocales(): LocaleListCompat {
    val localeConfig = ContextCompat.getContextForLanguage(this).resources.configuration
    return ConfigurationCompat.getLocales(localeConfig)
}

fun Context.createScaleWrapper(scale: Float): Context {
    val override = Configuration(resources.configuration).apply {
        densityDpi = (densityDpi * scale).toInt()
        fontScale *= scale
        val newMode = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> Configuration.UI_MODE_NIGHT_YES
            AppCompatDelegate.MODE_NIGHT_NO -> Configuration.UI_MODE_NIGHT_NO
            else -> {
                // If we're following the system, we just use the system default from the
                // application context
                val appConfig = this@createScaleWrapper.applicationContext.resources.configuration
                appConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
            }
        }
        uiMode = (newMode or (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()))
    }
    return ContextThemeWrapper(this, androidx.appcompat.R.style.Theme_AppCompat_Empty).apply {
        applyOverrideConfiguration(override)
    }
}
