@file:JvmName("ContextExt")

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat

/**
 * Created by shhu on 2022/9/27 14:26.
 *
 * @author shhu
 * @since 2022/9/27
 */

val globalContext: Context get() = GlobalContext.globalContext

@SuppressLint("StaticFieldLeak")
object GlobalContext {
    lateinit var globalContext: Context
        private set

    fun init(context: Context) {
        globalContext = context
    }
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

val Int.string: String get() = globalContext.getString(this)

val Int.color: Int get() = ContextCompat.getColor(globalContext, this)

val Int.drawable: Drawable get() = globalContext.requireDrawable(this)