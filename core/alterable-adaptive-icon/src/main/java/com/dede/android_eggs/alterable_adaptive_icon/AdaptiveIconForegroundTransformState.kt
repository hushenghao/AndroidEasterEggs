package com.dede.android_eggs.alterable_adaptive_icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Matrix

@Composable
fun rememberAdaptiveIconForegroundTransformState(): AdaptiveIconForegroundTransformState {
    return remember { AdaptiveIconForegroundTransformState() }
}

@Stable
class AdaptiveIconForegroundTransformState {

    val matrix = Matrix()

    var version by mutableIntStateOf(0)
        private set

    fun reset() {
        matrix.reset()
        version++
    }

    fun updateFrom(transformMatrix: Matrix) {
        matrix.setFrom(transformMatrix)
        version++
    }
}
