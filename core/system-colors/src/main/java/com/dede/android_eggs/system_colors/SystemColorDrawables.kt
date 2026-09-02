package com.dede.android_eggs.system_colors

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import androidx.annotation.DrawableRes
import com.dede.basic.requireDrawable

/**
 * Compose an egg logo drawable with the dynamic system color as the background:
 * the [background] builder is invoked only when the color resolves, its drawable
 * is tinted with the resolved [Context.getSystemColor] and layered under the
 * static foreground; falls back to the original drawable when the color name
 * cannot be resolved.
 */
object SystemColorDrawables {

    @JvmStatic
    fun create(
        context: Context,
        colorName: String,
        background: () -> Drawable,
        @DrawableRes foregroundRes: Int,
        @DrawableRes fallbackRes: Int,
    ): Drawable {
        val color = try {
            context.getSystemColor(colorName)
        } catch (_: Resources.NotFoundException) {
            -1
        }
        if (color == -1) {
            return context.requireDrawable(fallbackRes)
        }
        val bg = background().mutate().apply {
            if (this is GradientDrawable) {
                // GradientDrawable does not apply ColorFilter, fill directly
                setColor(color)
            } else {
                colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }
        return LayerDrawable(arrayOf(bg, context.requireDrawable(foregroundRes)))
    }

    @JvmStatic
    fun create(
        context: Context,
        colorName: String,
        @DrawableRes backgroundRes: Int,
        @DrawableRes foregroundRes: Int,
        @DrawableRes fallbackRes: Int,
    ): Drawable = create(
        context,
        colorName,
        { context.requireDrawable(backgroundRes) },
        foregroundRes,
        fallbackRes,
    )
}
