@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dede.android_eggs.composable.appbar.LocalOverlayHazeState
import com.dede.android_eggs.local_provider.rememberCustomTabsUriHandler
import com.dede.android_eggs.navigation.EasterEggsDestination.EasterEggs
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.android_eggs.navigation.LocalOverlayManager
import com.dede.android_eggs.navigation.Navigator.Companion.rememberNavigator
import com.dede.android_eggs.navigation.OverlayRoute
import com.dede.android_eggs.navigation.rememberEasterEggsDestinations
import com.dede.android_eggs.navigation.rememberNavigationState
import com.dede.android_eggs.navigation.rememberOverlayContentProviders
import com.dede.android_eggs.navigation.rememberOverlayManager
import com.dede.android_eggs.navigation.toEntries
import com.dede.android_eggs.views.main.compose.LocalKonfettiState
import com.dede.android_eggs.views.main.compose.rememberKonfettiController
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private const val DURATION = 400
private const val SCALE = 0.88f
private const val ALPHA = 0.6f

private fun navTransition(): ContentTransform {
    return ContentTransform(
        targetContentEnter = fadeIn(animationSpec = tween(DURATION), initialAlpha = ALPHA) +
                scaleIn(animationSpec = tween(DURATION), initialScale = SCALE) +
                slideInHorizontally(animationSpec = tween(DURATION)) { it },
        initialContentExit = slideOutHorizontally(animationSpec = tween(DURATION))
    )
}

private fun popTransition(): ContentTransform {
    return ContentTransform(
        targetContentEnter = fadeIn(animationSpec = tween(DURATION), initialAlpha = ALPHA) +
                scaleIn(animationSpec = tween(DURATION), initialScale = SCALE) +
                slideInHorizontally(animationSpec = tween(DURATION)),
        initialContentExit = slideOutHorizontally(animationSpec = tween(DURATION)) { it }
    )
}

@Composable
fun EasterEggsNavHost(
    modifier: Modifier = Modifier,
) {
    val navigationState = rememberNavigationState(startRoute = EasterEggs)
    val navigator = rememberNavigator(navigationState)
    val overlayManager = rememberOverlayManager()
    val konfettiController = rememberKonfettiController()
    val uriHandler = rememberCustomTabsUriHandler()
    // Sources everything the window draws, so a full screen overlay can blur the whole screen
    // behind it. Screens keep their own content scoped state, see HazeScaffold.
    val overlayHazeState = rememberHazeState()
    CompositionLocalProvider(
        LocalUriHandler provides uriHandler,
        LocalNavigator provides navigator,
        LocalOverlayManager provides overlayManager,
        LocalKonfettiState provides konfettiController,
        LocalOverlayHazeState provides overlayHazeState,
    ) {
        val entryProvider = entryProvider {
            val navDestinations = rememberEasterEggsDestinations()
            navDestinations.forEach { dest ->
                entry(dest.route) {
                    dest.Content()
                }
            }
        }
        // Overlays which are not windows of their own draw in this Box, above the navigation
        // content, and being the topmost hit target they also keep it from being touched.
        Box(modifier = modifier.fillMaxSize()) {
            val overlayRoute by overlayManager.currentRoute.collectAsStateWithLifecycle()
            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(overlayHazeState)
                    .then(
                        // A window of its own already keeps the content behind it out of reach
                        // of accessibility services, an overlay in this window does not.
                        if (overlayRoute is OverlayRoute.InWindowOverlay) {
                            Modifier.semantics { hideFromAccessibility() }
                        } else {
                            Modifier
                        }
                    ),
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
                transitionSpec = { navTransition() },
                popTransitionSpec = { popTransition() },
                predictivePopTransitionSpec = { popTransition() },
            )

            LaunchOverlayFlow(overlayManager = overlayManager, navigator = navigator)

            val overlayContentProviders = rememberOverlayContentProviders()
            OverlayHost(
                overlayManager = overlayManager,
                contentProviders = overlayContentProviders,
            )
        }
    }
}
