package com.dede.android_eggs.cat_editor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withMatrix


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

// fix Android N canvas scale
internal val useAndroidCanvasDraw = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1

private val androidPaint = Paint(Paint.ANTI_ALIAS_FLAG)

// https://issuetracker.google.com/issues/37138664
internal fun DrawScope.androidCanvasDraw(
    matrix: Matrix,
    bitmap: Bitmap,
    colorList: List<androidx.compose.ui.graphics.Color>,
    selectedPart: Int,
    blendRatio: Float
) {
    // clear bitmap
    bitmap.eraseColor(Color.TRANSPARENT)

    // draw bitmap
    bitmap.applyCanvas {
        withMatrix(matrix) {
            forEachCatDrawPart(colorList, selectedPart, blendRatio) { part, color ->
                part.androidDrawLambda(this, color, androidPaint)
            }
        }
    }

    // draw native canvas
    drawIntoCanvas { c ->
        c.nativeCanvas.drawBitmap(bitmap, 0f, 0f, null)
    }
}
