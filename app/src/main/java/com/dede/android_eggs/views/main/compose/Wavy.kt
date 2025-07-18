@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun PreviewWavy() {
    Wavy(
        modifier = Modifier.fillMaxWidth(),
        strokeWidth = 1.dp,
        amplitude = 0.8f,
        wavelength = 30.dp,
    )
}

@Composable
fun Wavy(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    strokeWidth: Dp = 1.2.dp,
    amplitude: Float = 0.8f,
    wavelength: Dp = 30.dp,
    waveSpeed: Dp = 0.dp,// still waves
) {
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }
    val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        LinearWavyProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = Color.Transparent,
            stroke = stroke,
            progress = { 1f },
            amplitude = { amplitude },
            wavelength = wavelength,
            waveSpeed = waveSpeed,
        )
    }
}
