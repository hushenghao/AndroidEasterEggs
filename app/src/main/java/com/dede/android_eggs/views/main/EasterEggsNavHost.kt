@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dede.android_eggs.local_provider.rememberCustomTabsUriHandler
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.EasterEggsDestination.EasterEggs
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.android_eggs.navigation.LocalOverlayManager
import com.dede.android_eggs.navigation.Navigator.Companion.rememberNavigator
import com.dede.android_eggs.navigation.rememberEasterEggsDestinations
import com.dede.android_eggs.navigation.rememberOverlayContentProviders
import com.dede.android_eggs.navigation.rememberOverlayManager
import com.dede.android_eggs.navigation.rememberNavigationState
import com.dede.android_eggs.navigation.toEntries
import com.dede.android_eggs.views.main.compose.LocalKonfettiState
import com.dede.android_eggs.views.main.compose.rememberKonfettiController

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
    CompositionLocalProvider(
        LocalUriHandler provides uriHandler,
        LocalNavigator provides navigator,
        LocalOverlayManager provides overlayManager,
        LocalKonfettiState provides konfettiController,
    ) {
        val onBack = { navigator.goBack() }
        val entryProvider = entryProvider {
            val navDestinations = rememberEasterEggsDestinations()
            navDestinations.forEach { dest ->
                entry(dest.route) {
                    dest.Content()
                }
            }
        }
        NavDisplay(
            modifier = modifier,
            entries = navigationState.toEntries(entryProvider),
            onBack = onBack,
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
