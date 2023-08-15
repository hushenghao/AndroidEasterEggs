@file:JvmName("ContextExt")

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale


val globalContext: Context get() = GlobalContext.globalContext
val globalThemeContext: Context get() = GlobalContext.globalThemeContext

@SuppressLint("StaticFieldLeak")
object GlobalContext {
    lateinit var globalContext: Context
        private set
    val globalThemeContext by lazy { globalContext.createThemeWrapperContext() }

    class Initializer : androidx.startup.Initializer<Unit> {
        override fun create(context: Context) {
            globalContext = context
        }

        override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> = emptyList()
    }
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
    config.setLocales(locales)
    config.fontScale = 0f
    config.uiMode = mode or (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
    themeWrapper.applyOverrideConfiguration(config)
    return themeWrapper
}

fun Context.createLocalesContext(locales: LocaleListCompat): Context {
    val config = Configuration(resources.configuration)
    config.setLocales(locales)
    return this.createConfigurationContext(config)
}

fun Configuration.setLocales(locales: LocaleListCompat) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        ConfigurationCompat.setLocales(this, locales)
    } else if (!locales.isEmpty) {
        val locale = locales.get(0).tempFixLocaleForApi23()
        setLocale(locale)
    }
}

fun Context.getConfigurationLocales(): LocaleListCompat {
    val localeConfig = ContextCompat.getContextForLanguage(this).resources.configuration
    return ConfigurationCompat.getLocales(localeConfig)
}

// temp fix api 23
private fun Locale?.tempFixLocaleForApi23(): Locale? {
    if (this == Locale.CHINESE) {
        return Locale.SIMPLIFIED_CHINESE
    }
    return this
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
