package com.dede.android_eggs.cat_editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
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
                val grid = size.calculateGrid(step)

                translate(left = grid.offset.x, top = grid.offset.y) {
                    for (c in 0 until grid.columns) {
                        val x = c * step
                        drawLine(
                            color,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = if (c % 5 == 0) strokeWidthPx * 1.5f else strokeWidthPx,
                        )
                    }
                    for (r in 0 until grid.rows) {
                        val y = r * step
                        drawLine(
                            color,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = if (r % 5 == 0) strokeWidthPx * 1.5f else strokeWidthPx,
                        )
                    }
                }
            }
        })
}

@Preview
@Composable
internal fun CatEditorGridPoint(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
    gridCount: Int = 28,
    radius: Dp = 1.25.dp,
) {

    val radiusPx = with(LocalDensity.current) { radius.toPx() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        onDraw = {
            clipRect {
                val step = size.maxDimension / gridCount
                val grid = size.calculateGrid(step)

                translate(left = grid.offset.x, top = grid.offset.y) {
                    for (c in 0 until grid.columns) {
                        val x = c * step
                        for (r in 0 until grid.rows) {
                            val y = r * step
                            drawCircle(
                                color = color,
                                radius = radiusPx,
                                center = Offset(x, y)
                            )
                        }
                    }
                }
            }
        })
}

private data class GridObj(
    val offset: Offset,
    val columns: Int,
    val rows: Int,
)

private fun Size.calculateGrid(step: Float): GridObj {

    fun remainder(a: Float, b: Float) = if (a % b > 0) 1 else 0

    val columns = (width / step).toInt() + remainder(width, step)
    val offsetX = (width - columns * step) / 2f + step / 2f

    val rows = (height / step).toInt() + remainder(height, step)
    val offsetY = (height - rows * step) / 2f + step / 2f

    return GridObj(Offset(offsetX, offsetY), columns, rows)
}
