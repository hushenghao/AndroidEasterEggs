@file:JvmName("SpanUtils")

package com.dede.android_eggs.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.annotation.DrawableRes
import androidx.core.graphics.withTranslation
import com.dede.basic.requireDrawable

fun SpannableStringBuilder.append(
    text: CharSequence?,
    vararg whats: Any,
    flag: Int = Spannable.SPAN_INCLUSIVE_EXCLUSIVE,
): SpannableStringBuilder {
    if (text.isNullOrEmpty()) return this

    val start = length
    append(text)
    for (what in whats) {
        setSpan(what, start, length, flag)
    }
    return this
}

fun centerImageSpan(context: Context, @DrawableRes resourceId: Int): ImageSpan =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ImageSpan(context, resourceId, ImageSpan.ALIGN_CENTER)
    } else {
        // apply tint
        API28CenterImageSpan(context.requireDrawable(resourceId))
    }

private class API28CenterImageSpan : ImageSpan {

    constructor(drawable: Drawable) : super(drawable)

    constructor(context: Context, @DrawableRes resourceId: Int) : super(context, resourceId)

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint,
    ) {
        val d: Drawable = drawable
        val transY = top + (bottom - top) / 2f - d.bounds.height() / 2f
        canvas.withTranslation(x, transY) {
            d.draw(this)
        }
    }
}
