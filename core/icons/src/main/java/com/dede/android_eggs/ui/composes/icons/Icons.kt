package com.dede.android_eggs.ui.composes.icons

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal inline fun materialIcon(
    name: String,
    autoMirror: Boolean = false,
    defaultWidth: Dp = 24.dp,
    defaultHeight: Dp = 24.dp,
    viewportWidth: Float = 24f,
    viewportHeight: Float = 24f,
    tintColor: Color = Color.Unspecified,
    tintBlendMode: BlendMode = BlendMode.SrcIn,
    block: ImageVector.Builder.() -> ImageVector.Builder
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = defaultWidth,
    defaultHeight = defaultHeight,
    viewportWidth = viewportWidth,
    viewportHeight = viewportHeight,
    autoMirror = autoMirror,
    tintColor = tintColor,
    tintBlendMode = tintBlendMode
).block().build()
