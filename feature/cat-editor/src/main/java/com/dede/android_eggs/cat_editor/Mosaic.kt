package com.dede.android_eggs.cat_editor

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Mosaic(
    modifier: Modifier = Modifier,
    step: Dp = 8.dp,
    lightColor: Color = Color.White,
    grayColor: Color = Color.LightGray
) {
    val stepSize = with(LocalDensity.current) { step.toPx() }
    Canvas(
        modifier = Modifier.then(modifier)
    ) {
        val columns = (size.width / stepSize + 1).toInt()
        val rows = (size.height / stepSize + 1).toInt()
        for (r in 0..<rows) {
            for (c in 0..<columns) {
                drawRect(
                    color = if (r % 2 == c % 2) lightColor else grayColor,
                    topLeft = Offset(c * stepSize, r * stepSize),
                    size = Size(stepSize, stepSize)
                )
            }
        }
    }
}
