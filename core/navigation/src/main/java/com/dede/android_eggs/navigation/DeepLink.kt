package com.dede.android_eggs.navigation

import androidx.navigation3.runtime.NavKey

private object PendingDeepLinkStore {
    private var navKey: NavKey? = null

    @Synchronized
    fun set(route: NavKey) {
        navKey = route
    }

    @Synchronized
    fun peek(): NavKey? = navKey

    @Synchronized
    fun consume(): NavKey? {
        val pending = navKey
        navKey = null
        return pending
    }

    @Synchronized
    fun clear() {
        navKey = null
    }
}

object DeepLink {

    fun setNavKey(route: NavKey) {
        PendingDeepLinkStore.set(route)
    }

    fun peekNavKey(): NavKey? {
        return PendingDeepLinkStore.peek()
    }

    fun clearNavKey() {
        PendingDeepLinkStore.clear()
    }

    fun handleNavKey(navigator: Navigator): Boolean {
        val route = PendingDeepLinkStore.consume() ?: return false
        navigator.navigate(route)
        return true
    }
}
