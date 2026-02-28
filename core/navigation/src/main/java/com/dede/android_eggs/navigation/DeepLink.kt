package com.dede.android_eggs.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavKey

object DeepLink {

    internal val deeplinkNavKey: MutableState<NavKey?> = mutableStateOf(null)

    fun setNavKey(route: NavKey) {
        deeplinkNavKey.value = route
    }

    fun handleNavKey(navigator: Navigator) {
        navigator.navigate(deeplinkNavKey.value ?: return)
        deeplinkNavKey.value = null
    }
}
