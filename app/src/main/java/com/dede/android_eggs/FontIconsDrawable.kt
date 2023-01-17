package com.dede.android_eggs

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.Paint.FontMetrics
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import androidx.annotation.Dimension
import com.dede.basic.globalContext
import com.google.android.material.color.MaterialColors
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Material Icons.
 *
 * @author shhu
 * @since 2023/1/17
 */
class FontIconsDrawable(
    context: Context,
    private val unicode: String,
    @Dimension(unit = Dimension.DP) size: Float = -1f,
) : Drawable() {

    companion object {
        val ICONS_TYPEFACE: Typeface by lazy {
            Typeface.createFromAsset(globalContext.assets, "icons.otf")
        }
    }

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val metrics = FontMetrics()

    private var dimension: Int = -1
    private var colorStateList: ColorStateList? = null

    init {
        paint.typeface = ICONS_TYPEFACE
        paint.textAlign = Paint.Align.CENTER
        val color = MaterialColors.getColor(
            context,
            com.google.android.material.R.attr.colorAccent,
            Color.WHITE
        )
        paint.color = color
        if (size > 0) {
            dimension = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                size,
                context.resources.displayMetrics
            ).roundToInt()
            paint.textSize = dimension.toFloat()
            paint.getFontMetrics(metrics)
        }
    }

    fun setColor(color: Int) {
        paint.color = color
        invalidateSelf()
    }

    fun setColorStateList(colorStateList: ColorStateList?) {
        if (colorStateList != this.colorStateList) {
            this.colorStateList = colorStateList
            invalidateSelf()
        }
    }

    override fun onStateChange(state: IntArray): Boolean {
        if (colorStateList != null) {
            return true
        }
        return super.onStateChange(state)
    }

    override fun getIntrinsicHeight(): Int {
        return if (dimension > 0) dimension else -1
    }

    override fun getIntrinsicWidth(): Int {
        return if (dimension > 0) dimension else -1
    }

    override fun onBoundsChange(bounds: Rect) {
        if (dimension > 0) {
            return
        }

        val size = min(bounds.width(), bounds.height())
        if (size <= 0) return

        paint.textSize = size.toFloat()
        paint.getFontMetrics(metrics)
    }

    override fun draw(canvas: Canvas) {
        if (unicode.isEmpty()) return

        val colorStateList = this.colorStateList
        if (colorStateList != null) {
            paint.color = colorStateList.getColorForState(state, colorStateList.defaultColor)
        }
        val y = (metrics.descent - metrics.ascent) / 2 - metrics.ascent / 2
        canvas.drawText(unicode, bounds.exactCenterX(), y, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}