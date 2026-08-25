package com.dede.android_eggs.composable

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

/**
 * Material 3 window width size class panes,
 * compact < 600dp <= medium < 840dp <= expanded.
 */
enum class WindowWidthPane { COMPACT, MEDIUM, EXPANDED }

@Composable
fun currentWindowWidthPane(): WindowWidthPane {
    val sizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    return when {
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ->
            WindowWidthPane.EXPANDED
        sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ->
            WindowWidthPane.MEDIUM
        else -> WindowWidthPane.COMPACT
    }
}
