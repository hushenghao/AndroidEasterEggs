package com.dede.android_eggs.util.compose

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.Dp

fun CornerBasedShape.top(size: Dp): CornerBasedShape {
    return this.copy(topStart = CornerSize(size), topEnd = CornerSize(size))
}

fun CornerBasedShape.bottom(size: Dp): CornerBasedShape {
    return this.copy(bottomStart = CornerSize(size), bottomEnd = CornerSize(size))
}

fun CornerBasedShape.start(size: Dp): CornerBasedShape {
    return this.copy(topStart = CornerSize(size), bottomStart = CornerSize(size))
}

fun CornerBasedShape.end(size: Dp): CornerBasedShape {
    return this.copy(topEnd = CornerSize(size), bottomEnd = CornerSize(size))
}