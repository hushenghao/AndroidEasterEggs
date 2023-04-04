/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.dede.android_eggs.ui.drawables

import android.graphics.Matrix
import android.graphics.Rect
import kotlin.math.min

/**
 * Options for scaling the child bounds to the parent bounds.
 *
 * Similar to [android.widget.ImageView.ScaleType], but ScaleType.MATRIX is not supported.
 *
 */
interface ScaleType {
    /**
     * Gets transformation matrix based on the scale type.
     *
     * @param outTransform out matrix to store result
     * @param parentBounds parent bounds
     * @param childWidth   child width
     * @param childHeight  child height
     * @return same reference to the out matrix for convenience
     */
    fun getTransform(
        outTransform: Matrix,
        parentBounds: Rect,
        childWidth: Int,
        childHeight: Int,
    ): Matrix

    companion object {
        /**
         * Scales width and height independently, so that the child matches the parent exactly. This may
         * change the aspect ratio of the child.
         */
        val FIT_XY = ScaleTypeFitXY.INSTANCE

        /**
         * Scales the child so that the child's width fits exactly. The height will be cropped if it
         * exceeds parent's bounds. Aspect ratio is preserved. Child is centered within the parent's
         * bounds.
         */
        val FIT_X = ScaleTypeFitX.INSTANCE

        /**
         * Scales the child so that the child's height fits exactly. The width will be cropped if it
         * exceeds parent's bounds. Aspect ratio is preserved. Child is centered within the parent's
         * bounds.
         */
        val FIT_Y = ScaleTypeFitY.INSTANCE

        /**
         * Scales the child so that it fits entirely inside the parent. At least one dimension (width or
         * height) will fit exactly. Aspect ratio is preserved. Child is aligned to the top-left corner
         * of the parent.
         */
        val FIT_START = ScaleTypeFitStart.INSTANCE

        /**
         * Scales the child so that it fits entirely inside the parent. At least one dimension (width or
         * height) will fit exactly. Aspect ratio is preserved. Child is centered within the parent's
         * bounds.
         */
        val FIT_CENTER = ScaleTypeFitCenter.INSTANCE

        /**
         * Scales the child so that it fits entirely inside the parent. At least one dimension (width or
         * height) will fit exactly. Aspect ratio is preserved. Child is aligned to the bottom-right
         * corner of the parent.
         */
        val FIT_END = ScaleTypeFitEnd.INSTANCE

        /**
         * Performs no scaling. Child is centered within parent's bounds.
         */
        val CENTER = ScaleTypeCenter.INSTANCE

        /**
         * Scales the child so that it fits entirely inside the parent. Unlike FIT_CENTER, if the child
         * is smaller, no up-scaling will be performed. Aspect ratio is preserved. Child is centered
         * within parent's bounds.
         */
        val CENTER_INSIDE = ScaleTypeCenterInside.INSTANCE

        /**
         * Scales the child so that both dimensions will be greater than or equal to the corresponding
         * dimension of the parent. At least one dimension (width or height) will fit exactly. Child is
         * centered within parent's bounds.
         */
        val CENTER_CROP = ScaleTypeCenterCrop.INSTANCE

        /**
         * Scales the child so that it fits entirely inside the parent. At least one dimension (width or
         * height) will fit exactly. Aspect ratio is preserved. Child is aligned to the bottom-left
         * corner of the parent.
         */
        val FIT_BOTTOM_START = ScaleTypeFitBottomStart.INSTANCE
    }
}

/**
 * A convenience base class that has some common logic.
 */
abstract class AbstractScaleType : ScaleType {
    override fun getTransform(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
    ): Matrix {
        val sX = parentRect.width().toFloat() / childWidth.toFloat()
        val sY = parentRect.height().toFloat() / childHeight.toFloat()
        getTransformImpl(outTransform, parentRect, childWidth, childHeight, sX, sY)
        return outTransform
    }

    abstract fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    )
}

private class ScaleTypeFitXY : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val dx = parentRect.left.toFloat()
        val dy = parentRect.top.toFloat()
        outTransform.setScale(scaleX, scaleY)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "fit_xy"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeFitXY()
    }
}

private class ScaleTypeFitStart : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val scale = min(scaleX, scaleY)
        val dx = parentRect.left.toFloat()
        val dy = parentRect.top.toFloat()
        outTransform.setScale(scale, scale)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "fit_start"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeFitStart()
    }
}

private class ScaleTypeFitBottomStart : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val scale = min(scaleX, scaleY)
        val dx = parentRect.left.toFloat()
        val dy = parentRect.top + (parentRect.height() - childHeight * scale)
        outTransform.setScale(scale, scale)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "fit_bottom_start"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeFitBottomStart()
    }
}

private class ScaleTypeFitCenter : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val scale = min(scaleX, scaleY)
        val dx = parentRect.left + (parentRect.width() - childWidth * scale) * 0.5f
        val dy = parentRect.top + (parentRect.height() - childHeight * scale) * 0.5f
        outTransform.setScale(scale, scale)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "fit_center"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeFitCenter()
    }
}

private class ScaleTypeFitEnd : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val scale = min(scaleX, scaleY)
        val dx = parentRect.left + (parentRect.width() - childWidth * scale)
        val dy = parentRect.top + (parentRect.height() - childHeight * scale)
        outTransform.setScale(scale, scale)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "fit_end"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeFitEnd()
    }
}

private class ScaleTypeCenter : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val dx = parentRect.left + (parentRect.width() - childWidth) * 0.5f
        val dy = parentRect.top + (parentRect.height() - childHeight) * 0.5f
        outTransform.setTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "center"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeCenter()
    }
}

private class ScaleTypeCenterInside : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val scale = min(min(scaleX, scaleY), 1.0f)
        val dx = parentRect.left + (parentRect.width() - childWidth * scale) * 0.5f
        val dy = parentRect.top + (parentRect.height() - childHeight * scale) * 0.5f
        outTransform.setScale(scale, scale)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "center_inside"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeCenterInside()
    }
}

private class ScaleTypeCenterCrop : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val scale: Float
        val dx: Float
        val dy: Float
        if (scaleY > scaleX) {
            scale = scaleY
            dx = parentRect.left + (parentRect.width() - childWidth * scale) * 0.5f
            dy = parentRect.top.toFloat()
        } else {
            scale = scaleX
            dx = parentRect.left.toFloat()
            dy = parentRect.top + (parentRect.height() - childHeight * scale) * 0.5f
        }
        outTransform.setScale(scale, scale)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "center_crop"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeCenterCrop()
    }
}

private class ScaleTypeFitX : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val scale: Float = scaleX
        val dx: Float = parentRect.left.toFloat()
        val dy: Float = parentRect.top + (parentRect.height() - childHeight * scale) * 0.5f
        outTransform.setScale(scale, scale)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "fit_x"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeFitX()
    }
}

private class ScaleTypeFitY : AbstractScaleType() {
    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        scaleX: Float,
        scaleY: Float,
    ) {
        val scale: Float = scaleY
        val dx: Float = parentRect.left + (parentRect.width() - childWidth * scale) * 0.5f
        val dy: Float = parentRect.top.toFloat()
        outTransform.setScale(scale, scale)
        outTransform.postTranslate((dx + 0.5f).toInt().toFloat(), (dy + 0.5f).toInt().toFloat())
    }

    override fun toString(): String {
        return "fit_y"
    }

    companion object {
        val INSTANCE: ScaleType = ScaleTypeFitY()
    }
}