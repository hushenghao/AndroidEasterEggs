package com.dede.android_eggs.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import java.lang.ref.WeakReference

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState) {

    companion object {

        private var navigatorRef: WeakReference<Navigator>? = null

        @JvmStatic
        fun findNavigator(): Navigator? {
            return navigatorRef?.get()
        }

        @Composable
        fun rememberNavigator(state: NavigationState): Navigator {
            val navigator = remember { Navigator(state) }
            LaunchedEffect(navigator) {
                navigatorRef = WeakReference(navigator)
            }
            return navigator
        }
    }

    fun navigate(route: NavKey, popUpTo: Boolean = false) {
        if (route in state.backStacks.keys) {
            // This is a top level route, just switch to it.
            state.topLevelRoute = route
        } else {
            val navBackStack = state.backStacks[state.topLevelRoute] ?: return
            if (popUpTo) {
                val navKey = navBackStack.findLast { it == route }
                if (navKey != null) {
                    while (navBackStack.isNotEmpty() && navBackStack.last() != navKey) {
                        navBackStack.removeLastOrNull()
                    }
                } else {
                    navBackStack.add(route)
                }
            } else {
                navBackStack.add(route)
            }
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
