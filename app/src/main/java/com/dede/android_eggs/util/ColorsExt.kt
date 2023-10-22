@file:JvmName("ColorsExt")

package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.color.MaterialColors
import com.google.android.material.resources.MaterialAttributes


fun Context.color(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.resolveColor(@AttrRes colorAttr: Int): Int {
    return MaterialColors.getColor(this, colorAttr, "")
}

fun Context.resolveColorStateList(@AttrRes colorAttr: Int): ColorStateList {
    return MaterialColors.getColorStateList(
        this, colorAttr, ColorStateList.valueOf(Color.TRANSPARENT)
    )
}

@SuppressLint("RestrictedApi")
fun Context.resolveColorStateList(
    @AttrRes styleAttr: Int,
    @AttrRes colorAttr: Int,
    useTint: Boolean = false,
    @ColorRes defaultId: Int = -1,
): ColorStateList? {
    val value = MaterialAttributes.resolve(this, styleAttr)
    var colorStateList: ColorStateList? = null
    if (value != null) {
        val resourceId = value.resourceId
        val attrs = intArrayOf(colorAttr)
        if (useTint) {
            val array = TintTypedArray.obtainStyledAttributes(this, resourceId, attrs)
            colorStateList = array.getColorStateList(0)
            array.recycle()
        } else {
            withStyledAttributes(resourceId, attrs) {
                colorStateList = getColorStateList(0)
            }
        }
    }
    if (colorStateList == null && defaultId != -1) {
        colorStateList = ContextCompat.getColorStateList(this, defaultId)
    }
    return colorStateList
}

fun Int.revertColor(): Int {
    return Color.argb(
        Color.alpha(this),
        255 - Color.red(this),
        255 - Color.green(this),
        255 - Color.blue(this)
    )
}
