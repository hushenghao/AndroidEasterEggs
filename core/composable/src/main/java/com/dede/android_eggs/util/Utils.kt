package com.dede.android_eggs.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toLocalPx() = with(LocalDensity.current) { this@toLocalPx.toPx() }
