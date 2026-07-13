package com.dede.android_eggs.ui.composes

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.window.BackEvent
import android.window.OnBackAnimationCallback
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.compose.animation.core.animate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toOffset
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

@SuppressLint("NewApi")
@Composable
fun predictiveBackProgressState(
    enabled: Boolean,
    backEndValue: (progress: Float) -> Float = { it },
    onBack: suspend () -> Unit
): State<Float> {
    val progressState = remember { mutableFloatStateOf(0f) }
    var progress by progressState

    val scope = rememberCoroutineScope()

    val currentOnBack by rememberUpdatedState(onBack)
    val currentBackEndValue by rememberUpdatedState(backEndValue)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        val context = LocalContext.current
        val activity = context as? Activity ?: return progressState
        val dispatcher = activity.onBackInvokedDispatcher
        val callback = remember {
            object : OnBackAnimationCallback {
                override fun onBackProgressed(backEvent: BackEvent) {
                    progress = backEvent.progress
                }

                override fun onBackStarted(backEvent: BackEvent) {
                    progress = backEvent.progress
                }

                override fun onBackInvoked() {
                    scope.launch {
                        currentOnBack()
                        progress = currentBackEndValue(progress)
                    }
                }

                override fun onBackCancelled() {
                    scope.launch {
                        animate(progress, 0f) { value, _ ->
                            progress = value
                        }
                    }
                }
            }
        }
        DisposableEffect(enabled) {
            if (enabled) {
                dispatcher.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, callback
                )
            }
            onDispose {
                dispatcher.unregisterOnBackInvokedCallback(callback)
            }
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val activity = context as? Activity ?: return progressState
        val dispatcher = activity.onBackInvokedDispatcher
        val callback = remember {
            OnBackInvokedCallback {
                scope.launch {
                    currentOnBack()
                    progress = currentBackEndValue(progress)
                }
            }
        }
        DisposableEffect(enabled) {
            if (enabled) {
                dispatcher.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, callback
                )
            }
            onDispose {
                dispatcher.unregisterOnBackInvokedCallback(callback)
            }
        }
    }

    LaunchedEffect(enabled) {
        if (enabled) {
            progress = 0f
        }
    }
    return progressState
}
