package com.dede.android_eggs.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

interface OverlayRoute {
    data object WelcomeDialog : OverlayRoute
    data object AnimatorAlertDialog : OverlayRoute
    data object SnapshotDialog : OverlayRoute
    data object TimelineDialog : OverlayRoute
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

@InstallIn(SingletonComponent::class)
@EntryPoint
interface OverlayContentProvidersEntryPoint {
    val providers: Set<@JvmSuppressWildcards OverlayContentProvider>
}

@Composable
fun rememberOverlayContentProviders(): Set<OverlayContentProvider> {
    val context = LocalContext.current.applicationContext
    return remember {
        EntryPointAccessors.fromApplication<OverlayContentProvidersEntryPoint>(context).providers
    }
}
