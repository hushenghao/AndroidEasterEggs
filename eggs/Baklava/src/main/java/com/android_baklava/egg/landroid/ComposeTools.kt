/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android_baklava.egg.landroid

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable fun Dp.toLocalPx() = with(LocalDensity.current) { this@toLocalPx.toPx() }

operator fun Easing.times(next: Easing) = { x: Float -> next.transform(transform(x)) }

fun flickerFadeEasing(rng: Random) = Easing { frac -> if (rng.nextFloat() < frac) 1f else 0f }

val flickerFadeIn =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = 1000,
                easing = CubicBezierEasing(0f, 1f, 1f, 0f) * flickerFadeEasing(Random),
            )
    )

fun flickerFadeInAfterDelay(delay: Int = 0) =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = 1000,
                delayMillis = delay,
                easing = CubicBezierEasing(0f, 1f, 1f, 0f) * flickerFadeEasing(Random),
            )
    )

@Composable
fun ConsoleButton(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    color: Color,
    bgColor: Color,
    borderColor: Color,
    text: String,
    onClick: () -> Unit,
) {
    Text(
        style = textStyle,
        color = color,
        modifier =
            modifier
                .clickable { onClick() }
                .background(color = bgColor)
                .border(width = 1.dp, color = borderColor)
                .padding(6.dp)
                .minimumInteractiveComponentSize(),
        text = text,
    )
}
