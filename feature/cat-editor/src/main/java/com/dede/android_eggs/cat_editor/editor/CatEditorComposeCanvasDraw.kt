package com.dede.android_eggs.cat_editor.editor

import android.graphics.BlurMaskFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativePaint
import com.dede.android_eggs.cat_editor.model.CatParts
import kotlin.math.ceil


internal fun createTransformedCatPaths(matrix: Matrix): List<Path> {
    return CatParts.drawOrders.map { part ->
        Path().apply {
            addPath(part.path)
            transform(matrix)
        }
    }
}

internal fun DrawScope.composeCanvasDraw(
    transformedPaths: List<Path>,
    scale: Float,
    onPartColor: (index: Int) -> Color,
) {
    if (transformedPaths.isEmpty() || scale <= 0f) return
    CatParts.drawOrders.forEachIndexed { index, part ->
        with(part) {
            draw(
                color = onPartColor(index),
                path = transformedPaths[index],
                scale = scale,
            )
        }
    }
}

@Composable
internal fun rememberCatShadow(
    canvasSize: Size,
    canvasMatrix: Matrix,
    shadowColor: Color,
    shadowBlurRadius: Float = CatParts.SHADOW_BLUR_RADIUS,
): Pair<ImageBitmap, Int>? {
    return remember(canvasSize, canvasMatrix, shadowColor, shadowBlurRadius) {
        if (canvasSize == Size.Zero || shadowColor == Color.Transparent || shadowBlurRadius <= 0f) {
            return@remember null
        }

        val scale = canvasSize.minDimension / CatParts.VIEW_PORT_SIZE
        val pixelBlur = shadowBlurRadius * scale
        if (pixelBlur <= 0f) return@remember null

        val padding = ceil(pixelBlur).toInt()
        val width = canvasSize.width.toInt() + padding * 2
        val height = canvasSize.height.toInt() + padding * 2
        if (width <= 0 || height <= 0) return@remember null

        val imageBitmap = ImageBitmap(width, height)
        val canvas = Canvas(imageBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            color = shadowColor
            style = PaintingStyle.Fill
            nativePaint.maskFilter = BlurMaskFilter(pixelBlur, BlurMaskFilter.Blur.NORMAL)
        }
        val transformedPath = Path().apply {
            addPath(CatParts.shadowPath)
            transform(canvasMatrix)
        }
        canvas.translate(padding.toFloat(), padding.toFloat())
        canvas.drawPath(transformedPath, paint)
        imageBitmap to padding
    }
}
