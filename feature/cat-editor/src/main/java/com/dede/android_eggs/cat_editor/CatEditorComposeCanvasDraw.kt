package com.dede.android_eggs.cat_editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform


internal fun forEachCatDrawPart(
    onPartColor: (index: Int) -> Color,
    onDrawPart: (part: CatParts.PathDraw, color: Color) -> Unit
) {
    CatParts.drawOrders.forEachIndexed { index, pathDraw ->
        onDrawPart(pathDraw, onPartColor(index))
    }
}

internal fun DrawScope.composeCanvasDraw(
    matrix: Matrix,
    onPartColor: (index: Int) -> Color
) {
    withTransform({ transform(matrix) }) {
        forEachCatDrawPart(onPartColor) { part, color ->
            part.drawLambda(this, color)
        }
    }
}
