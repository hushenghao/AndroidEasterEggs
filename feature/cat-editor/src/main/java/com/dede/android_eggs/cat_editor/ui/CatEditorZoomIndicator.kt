package com.dede.android_eggs.cat_editor.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun ZoomHapticFeedback(
    zoom: Float,
    hapticZoomStep: Float = 0.1f,
    hapticInterval: Long = 80L,
) {
    val hapticFeedback = LocalHapticFeedback.current
    var lastHapticTime by remember(hapticInterval) { mutableLongStateOf(System.currentTimeMillis()) }
    var lastHapticZoom by remember(hapticZoomStep) { mutableFloatStateOf(zoom) }
    LaunchedEffect(zoom) {
        val now = System.currentTimeMillis()
        if ((now - lastHapticTime) >= hapticInterval && abs(lastHapticZoom - zoom) > hapticZoomStep) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            lastHapticTime = now
            lastHapticZoom = zoom
        }
    }
}

@Composable
internal fun ZoomValuePopup(zoomValue: String, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(zoomValue) {
        visible = true
        delay(1.seconds)
        visible = false
    }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500)),
    ) {
        Card(
            shape = CircleShape,
            colors = cardColors(containerColor = colorScheme.surfaceColorAtElevation(4.dp)),
        ) {
            Text(
                text = zoomValue,
                modifier = Modifier
                    .padding(12.dp)
                    .sizeIn(minWidth = 30.dp),
                style = typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
