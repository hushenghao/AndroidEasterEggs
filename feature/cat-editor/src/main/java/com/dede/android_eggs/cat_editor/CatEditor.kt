@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeApi::class,
    ExperimentalComposeUiApi::class
)

package com.dede.android_eggs.cat_editor

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.dede.android_eggs.cat_editor.CaptureControllerDelegate.Companion.rememberCaptureControllerDelegate
import com.dede.android_eggs.cat_editor.CatParts.VIEW_PORT_SIZE
import com.dede.android_eggs.cat_editor.Utilities.toInvert
import com.dede.android_eggs.views.theme.blend
import dev.shreyaspatil.capturable.capturable
import kotlin.math.min

private const val TAG = "CatEditor"

@Composable
internal fun CatEditor(
    modifier: Modifier = Modifier,
    colors: SnapshotStateList<Int> = remember { mutableStateListOf(*CatPartColors.colors()) },
    selectedPartIndexState: MutableIntState = remember { mutableIntStateOf(-1) },
    touchable: Boolean = true,
    captureController: CaptureControllerDelegate = rememberCaptureControllerDelegate()
) {
    var selectedPartIndex by selectedPartIndexState

    val infiniteTransition = rememberInfiniteTransition(label = "CatEditor_SelectedPart")
    val ratio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "HighlightColorBlend"
    )

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(captureController) {
        captureController.onPerCapture = {
            // set normal state
            selectedPartIndex = -1
            scale = 1f
            offset = Offset.Zero
        }
    }

    val matrix = remember { Matrix() }
    Canvas(
        contentDescription = "Cat Editor",
        modifier = Modifier
            .then(modifier)
            .capturable(captureController.getDelegate())
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            }
            .onSizeChanged {
                val size = min(it.width, it.height)
                matrix.reset()
                matrix.translate((it.width - size) / 2f, (it.height - size) / 2f)
                matrix.scale(size / VIEW_PORT_SIZE, size / VIEW_PORT_SIZE)
            }
            .pointerInput(touchable) {
                if (!touchable) {
                    return@pointerInput
                }
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += (pan * scale)
                }
            }
            .pointerInput(touchable) {
                if (!touchable) {
                    return@pointerInput
                }
                detectTapGestures {
                    var handler = false
                    val pointMatrix = matrix.toInvert()
                    for (i in CatParts.drawOrders.size - 1 downTo 0) {
                        val pathDraw = CatParts.drawOrders[i]
                        if (!pathDraw.touchable) {
                            continue
                        }

                        if (Utilities.isPointInRegion(it, pointMatrix, pathDraw.regin)) {
                            handler = selectedPartIndex != i
                            selectedPartIndex = i
                            break
                        }
                    }
                    if (!handler) {
                        selectedPartIndex = -1
                    }
                }
            },
        onDraw = {
            withTransform({ transform(matrix) }) {
                CatParts.drawOrders.forEachIndexed { index, pathDraw ->
                    var color = colors[index]
                    if (selectedPartIndex == index) {
                        val blend = Utilities.getHighlightColor(color).toArgb()
                        color = color.blend(blend, ratio)
                    }
                    pathDraw.drawLambda.invoke(this, Color(color))
                }
            }
        }
    )
}
