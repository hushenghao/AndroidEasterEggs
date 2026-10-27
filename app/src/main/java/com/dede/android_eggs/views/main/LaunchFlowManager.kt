package com.dede.android_eggs.views.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.dede.android_eggs.navigation.DeepLink
import com.dede.android_eggs.navigation.Navigator
import com.dede.android_eggs.navigation.OverlayManager
import com.dede.android_eggs.navigation.OverlayRoute
import com.dede.android_eggs.views.main.compose.AnimatorAlertPrefs
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy
import com.dede.basic.Utils

@Composable
fun LaunchOverlayFlow(
    overlayManager: OverlayManager,
    navigator: Navigator,
) {
    val context = LocalContext.current

    // A deep link delivered via onNewIntent (app already alive) stores a pending nav key,
    // so consume it on every resume instead of only once on first composition.
    LifecycleResumeEffect(Unit) {
        DeepLink.handleNavKey(navigator)
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        if (!isAgreedPrivacyPolicy(context)) {
            overlayManager.awaitDialog(OverlayRoute.WelcomeDialog)
        }
        if (!Utils.areAnimatorEnabled(context)
            && !AnimatorAlertPrefs.isDontShowAgain(context)
        ) {
            overlayManager.awaitDialog(OverlayRoute.AnimatorAlertDialog)
        }
    }
}
