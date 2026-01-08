package com.dede.android_eggs.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

interface EasterEggsDestination {

    enum class Type {
        Composable,
        Dialog,
        ModalBottomSheet,
    }

    val type: Type
        get() = Type.Composable

    val route: String

    @Composable
    fun content() {
    }

    interface Provider {
        fun provider(): EasterEggsDestination
    }
}

@InstallIn(SingletonComponent::class)
@EntryPoint
interface EasterEggDestinationsEntryPoint {

    var destinations: Set<@JvmSuppressWildcards EasterEggsDestination>
}

@Composable
fun rememberEasterEggsDestinations(): Set<EasterEggsDestination> {
    val context = LocalContext.current.applicationContext
    return remember {
        EntryPointAccessors.fromApplication<EasterEggDestinationsEntryPoint>(context).destinations
    }
}
