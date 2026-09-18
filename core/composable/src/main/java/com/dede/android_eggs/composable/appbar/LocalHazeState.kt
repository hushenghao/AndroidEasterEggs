package com.dede.android_eggs.composable.appbar

import androidx.compose.runtime.staticCompositionLocalOf
import dev.chrisbanes.haze.HazeState

/**
 * The [HazeState] of the nearest [HazeScaffold], provided to its content scope.
 *
 * Its source is the scaffold content, so the scaffold bars and anything opened from that content
 * blur it. Overlays which have to blur the whole window instead read [LocalOverlayHazeState].
 *
 * Composition locals propagate into popup windows, so effects inside popups can share the
 * same state as the sources in the main window without parameter threading.
 */
val LocalHazeState = staticCompositionLocalOf { HazeState() }