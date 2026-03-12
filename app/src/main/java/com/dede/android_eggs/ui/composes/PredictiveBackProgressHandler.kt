package com.dede.android_eggs.ui.composes

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.animate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.catch

@Composable
fun predictiveBackProgressState(
    enabled: Boolean,
    backEndValue: (progress: Float) -> Float = { it },
    onBack: suspend () -> Unit
): State<Float> {
    val progressState = remember { mutableFloatStateOf(0f) }

    var progress by progressState
    PredictiveBackHandler(enabled = enabled) { flow ->
        flow.catch {
            animate(progress, 0f) { value, _ ->
                progress = value
            }
        }.collect { event ->
            progress = event.progress
        }

        onBack()
        progress = backEndValue(progress)
    }
    LaunchedEffect(enabled) {
        if (enabled) {
            progress = 0f
        }
    }
    return progressState
}
