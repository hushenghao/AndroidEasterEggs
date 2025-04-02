package com.dede.android_eggs.cat_editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * A grid line for the cat editor.
 */
@Composable
internal fun CatEditorGridLine(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
    step: Dp = 20.dp,
    strokeWidth: Dp = 1.dp,
) {

    val stepPx = with(LocalDensity.current) { step.toPx() }
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        onDraw = {
            clipRect {
                val columns = (size.width / stepPx).roundToInt()
                val offsetX = (size.width - columns * stepPx) / 2f
                for (c in 0 until columns) {
                    val x = offsetX + c * stepPx
                    drawLine(
                        color,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = if (c % 5 == 0) strokeWidthPx * 1.5f else strokeWidthPx,
                    )
                }

                val rows = (size.height / stepPx).roundToInt()
                val offsetY = (size.height - rows * stepPx) / 2f
                for (r in 0 until rows) {
                    val y = offsetY + r * stepPx
                    drawLine(
                        color,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = if (r % 5 == 0) strokeWidthPx * 1.5f else strokeWidthPx,
                    )
                }
            }
        })
}
