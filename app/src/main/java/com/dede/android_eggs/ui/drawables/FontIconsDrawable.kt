package com.dede.android_eggs.ui.drawables

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.Paint.FontMetrics
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import androidx.annotation.Keep
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import com.dede.android_eggs.R
import com.dede.basic.dp
import com.dede.basic.globalContext
import com.google.android.material.color.MaterialColors
import kotlin.math.min
import com.google.android.material.R as M3R

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
        val typeface: Typeface by lazy {
            checkNotNull(ResourcesCompat.getFont(globalContext, R.font.icons))
        }
    }

    constructor(
        context: Context,
        unicode: String,
        @AttrRes colorAttributeResId: Int,
        @Dimension(unit = Dimension.DP) size: Float = -1f,
    ) : this(context, unicode, size) {
        val color = MaterialColors.getColor(context, colorAttributeResId, Color.WHITE)
        setColor(color)
    }

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val metrics = FontMetrics()
    private val padding = Rect()
    private val tempBounds = Rect()

    private var dimension: Int = -1
    private var colorStateList: ColorStateList? = null
    private var degree: Float = 0f

    init {
        paint.typeface = typeface
        paint.textAlign = Paint.Align.CENTER
        val color = MaterialColors.getColor(context, M3R.attr.colorControlNormal, Color.WHITE)
        paint.color = color
        if (size > 0) {
            dimension = size.dp
            setBounds(0, 0, dimension, dimension)
            computeIconSize()
        }
    }

    @Keep
    fun setRotate(@FloatRange(from = -360.0, to = 360.0) degree: Float) {
        val newDegree = degree % 360f
        if (newDegree != this.degree) {
            this.degree = newDegree
            invalidateSelf()
        }
    }

    @Keep
    fun getRotate(): Float = this.degree

    fun setTypeface(typeface: Typeface) {
        paint.typeface = typeface
        invalidateSelf()
    }

    fun setColor(color: Int) {
        if (color != paint.color) {
            paint.color = color
            invalidateSelf()
        }
    }

    fun setColorStateList(colorStateList: ColorStateList?) {
        if (colorStateList != this.colorStateList) {
            this.colorStateList = colorStateList
            onStateChange(state)
        }
    }

    override fun onStateChange(state: IntArray): Boolean {
        val colorStateList = this.colorStateList
        if (colorStateList != null) {
            setColor(colorStateList.getColorForState(state, colorStateList.defaultColor))
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

    override fun getPadding(padding: Rect): Boolean {
        padding.set(this.padding)
        return true
    }

    fun setPadding(@Dimension padding: Int) {
        this.setPadding(padding, padding, padding, padding)
    }

    fun setPadding(
        @Dimension left: Int,
        @Dimension top: Int,
        @Dimension right: Int,
        @Dimension bottom: Int,
    ) {
        if (this.padding.left == left && this.padding.top == top &&
            this.padding.right == right && this.padding.bottom == bottom
        ) return

        this.padding.set(left, top, right, bottom)
        computeIconSize()
        invalidateSelf()
    }

    fun setPadding(padding: Rect) {
        setPadding(padding.left, padding.top, padding.right, padding.bottom)
    }

    private fun computeIconSize() {
        if (dimension > 0) {
            tempBounds.set(0, 0, dimension, dimension)
        } else {
            tempBounds.set(bounds)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            tempBounds.inset(padding.left, padding.top, padding.right, padding.bottom)
        } else {
            val (left, top, right, bottom) = tempBounds
            tempBounds.set(
                left + padding.left,
                top + padding.top,
                right - padding.right,
                bottom - padding.bottom
            )
        }

        val size = min(tempBounds.width(), tempBounds.height())
        if (size <= 0) return

        paint.textSize = size.toFloat()
        paint.getFontMetrics(metrics)
    }

    override fun onBoundsChange(bounds: Rect) {
        if (dimension > 0) {
            return
        }
        computeIconSize()
    }

    override fun draw(canvas: Canvas) {
        if (unicode.isEmpty()) return

        val count = canvas.save()
        val x = tempBounds.exactCenterX()
        canvas.rotate(degree, x, tempBounds.exactCenterY())
        val y = (metrics.descent - metrics.ascent) / 2 - metrics.ascent / 2 + padding.top
        canvas.drawText(unicode, x, y, paint)
        canvas.restoreToCount(count)
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