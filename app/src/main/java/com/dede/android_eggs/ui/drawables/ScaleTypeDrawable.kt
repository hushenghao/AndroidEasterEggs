package com.dede.android_eggs.ui.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.Drawable

/**
 * Created by shhu on 2023/2/3 16:52.
 *
 * @author shhu
 * @since 2023/2/3
 */
class ScaleTypeDrawable(private val delegate: Drawable, private val scaleType: ScaleType) :
    Drawable() {

    private var underlyingWidth = 0
    private var underlyingHeight = 0

    private val matrix = Matrix()
    private var drawMatrix: Matrix? = null

    override fun draw(canvas: Canvas) {
        configureBoundsIfUnderlyingChanged()
        if (drawMatrix != null) {
            val saveCount = canvas.save()
            canvas.clipRect(bounds)
            canvas.concat(drawMatrix)
            delegate.draw(canvas)
            canvas.restoreToCount(saveCount)
        } else {
            // drawMatrix == null means our bounds match and we can take fast path
            delegate.draw(canvas)
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        configureBounds()
    }

    private fun configureBoundsIfUnderlyingChanged() {
        val underlyingChanged =
            underlyingWidth != delegate.intrinsicWidth || underlyingHeight != delegate.intrinsicHeight
        if (underlyingChanged) {
            configureBounds()
        }
    }

    private fun configureBounds() {
        val underlyingDrawable = delegate
        val bounds = bounds
        val viewWidth = bounds.width()
        val viewHeight = bounds.height()
        val underlyingWidth: Int = underlyingDrawable.intrinsicWidth.also {
            this.underlyingWidth = it
        }
        val underlyingHeight: Int = underlyingDrawable.intrinsicHeight.also {
            this.underlyingHeight = it
        }

        // If the drawable has no intrinsic size, we just fill our entire view.
        if (underlyingWidth <= 0 || underlyingHeight <= 0) {
            underlyingDrawable.bounds = bounds
            drawMatrix = null
            return
        }

        // If the drawable fits exactly, no transform needed.
        if (underlyingWidth == viewWidth && underlyingHeight == viewHeight) {
            underlyingDrawable.bounds = bounds
            drawMatrix = null
            return
        }

        // If we're told to scale to fit, we just fill our entire view.
        if (scaleType === ScaleType.FIT_XY) {
            underlyingDrawable.bounds = bounds
            drawMatrix = null
            return
        }

        // We need to do the scaling ourselves, so have the underlying drawable use its preferred size.
        underlyingDrawable.setBounds(0, 0, underlyingWidth, underlyingHeight)
        scaleType.getTransform(
            matrix,
            bounds,
            underlyingWidth,
            underlyingHeight
        )
        drawMatrix = matrix
    }

    override fun setAlpha(alpha: Int) {
        throw UnsupportedOperationException()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        throw UnsupportedOperationException()
    }

    override fun getOpacity(): Int = delegate.opacity
}