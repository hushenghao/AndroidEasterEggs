@file:OptIn(ExperimentalComposeUiApi::class)

package com.dede.android_eggs.cat_editor

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.cat_editor.CaptureControllerDelegate.Companion.rememberCaptureControllerDelegate
import com.dede.android_eggs.cat_editor.CatParts.VIEW_PORT_SIZE
import com.dede.android_eggs.cat_editor.Utilities.toInvert
import dev.shreyaspatil.capturable.capturable
import kotlin.math.max
import kotlin.math.min
import com.dede.android_eggs.resources.R as StringR

private const val TAG = "CatEditor"

private const val S_MIN = 0.5f
private const val S_MAX = 3f

private fun range(float: Float, max: Float, min: Float): Float {
    return min(max, max(float, min))
}

private const val S_STEP = 1.5f

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

    LaunchedEffect(captureController) {
        captureController.onPerCapture = {
            // set normal state
            selectedPart = -1
            scale = 1f
            offset = Offset.Zero
        }
    }

    Box {
        AnimatedVisibility(
            visible = controllerImpl.isGridVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CatEditorGridLine()
        }

        val onDoubleTab: (Offset) -> Unit = {
            scale = nextScaleLevel(scale, S_MAX, S_MIN)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(controllerImpl.isGesturesEnabled) {
                    if (!controllerImpl.isGesturesEnabled) {
                        return@pointerInput
                    }
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = range(scale * zoom, S_MAX, S_MIN)
                        offset += (pan * scale)
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
                targetValue = 0.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "HighlightColorBlend"
            )

            val canvasMatrix = remember { Matrix() }
            Canvas(
                contentDescription = stringResource(StringR.string.cat_editor),
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .capturable(captureController.getDelegate())
                    .onSizeChanged {
                        val size = min(it.width, it.height)
                        canvasMatrix.reset()
                        canvasMatrix.translate((it.width - size) / 2f, (it.height - size) / 2f)
                        canvasMatrix.scale(size / VIEW_PORT_SIZE, size / VIEW_PORT_SIZE)
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
                    withTransform({ transform(canvasMatrix) }) {
                        CatParts.drawOrders.forEachIndexed { index, pathDraw ->
                            var color = controllerImpl.colorList[index]
                            if (selectedPart == index) {
                                val blend = Utilities.getHighlightColor(color)
                                color = Utilities.blendColor(color, blend, blendRatio)
                            }
                            pathDraw.drawLambda.invoke(this, color)
                        }
                    }
                }
            )
        }
    }
}
