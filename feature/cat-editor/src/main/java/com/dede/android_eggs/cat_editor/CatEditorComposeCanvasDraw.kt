package com.dede.android_eggs.cat_editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform


internal fun forEachCatDrawPart(
    colorList: List<Color>,
    selectedPart: Int,
    blendRatio: Float,
    block: (part: CatParts.PathDraw, color: Color) -> Unit
) {
    CatParts.drawOrders.forEachIndexed { index, pathDraw ->
        var color = colorList[index]
        if (selectedPart == index) {
            val blend = Utilities.getHighlightColor(color)
            color = Utilities.blendColor(color, blend, blendRatio)
        }
        block(pathDraw, color)
    }
}

internal fun DrawScope.composeCanvasDraw(
    matrix: Matrix,
    colorList: List<Color>,
    selectedPart: Int,
    blendRatio: Float
) {
    withTransform({ transform(matrix) }) {
        forEachCatDrawPart(colorList, selectedPart, blendRatio) { part, color ->
            part.drawLambda(this, color)
        }
    }
}
