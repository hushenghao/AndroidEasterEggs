package com.dede.android_eggs.cat_editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize


/**
 * A color palette that allows users to select a color using HSV (Hue, Saturation, Value) model.
 */
@Composable
fun ColorHsvPalette(
    modifier: Modifier = Modifier,
    defaultColor: Color = Color.Red,
    onColorChanged: (hvs: Color, hue: Float, saturation: Float) -> Unit = { _, _, _ -> }
) {
    val performColorChanged by rememberUpdatedState(onColorChanged)

    var paletteColor by remember { mutableStateOf(defaultColor) }
    var touchPoint by remember { mutableStateOf(Offset.Unspecified) }

    LaunchedEffect(defaultColor) {
        paletteColor = Utilities.rangeHsvPaletteColor(defaultColor)
        touchPoint = Offset.Unspecified
    }

    val touchCircleRadius = with(LocalDensity.current) { 8.dp.toPx() }
    val touchStrokeWidth = with(LocalDensity.current) { 1.5.dp.toPx() }

    fun onTouched(position: Offset, size: IntSize) {
        val pos = Utilities.rangeHsvPalettePoint(position, size)
        val hsv = Utilities.getHsvPaletteColorByPoint(pos, size)

        val hue = hsv[0]
        val saturation = hsv[1]
        val value = hsv[2]
        val selectedColor = Color.hsv(hue, saturation, value)
        paletteColor = selectedColor
        touchPoint = pos

        performColorChanged(selectedColor, hue, saturation)
    }

    Canvas(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    onTouched(change.position, size)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    onTouched(it, size)
                }
            }
            .then(modifier)
            .aspectRatio(1f)
    ) {
        val sweep = Brush.sweepGradient(
            colors = listOf(
                Color.Red, Color.Magenta,
                Color.Blue, Color.Cyan,
                Color.Green, Color.Yellow, Color.Red
            )
        )
        drawCircle(sweep)
        val radial = Brush.radialGradient(
            colors = listOf(Color.White, Color(0x00FFFFFF)),
            tileMode = TileMode.Clamp
        )
        drawCircle(radial)

        if (!touchPoint.isValid()) {
            touchPoint = Utilities.getHsvPalettePointByColor(paletteColor, size.toIntSize())
        }

        if (paletteColor.isSpecified && touchPoint.isValid()) {
            val clipPath = Path().apply { addOval(size.toRect()) }
            clipPath(clipPath) {
                drawCircle(
                    color = paletteColor,
                    radius = touchCircleRadius,
                    center = touchPoint,
                    style = Fill
                )
                drawCircle(
                    color = Color.Black,
                    radius = touchCircleRadius,
                    center = touchPoint,
                    style = Stroke(
                        width = touchStrokeWidth,
                    )
                )
                drawCircle(
                    color = Color.White,
                    radius = touchCircleRadius - touchStrokeWidth,
                    center = touchPoint,
                    style = Stroke(
                        width = touchStrokeWidth,
                    )
                )
            }
        }
    }
}
