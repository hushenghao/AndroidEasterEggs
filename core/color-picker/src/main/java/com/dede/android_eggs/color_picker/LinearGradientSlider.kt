package com.dede.android_eggs.color_picker

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.VerticalSlider
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
    colors: List<Color>,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
) {
    val sliderColors = SliderDefaults.colors()
    val interactionSource = remember { MutableInteractionSource() }

    val state = rememberSliderState(value, trackRange = valueRange)

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
            val trackColors = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                colors.reversed()
            } else {
                colors
            }
            val linear = Brush.linearGradient(trackColors)
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

@Composable
fun VerticalGradientSlider(
    modifier: Modifier = Modifier,
    value: Float,
    colors: List<Color>,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
) {
    val sliderColors = SliderDefaults.colors()
    val interactionSource = remember { MutableInteractionSource() }

    val state = rememberSliderState(value, trackRange = valueRange)

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

    // Caller colors are ordered from the minimum value to the maximum, while the
    // vertical track is painted top to bottom, so the list order is reversed.
    val vertical = Brush.verticalGradient(colors.asReversed())

    VerticalSlider(
        state = state,
        onValueChange = {
            state.value = it
            onValueChange(it)
        },
        modifier = modifier,
        topToBottom = false,  // the bottom end holds the minimum value
        colors = sliderColors,
        interactionSource = interactionSource,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                isVertical = true,
                colors = sliderColors,
                enabled = true,
                thumbSize = DpSize(30.dp, 4.dp),
            )
        },
        track = {
            Checkerboard(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .drawWithContent {
                        drawContent()
                        drawRect(vertical)
                    },
            )
        },
        onValueChangeFinished = onValueChangeFinished,
    )
}
