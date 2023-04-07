package com.dede.android_eggs.util

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView

private val defaultCompoundDrawable = ColorDrawable(0)

fun TextView.updateCompoundDrawablesRelative(
    start: Drawable? = defaultCompoundDrawable,
    top: Drawable? = defaultCompoundDrawable,
    end: Drawable? = defaultCompoundDrawable,
    bottom: Drawable? = defaultCompoundDrawable,
) {
    val drawables = compoundDrawablesRelative
    if (start !== defaultCompoundDrawable) drawables[0] = start
    if (top !== defaultCompoundDrawable) drawables[1] = top
    if (end !== defaultCompoundDrawable) drawables[2] = end
    if (bottom !== defaultCompoundDrawable) drawables[3] = bottom
    setCompoundDrawablesRelative(drawables[0], drawables[1], drawables[2], drawables[3])
}