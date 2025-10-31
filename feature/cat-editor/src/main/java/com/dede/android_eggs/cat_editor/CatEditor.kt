@file:OptIn(ExperimentalComposeUiApi::class)

package com.dede.android_eggs.cat_editor

import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.dede.android_eggs.cat_editor.CaptureControllerDelegate.Companion.rememberCaptureControllerDelegate
import com.dede.android_eggs.cat_editor.CatParts.VIEW_PORT_SIZE
import com.dede.android_eggs.cat_editor.Utilities.asAndroidMatrix
import com.dede.android_eggs.cat_editor.Utilities.toInvert
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
    val ns = scale * S_STEP
    if (ns > max) {
        return min
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
            selectedPart = -1
            controllerImpl.resetGraphicsLayer()
        }
    }

    Box {
        AnimatedVisibility(
            visible = controllerImpl.isGridVisible,
            enter = fadeIn(),
            exit = fadeOut()
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
                        controllerImpl.selectPart = -1
                    }
                }
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "CatEditor_SelectedPart")
            val blendRatio by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = if (selectedPart != -1) 0.5f else 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "HighlightColorBlend"
            )

            val partColorBlend: (index: Int) -> Color = { index ->
                var color = colors[index]
                if (selectedPart == index) {
                    val blend = Utilities.getHighlightColor(color)
                    color = Utilities.blendColor(color, blend, blendRatio)
                }
                color
            }

            var canvasSize by remember { mutableStateOf(Size.Zero) }
            val canvasMatrix = remember(canvasSize) { createCanvasMatrix(canvasSize) }

            LaunchedEffect(canvasSize, editorSize) {
                if (canvasSize != Size.Zero && editorSize != Size.Zero) {
                    controllerImpl.defaultGraphicsLayerScale =
                        editorSize.minDimension / canvasSize.maxDimension
                }
            }

            val bitmap: Bitmap? = remember(canvasSize) {
                if (useAndroidCanvasDraw) createAndroidBitmap(canvasSize) else null
            }
            val rotationYAnim by animateFloatAsState(if (controllerImpl.isMirrorMode) 180f else 0f)
            Canvas(
                contentDescription = stringResource(StringR.string.cat_editor),
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .capturable(captureController.getDelegate())
                    .graphicsLayer {
                        // for mirror mode
                        rotationY = rotationYAnim
                    }
                    .pointerInput(controllerImpl.isSelectEnabled) {
                        if (!controllerImpl.isSelectEnabled) {
                            return@pointerInput
                        }
                        detectTapGestures(
                            onDoubleTap = onDoubleTab,
                        ) {
                            var handler = false
                            val pointMatrix = canvasMatrix.toInvert()
                            for (i in CatParts.drawOrders.size - 1 downTo 0) {
                                val pathDraw = CatParts.drawOrders[i]
                                if (!pathDraw.touchable) {
                                    continue
                                }

                                if (Utilities.isPointInRegion(it, pointMatrix, pathDraw.regin)) {
                                    handler = selectedPart != i
                                    selectedPart = i
                                    break
                                }
                            }
                            if (!handler) {
                                selectedPart = -1
                            }
                        }
                    },
                onDraw = {
                    if (size != canvasSize) {
                        // canvas size changed
                        canvasSize = size
                        return@Canvas
                    }

                    if (useAndroidCanvasDraw && bitmap != null) {
                        // android canvas draw
                        androidCanvasDraw(
                            canvasMatrix.asAndroidMatrix(),
                            bitmap,
                            partColorBlend
                        )
                    } else {
                        // compose canvas draw
                        composeCanvasDraw(
                            canvasMatrix,
                            partColorBlend
                        )
                    }
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
