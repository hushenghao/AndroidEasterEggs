@file:JvmName("ContextExt")

package com.dede.basic

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.dede.android_eggs.util.LocalEvent


val globalContext: Context
    get() = GlobalContext.globalContext

@SuppressLint("StaticFieldLeak")
object GlobalContext {

    internal lateinit var globalContext: Context
        private set

    class Initializer : androidx.startup.Initializer<Unit> {
        override fun create(context: Context) {
            globalContext = context
            LocalEvent.registerTrimMemoryCallback(context as Application)
        }

        override fun dependencies(): List<Class<out androidx.startup.Initializer<*>>> = emptyList()
    }
}

fun Context.getActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, resId, duration).apply(Toast::show)
}

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, text, duration).apply(Toast::show)
}

fun Context.createChooser(target: Intent, title: CharSequence? = null): Intent {
    return Intent.createChooser(target, title ?: getString(R.string.title_open_with))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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

fun Context.checkSelfPermissions(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(
                this, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}
