package com.dede.android_eggs.navigation

import androidx.compose.runtime.Composable
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

interface OverlayRoute {
    data object WelcomeDialog : OverlayRoute
    data object AnimatorAlertDialog : OverlayRoute
    data object SnapshotPreview : InWindowOverlay
    data object TimelineDialog : OverlayRoute

    /**
     * An [OverlayRoute] whose overlay draws inside the app window instead of a window of its own.
     *
     * A window of its own keeps the content behind it out of reach of accessibility services. An
     * overlay in the same window does not, so the host has to hide that content while such a
     * route is shown.
     */
    interface InWindowOverlay : OverlayRoute
}

interface OverlayContentProvider {
    val route: OverlayRoute

    @Composable
    fun Content(onDismiss: () -> Unit)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class OverlayContentProviderModule {
    @Multibinds
    abstract fun bindOverlayContentProviders(): Set<OverlayContentProvider>
}
