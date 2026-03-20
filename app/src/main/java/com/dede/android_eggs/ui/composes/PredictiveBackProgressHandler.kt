package com.dede.android_eggs.ui.composes

import androidx.compose.animation.core.animate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toOffset
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.launch

object PredictiveBackProgressHandler {

    private const val SHRINK_FACTOR = 0.15f

    private fun computeBackShrinkProgress(progress: Float): Float {
        return 1f - (SHRINK_FACTOR * progress.coerceIn(0f, 1f))
    }

    private val Size = IntSize(100, 100)
    private val Space = IntSize(200, 200)

    private fun Alignment.toTransformOrigin(layoutDirection: LayoutDirection): TransformOrigin {
        val offset = this.align(Size, Space, layoutDirection).toOffset() / 100f
        return TransformOrigin(offset.x, offset.y)
    }

    fun GraphicsLayerScope.predictiveBackShrink(
        progress: Float,
        shrinkOrigin: Alignment = Alignment.Center,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr
    ) {
        val shrinkProgress = computeBackShrinkProgress(progress)
        this.scaleX = shrinkProgress
        this.scaleY = shrinkProgress

        this.transformOrigin = shrinkOrigin.toTransformOrigin(layoutDirection)
    }
}

@Composable
fun predictiveBackProgressState(
    enabled: Boolean,
    backEndValue: (progress: Float) -> Float = { it },
    onBack: suspend () -> Unit
): State<Float> {
    val progressState = remember { mutableFloatStateOf(0f) }
    var progress by progressState

    val navState = rememberNavigationEventState(NavigationEventInfo.None)
    LaunchedEffect(navState.transitionState) {
        when (val state = navState.transitionState) {
            is NavigationEventTransitionState.InProgress -> {
                progress = state.latestEvent.progress
            }
            is NavigationEventTransitionState.Idle -> {
            }
        }
    }
    val scope = rememberCoroutineScope()
    NavigationBackHandler(
        state = navState,
        isBackEnabled = enabled,
        onBackCompleted = {
            scope.launch {
                onBack()
                progress = backEndValue(progress)
            }
        },
        onBackCancelled = {
            scope.launch {
                animate(progress, 0f) { value, _ ->
                    progress = value
                }
            }
        },
    )

    LaunchedEffect(enabled) {
        if (enabled) {
            progress = 0f
        }
    }
    return progressState
}
