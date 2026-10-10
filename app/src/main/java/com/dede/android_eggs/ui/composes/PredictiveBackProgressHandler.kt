package com.dede.android_eggs.ui.composes

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toOffset
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.launch

object PredictiveBackProgressHandler {

    private const val SHRINK_FACTOR = 0.15f

    private fun computeBackShrinkProgress(progress: Float, shrinkFactor: Float): Float {
        return 1f - (shrinkFactor * progress.coerceIn(0f, 1f))
    }

    private val Size = IntSize(100, 100)
    private val Space = IntSize(200, 200)

    private fun Alignment.toTransformOrigin(layoutDirection: LayoutDirection): TransformOrigin {
        val offset = this.align(Size, Space, layoutDirection).toOffset() / 100f
        return TransformOrigin(offset.x, offset.y)
    }

    fun GraphicsLayerScope.predictiveBackShrink(
        progress: Float,
        shrinkFactor: Float = SHRINK_FACTOR,
        shrinkOrigin: Alignment = Alignment.Center,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr
    ) {
        val shrinkProgress = computeBackShrinkProgress(progress, shrinkFactor)
        this.scaleX = shrinkProgress
        this.scaleY = shrinkProgress

        this.transformOrigin = shrinkOrigin.toTransformOrigin(layoutDirection)
    }
}

private fun NavigationEvent.copy(
    swipeEdge: Int = this.swipeEdge,
    progress: Float = this.progress,
    touchX: Float = this.touchX,
    touchY: Float = this.touchY,
    frameTimeMillis: Long = this.frameTimeMillis,
) = NavigationEvent(swipeEdge, progress, touchX, touchY, frameTimeMillis)

@Composable
fun predictiveBackProgressEventState(
    enabled: Boolean,
    onBackCompleted: () -> Unit,
    onBackCancelled: () -> Unit = {},
): State<NavigationEvent> {
    val navEventState = remember { mutableStateOf(NavigationEvent()) }
    var navEvent by navEventState

    val navState = rememberNavigationEventState(NavigationEventInfo.None)
    LaunchedEffect(navState.transitionState) {
        when (val state = navState.transitionState) {
            is NavigationEventTransitionState.InProgress -> {
                navEvent = state.latestEvent
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
            onBackCompleted()
        },
        onBackCancelled = {
            scope.launch {
                animate(navEvent.progress, 0f, animationSpec = tween()) { value, _ ->
                    navEvent = navEvent.copy(progress = value)
                }
            }
            onBackCancelled()
        },
    )

    LaunchedEffect(enabled) {
        if (enabled) {
            navEvent = navEvent.copy(progress = 0f)
        }
    }
    return navEventState
}

@Composable
fun predictiveBackProgressState(
    enabled: Boolean,
    onBackCompleted: () -> Unit,
    onBackCancelled: () -> Unit = {},
): State<Float> {
    val navEventState = predictiveBackProgressEventState(enabled, onBackCompleted, onBackCancelled)
    return remember { derivedStateOf { navEventState.value.progress } }
}
