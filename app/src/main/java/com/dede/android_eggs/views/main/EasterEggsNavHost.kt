package com.dede.android_eggs.views.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.android_next.egg.ACTION_SHOE_ANDROID_NEXT_DIALOG
import com.android_next.egg.AndroidNextTimelineDialog
import com.dede.android_eggs.cat_editor.CatEditorScreen
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavController
import com.dede.android_eggs.navigation.rememberEasterEggsDestinations
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.Receiver
import com.dede.android_eggs.views.main.compose.EasterEggsScreen
import com.dede.android_eggs.views.main.compose.LocalKonfettiState
import com.dede.android_eggs.views.main.compose.rememberKonfettiState

const val ACTION_CAT_EDITOR = "com.dede.android_eggs.action.CAT_EDITOR"

private const val DURATION = 400
private const val SCALE = 0.88f
private const val ALPHA = 0.6f

@Composable
fun EasterEggsNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val konfettiState = rememberKonfettiState()
    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalKonfettiState provides konfettiState,
    ) {
        val navDestinations = rememberEasterEggsDestinations()
        NavHost(
            navController = navController,
            startDestination = EasterEggsScreen.route,
            modifier = modifier,
            enterTransition = {
                fadeIn(animationSpec = tween(DURATION), initialAlpha = ALPHA) +
                        scaleIn(animationSpec = tween(DURATION), initialScale = SCALE) +
                        slideInHorizontally(animationSpec = tween(DURATION)) { it }
            },
            exitTransition = {
                scaleOut(animationSpec = tween(DURATION), targetScale = SCALE) +
                        slideOutHorizontally(animationSpec = tween(DURATION))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(DURATION), initialAlpha = ALPHA) +
                        scaleIn(animationSpec = tween(DURATION), initialScale = SCALE) +
                        slideInHorizontally(animationSpec = tween(DURATION))
            },
            popExitTransition = {
                scaleOut(animationSpec = tween(DURATION), targetScale = SCALE) +
                        slideOutHorizontally(animationSpec = tween(DURATION)) { it }
            },
        ) {
            navDestinations.forEach { dest ->
                when (dest.type) {
                    EasterEggsDestination.Type.Composable -> {
                        composable(route = dest.route, content = { dest.Content() })
                    }
                    EasterEggsDestination.Type.Dialog -> {
                        dialog(route = dest.route, content = { dest.Content() })
                    }
                    EasterEggsDestination.Type.ModalBottomSheet -> {
                        // don't work, https://issuetracker.google.com/issues/435672848
                        throw UnsupportedOperationException()
                    }
                }
            }
        }

        LocalEvent.Receiver(ACTION_SHOE_ANDROID_NEXT_DIALOG) {
            navController.navigate(AndroidNextTimelineDialog.route)
        }

        LocalEvent.Receiver(ACTION_CAT_EDITOR) {
            navController.navigate(CatEditorScreen.route)
        }
    }
}
