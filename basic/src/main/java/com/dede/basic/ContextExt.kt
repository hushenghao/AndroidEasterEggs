@file:JvmName("ContextExt")

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper


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
    config.fontScale = 0f
    config.uiMode = mode or (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
    themeWrapper.applyOverrideConfiguration(config)
    return themeWrapper
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
