package com.dede.android_eggs.views.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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

    LaunchedEffect(Unit) {
        if (!isAgreedPrivacyPolicy(context)) {
            overlayManager.awaitDialog(OverlayRoute.WelcomeDialog)
        }
        if (!Utils.areAnimatorEnabled(context)
            && !AnimatorAlertPrefs.isDontShowAgain(context)
        ) {
            overlayManager.awaitDialog(OverlayRoute.AnimatorAlertDialog)
        }
        DeepLink.handleNavKey(navigator)
    }
}
