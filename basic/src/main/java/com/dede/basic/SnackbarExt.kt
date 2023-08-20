@file:JvmName("SnackbarUtils")

package com.dede.basic

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.R
import com.google.android.material.snackbar.Snackbar

const val LENGTH_SHORT = Snackbar.LENGTH_SHORT
const val LENGTH_LONG = Snackbar.LENGTH_LONG

private fun isAppCompatTheme(context: Context): Boolean {
    val a = context.obtainStyledAttributes(R.styleable.AppCompatTheme)
    val isAppCompatTheme = a.hasValue(R.styleable.AppCompatTheme_windowActionBar)
    a.recycle()
    return isAppCompatTheme
}

fun Activity.snackbar(text: CharSequence, duration: Int = LENGTH_SHORT) {
    val view = findViewById<View>(android.R.id.content)
    val context = if (isAppCompatTheme(this)) this else createThemeWrapperContext()
    Snackbar.make(context, view, text, duration).show()
}

fun Activity.snackbar(@StringRes resId: Int, duration: Int = LENGTH_SHORT) {
    this.snackbar(this.getString(resId), duration)
}

fun View.snackbar(text: CharSequence, duration: Int = LENGTH_SHORT) {
    val context = this.context
    val themeCtx = if (isAppCompatTheme(context)) context else context.createThemeWrapperContext()
    Snackbar.make(themeCtx, this, text, duration).show()
}

fun View.snackbar(@StringRes resId: Int, duration: Int = LENGTH_SHORT) {
    this.snackbar(this.resources.getString(resId), duration)
}