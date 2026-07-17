package com.dede.android_eggs.cat_editor

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withMatrix
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.Matrix as ComposeMatrix


internal fun createAndroidBitmap(size: Size): Bitmap {
    var width = size.width.toInt()
    var height = size.height.toInt()
    if (width <= 0) {
        width = 1
    }
    if (height <= 0) {
        height = 1
    }
    return createBitmap(width, height)
}

// fix Android N canvas scale, Android O ???
internal val useAndroidCanvasDraw = Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1

private val androidPaint = Paint(Paint.ANTI_ALIAS_FLAG)

// https://issuetracker.google.com/issues/37138664
internal fun DrawScope.androidCanvasDraw(
    matrix: Matrix,
    bitmap: Bitmap,
    onPartColor: (index: Int) -> ComposeColor,
) {
    // clear bitmap
    bitmap.eraseColor(Color.TRANSPARENT)

    // draw bitmap
    bitmap.applyCanvas {
        withMatrix(matrix) {
            forEachCatDrawPart(onPartColor) { part, color ->
                part.androidDrawLambda(this, color, androidPaint)
            }
        }
    }

    // draw native canvas
    drawIntoCanvas { c ->
        c.nativeCanvas.drawBitmap(bitmap, 0f, 0f, null)
    }
}


internal fun DrawScope.drawShadowLayer(
    canvasMatrix: ComposeMatrix,
    shadowColor: ComposeColor,
    shadowBlurRadius: Float = CatParts.SHADOW_BLUR_RADIUS,
) {
    if (shadowColor == ComposeColor.Transparent || shadowBlurRadius <= 0f) return

    val scale = this.size.minDimension / CatParts.VIEW_PORT_SIZE
    val pixelBlur = shadowBlurRadius * scale

    val onPartColor: (index: Int) -> ComposeColor = { shadowColor }

    withTransform({ transform(canvasMatrix) }) {
        drawIntoCanvas { canvas ->
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = shadowColor.toArgb()
                style = Paint.Style.FILL
                maskFilter = BlurMaskFilter(pixelBlur, BlurMaskFilter.Blur.NORMAL)
            }
            forEachCatDrawPart(onPartColor) { part, color ->
                part.androidDrawLambda(canvas.nativeCanvas, color, paint)
            }
        }
    }
}
