package com.dede.android_eggs.ui.drawables

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.core.graphics.withSave
import com.dede.android_eggs.util.PathInflater
import com.dede.android_eggs.views.settings.compose.IconShapePrefUtil
import com.dede.basic.requireDrawable
import kotlin.math.max
import kotlin.math.roundToInt


class AlterableAdaptiveIconDrawable(
    private val context: Context,
    @DrawableRes res: Int,
    maskPathStr: String? = null,
) : Drawable() {

    companion object {
        private const val MASK_SIZE = 100f

        private const val EXTRA_INSET_PERCENTAGE = 1 / 4f
        private const val DEFAULT_VIEW_PORT_SCALE = 1f / (1 + 2 * EXTRA_INSET_PERCENTAGE)

        private const val BACKGROUND_ID = 0
        private const val FOREGROUND_ID = 1
    }

    private val tempRect = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val maskMatrix = Matrix()

    private val childDrawables: Array<ChildDrawable>
    private val mask: Path = Path()
    private val savedMask: Path = Path()

    private val layerCanvas = Canvas()
    private var layerBitmap: Bitmap? = null
    private var layerShader: BitmapShader? = null

    val isAdaptiveIconDrawable: Boolean
    private val foregroundMatrix = Matrix()

    init {
        var pathStr = maskPathStr
        if (pathStr == null || TextUtils.isEmpty(pathStr)) {
            pathStr = IconShapePrefUtil.getSystemMaskPath(context)
        }
        savedMask.set(PathInflater.inflate(pathStr))
        mask.set(savedMask)

        val drawable = context.requireDrawable(res)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable) {
            childDrawables = arrayOf(
                ChildDrawable(drawable.background, BACKGROUND_ID),
                ChildDrawable(drawable.foreground, FOREGROUND_ID)
            )
            isAdaptiveIconDrawable = true
        } else {
            childDrawables = arrayOf(ChildDrawable(drawable, BACKGROUND_ID))
            isAdaptiveIconDrawable = false
        }
    }

    fun setMaskPath(pathStr: String) {
        var path = pathStr
        if (TextUtils.isEmpty(path)) {
            path = IconShapePrefUtil.getSystemMaskPath(context)
        }
        savedMask.set(PathInflater.inflate(path))
        updateMaskBoundsInternal(bounds)
        invalidateSelf()
    }

    fun setForegroundMatrix(matrix: Matrix) {
        if (!isAdaptiveIconDrawable) return
        foregroundMatrix.set(matrix)
        invalidateSelf()
    }

    override fun invalidateSelf() {
        layerShader = null
        super.invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        val layerBitmap: Bitmap = this.layerBitmap ?: return

        if (layerShader == null) {
            layerCanvas.setBitmap(layerBitmap)
            layerCanvas.drawColor(Color.BLACK)
            for (childDrawable in childDrawables) {
                if (childDrawable.isForeground()) {
                    layerCanvas.withSave {
                        layerCanvas.setMatrix(foregroundMatrix)
                        childDrawable.draw(layerCanvas)
                    }
                } else {
                    childDrawable.draw(layerCanvas)
                }
            }

            layerShader = BitmapShader(layerBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = layerShader
        }

        if (!mask.isEmpty) {
            val rect = bounds
            canvas.translate(rect.left.toFloat(), rect.top.toFloat())
            canvas.drawPath(mask, paint)
            canvas.translate(-rect.left.toFloat(), -rect.top.toFloat())
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        if (bounds.isEmpty) return

        updateMaskBoundsInternal(bounds)
        updateLayerBoundsInternal(bounds)

        invalidateSelf()
    }

    override fun getIntrinsicHeight(): Int {
        var max = 0
        for (child in childDrawables) {
            max = max(max, child.getHeight())
        }
        return (max * DEFAULT_VIEW_PORT_SCALE).roundToInt()
    }

    override fun getIntrinsicWidth(): Int {
        var max = 0
        for (child in childDrawables) {
            max = max(max, child.getWidth())
        }
        return (max * DEFAULT_VIEW_PORT_SCALE).roundToInt()
    }

    private fun updateLayerBoundsInternal(bounds: Rect) {
        val outRect = tempRect
        if (isAdaptiveIconDrawable) {
            val cX: Int = bounds.width() / 2
            val cY: Int = bounds.height() / 2
            val insetWidth: Int = (bounds.width() / (DEFAULT_VIEW_PORT_SCALE * 2)).toInt()
            val insetHeight: Int = (bounds.height() / (DEFAULT_VIEW_PORT_SCALE * 2)).toInt()
            outRect.set(cX - insetWidth, cY - insetHeight, cX + insetWidth, cY + insetHeight)
        } else {
            outRect.set(bounds)
        }

        for (drawable in childDrawables) {
            drawable.setBounds(outRect)
        }
    }

    private fun updateMaskBoundsInternal(bounds: Rect) {
        maskMatrix.reset()
        maskMatrix.setScale(bounds.width() / MASK_SIZE, bounds.height() / MASK_SIZE)
        savedMask.transform(maskMatrix, mask)

//        maskMatrix.postTranslate(bounds.left.toFloat(), bounds.top.toFloat())
//        mask.transform(maskMatrix)

        val bitmap = layerBitmap
        if (bitmap == null || bounds.width() != bitmap.width || bounds.height() != bitmap.height) {
            layerBitmap = Bitmap.createBitmap(
                bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888
            )
        }
        paint.shader = null
        layerShader = null
    }

    private class ChildDrawable(val drawable: Drawable, val id: Int) {

        fun isBackground(): Boolean = id == BACKGROUND_ID
        fun isForeground(): Boolean = id == FOREGROUND_ID

        fun setBounds(bounds: Rect) {
            drawable.bounds = bounds
        }

        fun draw(canvas: Canvas) {
            drawable.draw(canvas)
        }

        fun getWidth(): Int = drawable.intrinsicWidth
        fun getHeight(): Int = drawable.intrinsicHeight
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun applyTheme(t: Resources.Theme) {
        for (drawable in childDrawables) {
            val d = drawable.drawable
            if (d.canApplyTheme()) {
                d.applyTheme(t)
            }
        }
    }
}