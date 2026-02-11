package com.dede.android_eggs.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.Serializable

interface EasterEggsDestination {

    enum class Type {
        Composable,
        Dialog,
        ModalBottomSheet,
    }

    val type: Type
        get() = Type.Composable

    val route: NavKey

    @Composable
    fun Content(properties: DestinationProps) {
        Content()
    }

    @Composable
    fun Content() {
    }

    interface Provider {
        fun provider(): EasterEggsDestination
    }

    data class DestinationProps(
        val navKey: NavKey,
        val onBack: () -> Unit = {},
    )

    // @formatter:off
    @Serializable data object EasterEggs : NavKey
    @Serializable data object WelcomeDialog : NavKey
    @Serializable data object SnapshotDialog : NavKey
    @Serializable data object AndroidNextTimelineDialog : NavKey
    @Serializable data object AnimatorDisabledAlertDialog : NavKey
    @Serializable data object CatEditor : NavKey
    @Serializable data object LibrariesInfo : NavKey
    @Serializable data object TimelineDialog : NavKey
    // @formatter:on
}

@InstallIn(SingletonComponent::class)
@EntryPoint
interface EasterEggDestinationsEntryPoint {

    val destinations: Set<@JvmSuppressWildcards EasterEggsDestination>
}

@Composable
fun rememberEasterEggsDestinations(): Set<EasterEggsDestination> {
    val context = LocalContext.current.applicationContext
    return remember {
        EntryPointAccessors.fromApplication<EasterEggDestinationsEntryPoint>(context).destinations
    }
}
