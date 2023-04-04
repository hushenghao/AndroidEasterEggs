@file:JvmName("SpanUtils")

package com.dede.android_eggs.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.core.graphics.withTranslation
import com.dede.basic.requireDrawable
import com.google.android.material.color.MaterialColors

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

fun foregroundColorSpan(context: Context, @AttrRes colorAttributeResId: Int): ForegroundColorSpan =
    ForegroundColorSpan(MaterialColors.getColor(context, colorAttributeResId, Color.WHITE))

fun centerImageSpan(context: Context, @DrawableRes resourceId: Int): ImageSpan =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ImageSpan(context, resourceId, ImageSpan.ALIGN_CENTER)
    } else {
        // apply tint
        CenterImageSpan(context.requireDrawable(resourceId))
    }

fun customTabURLSpan(url: String): URLSpan = object : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
    }

    override fun onClick(widget: View) {
        CustomTabsBrowser.launchUrl(widget.context, Uri.parse(url))
    }
}

fun TextView.enableClickSpan() {
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

class CenterImageSpan : ImageSpan {

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


