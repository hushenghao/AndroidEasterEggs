package com.dede.android_eggs.color_picker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.color_picker.ColorPickerUtilities.getHighlightColor
import com.dede.android_eggs.color_picker.ColorPickerUtilities.getSvPaletteColorByPoint
import com.dede.android_eggs.color_picker.ColorPickerUtilities.getSvPalettePointByColor
import com.dede.android_eggs.color_picker.ColorPickerUtilities.rangeSvPalettePoint
import kotlinx.coroutines.launch

@Composable
fun ColorSvPalette(
    modifier: Modifier = Modifier,
    hue: Float,
    defaultColor: Color = Color.White,
    onColorChanged: (saturation: Float, value: Float) -> Unit = { _, _ -> },
) {
    val performColorChanged by rememberUpdatedState(onColorChanged)
    // Pointer input handlers keep the captures from when their coroutine first
    // launched; without this a hue change would never reach them.
    val currentHue by rememberUpdatedState(hue)

    var paletteColor by remember { mutableStateOf(defaultColor) }
    val palettePoint = remember {
        Animatable(Offset.Unspecified, Offset.VectorConverter)
    }
    var indicatorPressed by remember { mutableStateOf(false) }
    val indicatorScale by animateFloatAsState(
        targetValue = if (indicatorPressed) 1.5f else 1f,
        label = "indicatorScale",
    )
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val scope = rememberCoroutineScope()

    // Animating from Offset.Unspecified is not defined; snap until the point
    // has a real position.
    suspend fun movePoint(target: Offset, animate: Boolean) {
        if (animate && palettePoint.value.isSpecified) {
            palettePoint.animateTo(target)
        } else {
            palettePoint.snapTo(target)
        }
    }

    LaunchedEffect(defaultColor) {
        if (defaultColor != paletteColor) {
            paletteColor = defaultColor
            if (canvasSize != IntSize.Zero) {
                movePoint(getSvPalettePointByColor(paletteColor, canvasSize), animate = true)
            }
        }
    }

    // The point is stored in absolute pixels, so a size change has to
    // re-derive it from the current color.
    LaunchedEffect(canvasSize) {
        if (canvasSize != IntSize.Zero && paletteColor.isSpecified) {
            movePoint(getSvPalettePointByColor(paletteColor, canvasSize), animate = false)
        }
    }

    val touchCircleRadius = with(LocalDensity.current) { 10.dp.toPx() }
    val touchStrokeWidth = with(LocalDensity.current) { 1.dp.toPx() }

    fun onTouched(anim: Boolean, position: Offset, size: IntSize) {
        val pos = rangeSvPalettePoint(position, size)
        val hsv = getSvPaletteColorByPoint(pos, size, currentHue)
        paletteColor = Color.hsv(hsv[0], hsv[1], hsv[2])

        scope.launch {
            movePoint(pos, anim)
        }

        performColorChanged(hsv[1], hsv[2])
    }

    Spacer(
        modifier = modifier
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
            .onSizeChanged { canvasSize = it }
            .drawWithCache {
                val paletteClip = Path().apply {
                    addRoundRect(RoundRect(size.toRect(), CornerRadius(6.dp.toPx())))
                }
                onDrawBehind {
                    // Only the gradient is clipped to the rounded rect so the indicator
                    // circle can overhang the palette edge without being cut off.
                    // currentHue is a state read so a hue change only redraws and does
                    // not rebuild the cached clip path.
                    clipPath(paletteClip) {
                        drawRect(
                            Brush.horizontalGradient(
                                listOf(Color.White, Color.hsv(currentHue, 1f, 1f)),
                            ),
                        )
                        drawRect(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black),
                            ),
                        )
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
            },
    )
}
