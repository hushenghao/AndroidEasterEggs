package com.dede.android_eggs.util

import android.content.Context
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.util.LayoutDirection
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import com.dede.basic.requireDrawable
import com.google.android.material.R


fun Drawable.inset(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0): Drawable {
    val isRtl = DrawableCompat.getLayoutDirection(this) == LayoutDirection.RTL
    val left: Int
    val right: Int
    if (isRtl) {
        left = end
        right = start
    } else {
        left = start
        right = end
    }
    return InsetDrawable(this, left, top, right, bottom)
}

fun createRepeatWavyDrawable(
    context: Context,
    @DrawableRes wavyRes: Int,
    @AttrRes tintAttr: Int = R.attr.colorSecondary,
): Drawable {
    val bitmap = context.requireDrawable(wavyRes).toBitmap()
    return BitmapDrawable(context.resources, bitmap).apply {
        tileModeX = Shader.TileMode.REPEAT
        setTint(context.resolveColor(tintAttr))
    }
}

fun createGradient(shape: Int, @ColorInt color: Int): GradientDrawable {
    return GradientDrawable().apply {
        setColor(color)
        this.shape = shape
    }
}

fun createLayer(vararg drawables: Drawable): LayerDrawable {
    return LayerDrawable(drawables)
}

fun createOvalWrapper(drawable: Drawable, @ColorInt color: Int): Drawable {
    return createLayer(createGradient(GradientDrawable.OVAL, color), drawable)
}

fun createOvalRipple(context: Context, content: Drawable?): RippleDrawable {
    return createRipple(
        context, content, createGradient(GradientDrawable.OVAL, Color.WHITE)
    )
}

fun createRipple(context: Context, content: Drawable?, mask: Drawable?): RippleDrawable {
    return RippleDrawable(
        context.resolveColorStateList(R.attr.colorControlHighlight), content, mask
    )
}
