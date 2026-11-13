package com.dede.android_eggs.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

interface EasterEggsDestination {

    val route: NavKey

    @Composable
    fun Content(route: NavKey) {
    }

    interface Provider {
        fun provider(): EasterEggsDestination
    }

    // @formatter:off
    @Serializable data object EasterEggs : NavKey
    @Serializable data class CatEditor(val seed: Long? = null) : NavKey
    @Serializable data object LibrariesInfo : NavKey
    // @formatter:on
}
