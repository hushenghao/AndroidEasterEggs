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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.android_next.egg.ACTION_SHOE_ANDROID_NEXT_DIALOG
import com.dede.android_eggs.navigation.BottomSheetSceneStrategy
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.EasterEggsDestination.DestinationProps
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.android_eggs.navigation.Navigator
import com.dede.android_eggs.navigation.rememberEasterEggsDestinations
import com.dede.android_eggs.navigation.rememberNavigationState
import com.dede.android_eggs.navigation.toEntries
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.Receiver
import com.dede.android_eggs.views.main.compose.AnimatorDisabledAlertDialog
import com.dede.android_eggs.views.main.compose.LocalKonfettiState
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy
import com.dede.android_eggs.views.main.compose.rememberKonfettiState
import com.dede.basic.Utils

const val ACTION_CAT_EDITOR = "com.dede.android_eggs.action.CAT_EDITOR"

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
    val navigationState = rememberNavigationState(startRoute = EasterEggsDestination.EasterEggs)
    val navigator = remember { Navigator(navigationState) }
    CompositionLocalProvider(
        LocalNavigator provides navigator,
        LocalKonfettiState provides rememberKonfettiState(),
    ) {
        val entryProvider = entryProvider {
            val navDestinations = rememberEasterEggsDestinations()
            val onBackProp = { navigator.goBack() }
            navDestinations.forEach { dest ->
                when (dest.type) {
                    EasterEggsDestination.Type.Composable -> {
                        entry(dest.route) {
                            val properties = DestinationProps(it)
                            dest.Content(properties)
                        }
                    }
                    EasterEggsDestination.Type.Dialog -> {
                        entry(key = dest.route, metadata = DialogSceneStrategy.dialog()) {
                            val properties = DestinationProps(it, onBack = onBackProp)
                            dest.Content(properties)
                        }
                    }
                    EasterEggsDestination.Type.BottomSheet -> {
                        entry(
                            key = dest.route,
                            metadata = BottomSheetSceneStrategy.bottomSheet(customBottomSheet = true)
                        ) {
                            val properties = DestinationProps(it, onBack = onBackProp)
                            dest.Content(properties)
                        }
                    }
                }
            }
        }
        NavDisplay(
            modifier = modifier,
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            sceneStrategy = remember {
                DialogSceneStrategy<NavKey>() then BottomSheetSceneStrategy()
            },
            transitionSpec = { navTransition() },
            popTransitionSpec = { popTransition() },
            predictivePopTransitionSpec = { popTransition() },
        )

        val context = LocalContext.current
        LaunchedEffect(navigator) {
            if (!isAgreedPrivacyPolicy(context)) {
                navigator.navigate(EasterEggsDestination.WelcomeDialog, true)
            }

            if (!AnimatorDisabledAlertDialog.isDontShowAgain(context) &&
                !Utils.areAnimatorEnabled(context)
            ) {
                navigator.navigate(EasterEggsDestination.AnimatorDisabledAlertDialog, true)
            }
        }

        LocalEvent.Receiver(ACTION_SHOE_ANDROID_NEXT_DIALOG) {
            navigator.navigate(EasterEggsDestination.AndroidNextTimelineDialog)
        }

        LocalEvent.Receiver(ACTION_CAT_EDITOR) {
            navigator.navigate(EasterEggsDestination.CatEditor)
        }
    }
}
