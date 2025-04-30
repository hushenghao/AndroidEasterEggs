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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A grid line for the cat editor.
 */
@Preview
@Composable
internal fun CatEditorGridLine(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
    gridLineCount: Int = 28,
    strokeWidth: Dp = 1.dp,
) {

    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        onDraw = {
            clipRect {
                val step = size.maxDimension / gridLineCount

                val columns = (size.width / step).toInt() + 1
                val offsetX = (size.width - columns * step) / 2f
                for (c in 0 until columns) {
                    val x = offsetX + c * step
                    drawLine(
                        color,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = if (c % 5 == 0) strokeWidthPx * 1.5f else strokeWidthPx,
                    )
                }

                val rows = (size.height / step).toInt() + 1
                val offsetY = (size.height - rows * step) / 2f
                for (r in 0 until rows) {
                    val y = offsetY + r * step
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
