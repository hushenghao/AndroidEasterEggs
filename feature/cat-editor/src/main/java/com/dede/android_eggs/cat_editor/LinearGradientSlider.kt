@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.cat_editor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
internal fun LinearGradientSlider(
    modifier: Modifier = Modifier,
    value: Float,
    startColor: Color,
    endColor: Color,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {}
) {
    val sliderColors = SliderDefaults.colors()
    val interactionSource = remember { MutableInteractionSource() }

    val animValue by animateFloatAsState(value, label = "SliderValue")
    Slider(
        colors = sliderColors,
        modifier = Modifier.then(modifier),
        interactionSource = interactionSource,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                colors = sliderColors,
                enabled = true,
                thumbSize = DpSize(4.dp, 30.dp),
            )
        },
        track = {
            val colors = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                listOf(endColor, startColor)
            } else {
                listOf(startColor, endColor)
            }
            val linear = Brush.linearGradient(colors)
            Checkerboard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .drawWithContent {
                        drawContent()
                        drawRect(linear)
                    },
            )
        },
        value = animValue,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished
    )

}