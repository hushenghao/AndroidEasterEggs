package com.dede.android_eggs.util

import android.content.Context
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import com.dede.basic.requireDrawable
import com.google.android.material.R


fun createRepeatWavyDrawable(
    context: Context,
    @DrawableRes wavyRes: Int,
    @AttrRes tintAttr: Int = R.attr.colorSecondary
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

fun createOvalRipple(context: Context, content: Drawable): RippleDrawable {
    return RippleDrawable(
        context.resolveColorStateList(R.attr.colorControlHighlight),
        content,
        createGradient(GradientDrawable.OVAL, Color.WHITE)
    )
}
