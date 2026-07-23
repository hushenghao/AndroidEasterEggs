package com.dede.android_eggs.views.theme

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils

fun Int.blend(
    color: Int,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.5f,
): Int = ColorUtils.blendARGB(this, color, fraction)

val defaultSeedColor = Color(0xFF4ADC8A)
