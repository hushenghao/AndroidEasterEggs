@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.composable.colorpicker

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun LinearGradientSlider(
    modifier: Modifier = Modifier,
    value: Float,
    startColor: Color,
    endColor: Color,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
) {
    val sliderColors = SliderDefaults.colors()
    val interactionSource = remember { MutableInteractionSource() }

    val state = rememberSliderState(value)

    // rememberSliderState takes `value` as its initial value only. Sync external
    // changes (e.g. the shuffle button) back to the state with the same default
    // spring animateFloatAsState used; drag-driven changes are already reflected
    // by the gesture itself.
    LaunchedEffect(value) {
        if (value != state.value) {
            if (state.isDragging) {
                state.value = value
            } else {
                animate(
                    initialValue = state.value,
                    targetValue = value,
                    animationSpec = spring(),
                ) { animated, _ ->
                    state.value = animated
                }
            }
        }
    }

    Slider(
        state = state,
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
        onValueChange = {
            state.value = it
            onValueChange(it)
        },
        onValueChangeFinished = onValueChangeFinished,
    )
}
