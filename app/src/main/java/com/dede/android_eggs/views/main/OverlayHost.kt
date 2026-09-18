package com.dede.android_eggs.views.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dede.android_eggs.navigation.OverlayContentProvider
import com.dede.android_eggs.navigation.OverlayManager
import com.dede.android_eggs.navigation.OverlayRoute
import com.dede.android_eggs.views.main.compose.AnimatorDisabledAlertDialog
import com.dede.android_eggs.views.main.compose.WelcomeDialog
import com.dede.android_eggs.views.settings.compose.prefs.SnapshotOverlay
import com.dede.android_eggs.views.timeline.TimelineListDialog

@Composable
fun OverlayHost(
    overlayManager: OverlayManager,
    contentProviders: Set<OverlayContentProvider> = emptySet(),
) {
    val route by overlayManager.currentRoute.collectAsStateWithLifecycle()

    when (route) {
        OverlayRoute.WelcomeDialog -> {
            WelcomeDialog(onDismiss = overlayManager::dismiss)
        }

        OverlayRoute.AnimatorAlertDialog -> {
            AnimatorDisabledAlertDialog(onDismiss = overlayManager::dismiss)
        }

        OverlayRoute.SnapshotPreview -> {
            SnapshotOverlay(onDismiss = overlayManager::dismiss)
        }

        OverlayRoute.TimelineDialog -> {
            TimelineListDialog(onDismiss = overlayManager::dismiss)
        }

        else -> {
            if (route != null) {
                contentProviders.firstOrNull { provider -> provider.route == route }
                    ?.Content(onDismiss = overlayManager::dismiss)
            }
        }
    }
}
