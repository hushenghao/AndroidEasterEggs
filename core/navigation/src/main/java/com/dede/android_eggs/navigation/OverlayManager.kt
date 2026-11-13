package com.dede.android_eggs.navigation

import androidx.compose.runtime.compositionLocalOf
import com.dede.android_eggs.local_provider.noLocalProvidedFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayManager @Inject constructor() {

    private val _currentRoute = MutableStateFlow<OverlayRoute?>(null as OverlayRoute?)
    val currentRoute: StateFlow<OverlayRoute?> = _currentRoute.asStateFlow()

    /**
     * 展示一个 Dialog/BottomSheet，并挂起直到被关闭。
     *
     * 适用于启动流程编排等需要顺序执行场景。
     */
    suspend fun awaitDialog(route: OverlayRoute) {
        if (_currentRoute.value != null) {
            _currentRoute.first { it == null }
        }
        _currentRoute.value = route
        _currentRoute.first { it == null }
    }

    /** 展示一个 Dialog/BottomSheet，不等待关闭（fire-and-forget）。 */
    fun show(route: OverlayRoute) {
        _currentRoute.value = route
    }

    /** 关闭当前 Dialog/BottomSheet。 */
    fun dismiss() {
        _currentRoute.value = null
    }
}

val LocalOverlayManager = compositionLocalOf<OverlayManager> {
    noLocalProvidedFor("LocalOverlayManager")
}
