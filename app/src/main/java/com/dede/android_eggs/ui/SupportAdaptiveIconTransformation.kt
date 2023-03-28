package com.dede.android_eggs.ui

import android.graphics.*
import androidx.core.graphics.PathParser
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import coil.decode.DecodeUtils
import coil.size.*
import coil.transform.Transformation
import kotlin.math.roundToInt

class SupportAdaptiveIconTransformation(private val maskPathStr: String) : Transformation {

    companion object {
        private const val MASK_SIZE = 100f

        fun getShapeMaskPath(pathStr: String?, width: Int, height: Int): Path {
            if (pathStr.isNullOrBlank()) return Path()

            val path = PathParser.createPathFromPathData(pathStr)
            val matrix = Matrix()
            matrix.setScale(width / MASK_SIZE, height / MASK_SIZE)
            path.transform(matrix)
            return path
        }
    }

    override val cacheKey: String = "${javaClass.name}-$maskPathStr"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val (outputWidth, outputHeight) = calculateOutputSize(input, size)

        val safeConfig = input.config ?: Bitmap.Config.ARGB_8888
        val output = createBitmap(outputWidth, outputHeight, safeConfig)
        return output.applyCanvas {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            paint.shader = getBitmapShader(input, outputWidth, outputHeight)
            val path = getShapeMaskPath(maskPathStr, outputWidth, outputHeight)
            drawPath(path, paint)

            setBitmap(null)
        }
    }

    private fun getBitmapShader(
        input: Bitmap,
        outputWidth: Int,
        outputHeight: Int,
    ): BitmapShader {
        val matrix = Matrix()
        val multiplier = DecodeUtils.computeSizeMultiplier(
            srcWidth = input.width,
            srcHeight = input.height,
            dstWidth = outputWidth,
            dstHeight = outputHeight,
            scale = Scale.FILL
        ).toFloat()
        val dx = (outputWidth - multiplier * input.width) / 2
        val dy = (outputHeight - multiplier * input.height) / 2
        matrix.setTranslate(dx, dy)
        matrix.preScale(multiplier, multiplier)

        val shader = BitmapShader(input, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(matrix)
        return shader
    }

    private fun calculateOutputSize(input: Bitmap, size: Size): Pair<Int, Int> {
        if (size.isOriginal) {
            return input.width to input.height
        }

        val (dstWidth, dstHeight) = size
        if (dstWidth is Dimension.Pixels && dstHeight is Dimension.Pixels) {
            return dstWidth.px to dstHeight.px
        }

        val multiplier = DecodeUtils.computeSizeMultiplier(
            srcWidth = input.width,
            srcHeight = input.height,
            dstWidth = size.width.pxOrElse { Int.MIN_VALUE },
            dstHeight = size.height.pxOrElse { Int.MIN_VALUE },
            scale = Scale.FILL
        )
        val outputWidth = (multiplier * input.width).roundToInt()
        val outputHeight = (multiplier * input.height).roundToInt()
        return outputWidth to outputHeight
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is SupportAdaptiveIconTransformation && this.maskPathStr == other.maskPathStr
    }

    override fun hashCode(): Int {
        return this.maskPathStr.hashCode()
    }
}