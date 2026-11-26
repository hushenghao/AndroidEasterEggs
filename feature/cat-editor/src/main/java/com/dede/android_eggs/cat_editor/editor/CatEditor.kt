@file:OptIn(ExperimentalComposeUiApi::class)

package com.dede.android_eggs.cat_editor.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.computedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import com.dede.android_eggs.cat_editor.model.CatParts
import com.dede.android_eggs.cat_editor.model.CatParts.VIEW_PORT_SIZE
import com.dede.android_eggs.cat_editor.util.CaptureControllerDelegate
import com.dede.android_eggs.cat_editor.util.CaptureControllerDelegate.Companion.rememberCaptureControllerDelegate
import com.dede.android_eggs.cat_editor.util.Utilities
import com.dede.android_eggs.cat_editor.util.Utilities.toInvert
import com.dede.android_eggs.color_picker.ColorPickerUtilities
import dev.shreyaspatil.capturable.capturable
import kotlin.math.max
import kotlin.math.min
import com.dede.android_eggs.resources.R as StringR

private const val TAG = "CatEditor"

internal const val S_MIN = 0.3f
internal const val S_MAX = 5f

internal fun range(float: Float, max: Float, min: Float): Float {
    return min(max, max(float, min))
}

internal const val S_STEP = 1.5f

private fun nextScaleLevel(scale: Float, max: Float, min: Float): Float {
    if (scale == max) {
        return min
    }
    val ns = scale * S_STEP
    if (ns > max) {
        return max
    }
    return ns
}

@Preview
@Composable
internal fun CatEditor(
    controller: CatEditorController = rememberCatEditorController(),
    captureController: CaptureControllerDelegate = rememberCaptureControllerDelegate()
) {
    val controllerImpl = controller as CatEditorControllerImpl

    var selectedPart by controllerImpl.selectedPartState

    var scale by controllerImpl.scaleState
    var offset by controllerImpl.offsetState
    val scaleAnim by animateFloatAsState(scale)
    val offsetAnim by animateOffsetAsState(offset)

    val colorsVersion by controllerImpl.colorListVersionState
    val colors = remember(colorsVersion, controllerImpl.colorList) { controllerImpl.colorList }

    LaunchedEffect(captureController) {
        captureController.onPerCapture = {
            // set normal state
            selectedPart = CatEditorController.UNSELECTED_PART
            controllerImpl.resetGraphicsLayer()
        }
    }

    Box {
        AnimatedVisibility(
            visible = controllerImpl.isGridVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            CatEditorGridPoint()
        }

        val onDoubleTab: (Offset) -> Unit = {
            scale = nextScaleLevel(scale, S_MAX, S_MIN)
        }

        var editorSize by remember { mutableStateOf(Size.Zero) }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    editorSize = it.toSize()
                }
                .pointerInput(controllerImpl.isGesturesEnabled) {
                    if (!controllerImpl.isGesturesEnabled) {
                        return@pointerInput
                    }
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = range(scale * zoom, S_MAX, S_MIN)
                        offset += pan
                    }
                }
                .graphicsLayer {
                    scaleX = scaleAnim
                    scaleY = scaleAnim
                    translationX = offsetAnim.x
                    translationY = offsetAnim.y
                }
                .pointerInput(controllerImpl.isGesturesEnabled) {
                    if (!controllerImpl.isGesturesEnabled) {
                        return@pointerInput
                    }
                    detectTapGestures(
                        onDoubleTap = onDoubleTab
                    ) {
                        controllerImpl.selectPart = CatEditorController.UNSELECTED_PART
                    }
                }
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "CatEditor_SelectedPart")
            val hasSelected by remember {
                computedStateOf { selectedPart != CatEditorController.UNSELECTED_PART }
            }
            val blendRatio by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = if (hasSelected) 0.5f else 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "HighlightColorBlend"
            )

            val partColorBlend: (index: Int) -> Color = { index ->
                var color = colors[index]
                if (selectedPart == index) {
                    val blend = ColorPickerUtilities.getHighlightColor(color)
                    color = ColorPickerUtilities.blendColor(color, blend, blendRatio)
                }
                color
            }

            val isDarkTheme = isSystemInDarkTheme()
            val shadowColor = remember(isDarkTheme) {
                if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.18f)
            }

            var canvasSize by remember { mutableStateOf(Size.Zero) }
            val canvasMatrix = remember(canvasSize) { createCanvasMatrix(canvasSize) }

            LaunchedEffect(canvasSize, editorSize) {
                if (canvasSize != Size.Zero && editorSize != Size.Zero) {
                    controllerImpl.defaultGraphicsLayerScale =
                        editorSize.minDimension / canvasSize.maxDimension
                }
            }

            val catPaths = remember(canvasSize, canvasMatrix) {
                if (canvasSize == Size.Zero) emptyList() else createTransformedCatPaths(canvasMatrix)
            }
            val pointMatrix = remember(canvasSize, canvasMatrix) {
                if (canvasSize == Size.Zero) null else canvasMatrix.toInvert()
            }
            val rotationYAnim by animateFloatAsState(if (controllerImpl.isMirrorMode) 180f else 0f)

            val canvasModifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .graphicsLayer {
                    // for mirror mode
                    rotationY = rotationYAnim
                }

            val catShadow = rememberCatShadow(canvasSize, canvasMatrix, shadowColor)
            Canvas(
                modifier = canvasModifier,
                onDraw = {
                    if (catShadow != null) {
                        val (shadowBitmap, padding) = catShadow
                        drawImage(
                            image = shadowBitmap,
                            topLeft = Offset(-padding.toFloat(), -padding.toFloat()),
                        )
                    }
                }
            )

            Canvas(
                contentDescription = stringResource(StringR.string.cat_editor),
                modifier = Modifier
                    .then(canvasModifier)
                    .capturable(captureController.getDelegate())
                    .onSizeChanged { canvasSize = it.toSize() }
                    .pointerInput(controllerImpl.isSelectEnabled) {
                        if (!controllerImpl.isSelectEnabled) {
                            return@pointerInput
                        }
                        detectTapGestures(
                            onDoubleTap = onDoubleTab,
                        ) {
                            val invertMatrix = pointMatrix ?: return@detectTapGestures
                            var handler = false
                            for (i in CatParts.drawOrders.size - 1 downTo 0) {
                                val pathDraw = CatParts.drawOrders[i]
                                if (!pathDraw.touchable) {
                                    continue
                                }

                                if (Utilities.isPointInRegion(it, invertMatrix, pathDraw.regin)) {
                                    handler = selectedPart != i
                                    selectedPart = i
                                    break
                                }
                            }
                            if (!handler) {
                                selectedPart = CatEditorController.UNSELECTED_PART
                            }
                        }
                    },
                onDraw = {
                    val catScale = size.minDimension / VIEW_PORT_SIZE
                    composeCanvasDraw(catPaths, catScale, partColorBlend)
                }
            )
        }
    }
}

internal fun createCanvasMatrix(size: Size): Matrix {
    val matrix = Matrix()
    val minDimension = size.minDimension
    matrix.translate((size.width - minDimension) / 2f, (size.height - minDimension) / 2f)
    matrix.scale(minDimension / VIEW_PORT_SIZE, minDimension / VIEW_PORT_SIZE)
    return matrix
}
