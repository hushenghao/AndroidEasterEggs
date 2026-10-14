package com.dede.android_eggs.composable.colorpicker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.getSvPaletteColorByPoint
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.getSvPalettePointByColor
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.rangeSvPalettePoint
import kotlinx.coroutines.launch

@Composable
fun ColorSvPalette(
    modifier: Modifier = Modifier,
    hue: Float,
    defaultColor: Color = Color.White,
    onColorChanged: (hsv: Color, saturation: Float, value: Float) -> Unit = { _, _, _ -> },
) {
    val performColorChanged by rememberUpdatedState(onColorChanged)

    var paletteColor by remember { mutableStateOf(defaultColor) }
    val palettePoint = remember {
        Animatable(Offset.Unspecified, Offset.VectorConverter)
    }
    var indicatorPressed by remember { mutableStateOf(false) }
    val indicatorScale by animateFloatAsState(
        targetValue = if (indicatorPressed) 1.5f else 1f,
        label = "indicatorScale",
    )
    var onDefaultColorUpdate by remember { mutableStateOf(true) }

    LaunchedEffect(defaultColor) {
        if (defaultColor != paletteColor) {
            paletteColor = defaultColor
            onDefaultColorUpdate = true
        }
    }

    val scope = rememberCoroutineScope()

    val touchCircleRadius = with(LocalDensity.current) { 10.dp.toPx() }
    val touchStrokeWidth = with(LocalDensity.current) { 1.5.dp.toPx() }

    fun onTouched(anim: Boolean, position: Offset, size: IntSize) {
        val pos = rangeSvPalettePoint(position, size)
        val hsv = getSvPaletteColorByPoint(pos, size, hue)

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

        performColorChanged(selectedColor, saturation, value)
    }

    Canvas(
        modifier = Modifier
            .then(modifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        indicatorPressed = true
                        onTouched(true, offset, size)
                        // A press that turns into a drag cancels the tap gesture;
                        // detectDragGestures owns the release in that case.
                        if (tryAwaitRelease()) {
                            indicatorPressed = false
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        indicatorPressed = true
                    },
                    onDragEnd = {
                        indicatorPressed = false
                    },
                    onDragCancel = {
                        indicatorPressed = false
                    },
                ) { change, _ ->
                    change.consume()
                    onTouched(false, change.position, size)
                }
            }
    ) {
        // Only the gradient is clipped to the rounded rect so the indicator
        // circle can overhang the palette edge without being cut off.
        val paletteClip = Path().apply {
            addRoundRect(RoundRect(size.toRect(), CornerRadius(8.dp.toPx())))
        }
        clipPath(paletteClip) {
            drawRect(
                Brush.horizontalGradient(
                    listOf(Color.White, Color.hsv(hue, 1f, 1f)),
                ),
            )
            drawRect(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black),
                ),
            )
        }

        if (onDefaultColorUpdate) {
            onDefaultColorUpdate = false

            val point = getSvPalettePointByColor(paletteColor, size.toIntSize())
            scope.launch {
                palettePoint.animateTo(point)
            }
        }

        if (paletteColor.isSpecified && palettePoint.value.isSpecified) {
            val radius = touchCircleRadius * indicatorScale
            drawCircle(
                color = paletteColor,
                radius = radius,
                center = palettePoint.value,
                style = Fill,
            )
            drawCircle(
                color = getHighlightColor(paletteColor),
                radius = radius,
                center = palettePoint.value,
                style = Stroke(width = touchStrokeWidth),
            )
        }
    }
}
