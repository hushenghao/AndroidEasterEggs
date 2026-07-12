package com.dede.android_eggs.composable.colorpicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
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
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.getHighlightColor
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.getHsvPaletteColorByPoint
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.getHsvPalettePointByColor
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.rangeHsvPaletteColor
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.rangeHsvPalettePoint
import kotlinx.coroutines.launch

@Composable
fun ColorHsvPalette(
    modifier: Modifier = Modifier,
    defaultColor: Color = Color.White,
    onColorChanged: (hsv: Color, hue: Float, saturation: Float) -> Unit = { _, _, _ -> },
) {
    val performColorChanged by rememberUpdatedState(onColorChanged)

    var paletteColor by remember { mutableStateOf(defaultColor) }
    val palettePoint = remember {
        Animatable(Offset.Unspecified, Offset.VectorConverter)
    }
    var onDefaultColorUpdate by remember { mutableStateOf(true) }

    LaunchedEffect(defaultColor) {
        if (defaultColor != paletteColor) {
            paletteColor = rangeHsvPaletteColor(defaultColor)
            onDefaultColorUpdate = true
        }
    }

    val scope = rememberCoroutineScope()

    val touchCircleRadius = with(LocalDensity.current) { 10.dp.toPx() }
    val touchStrokeWidth = with(LocalDensity.current) { 1.5.dp.toPx() }

    fun onTouched(anim: Boolean, position: Offset, size: IntSize) {
        val pos = rangeHsvPalettePoint(position, size)
        val hsv = getHsvPaletteColorByPoint(pos, size)

        val hue = hsv[0]
        val saturation = hsv[1]
        val value = hsv[2]
        val selectedColor = Color.hsv(hue, saturation, value)
        paletteColor = selectedColor

        scope.launch {
            if (anim) {
                palettePoint.animateTo(pos)
            } else {
                palettePoint.snapTo(pos)
            }
        }

        performColorChanged(selectedColor, hue, saturation)
    }

    Canvas(
        modifier = Modifier
            .then(modifier)
            .pointerInput(Unit) {
                detectTapGestures {
                    onTouched(true, it, size)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    onTouched(false, change.position, size)
                }
            }
            .aspectRatio(1f)
    ) {
        val sweep = Brush.sweepGradient(
            colors = listOf(
                Color.Red, Color.Magenta,
                Color.Blue, Color.Cyan,
                Color.Green, Color.Yellow, Color.Red,
            ),
        )
        drawCircle(sweep)
        val radial = Brush.radialGradient(
            colors = listOf(Color.White, Color(0x00FFFFFF)),
            tileMode = TileMode.Clamp,
        )
        drawCircle(radial)

        if (onDefaultColorUpdate) {
            onDefaultColorUpdate = false

            val point = getHsvPalettePointByColor(paletteColor, size.toIntSize())
            scope.launch {
                palettePoint.animateTo(point)
            }
        }

        if (paletteColor.isSpecified && palettePoint.value.isSpecified) {
            val clipPath = Path().apply { addOval(size.toRect()) }
            clipPath(clipPath) {
                drawCircle(
                    color = paletteColor,
                    radius = touchCircleRadius,
                    center = palettePoint.value,
                    style = Fill,
                )
                drawCircle(
                    color = getHighlightColor(paletteColor),
                    radius = touchCircleRadius,
                    center = palettePoint.value,
                    style = Stroke(width = touchStrokeWidth),
                )
            }
        }
    }
}
