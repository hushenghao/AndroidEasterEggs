package com.dede.android_eggs.composable.appbar

import androidx.compose.runtime.staticCompositionLocalOf
import dev.chrisbanes.haze.HazeState

/**
 * The [HazeState] whose source covers everything the window draws, for full screen overlays which
 * blur the screen behind them.
 *
 * The nav host provides this one. Unlike [LocalHazeState], which every [HazeScaffold] narrows to
 * its own content, it stays window wide down the whole tree, so an overlay can sample the whole
 * window wherever it is composed from.
 *
 * The overlay itself has to stay outside the source, so that the effect consumes content drawn
 * before it instead of its own output.
 */
val LocalOverlayHazeState = staticCompositionLocalOf<HazeState> { HazeState() }
