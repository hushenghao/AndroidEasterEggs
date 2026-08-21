package com.dede.android_eggs.composable.appbar

import androidx.compose.runtime.staticCompositionLocalOf
import dev.chrisbanes.haze.HazeState

/**
 * The [HazeState] of the nearest [HazeScaffold], provided to its content scope.
 *
 * Composition locals propagate into popup windows, so effects inside popups can share the
 * same state as the sources in the main window without parameter threading.
 */
val LocalHazeState = staticCompositionLocalOf<HazeState> { HazeState() }