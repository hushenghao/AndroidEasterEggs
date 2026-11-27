package com.dede.android_eggs.settings_state

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.preferences.AppSettingsSnapshot
import com.dede.android_eggs.preferences.PrefKey

private val LocalAppSettingsSnapshot = compositionLocalOf<State<AppSettingsSnapshot>?> { null }

/**
 * Provides a single shared [AppSettingsSnapshot] subscription for the subtree so all
 * [rememberPrefValue] and `rememberPref*State` readers derive their key via [derivedStateOf]
 * instead of spawning per-call DataStore collectors.
 */
@Composable
fun ProvideAppSettingsState(content: @Composable () -> Unit) {
    if (LocalInspectionMode.current || LocalAppSettingsSnapshot.current != null) {
        content()
        return
    }
    val appContext = LocalContext.current.applicationContext
    val initial = remember(appContext) { AppSettings.snapshot(appContext) }
    val flow = remember(appContext) { AppSettings.snapshotFlow(appContext) }
    val snapshotState = flow.collectAsState(initial = initial)
    CompositionLocalProvider(
        LocalAppSettingsSnapshot provides snapshotState,
        content = content,
    )
}

/**
 * Read-only reactive preference value. Uses the shared [ProvideAppSettingsState] snapshot
 * when available and avoids allocating a [MutableState] wrapper.
 */
@Composable
fun <T> rememberPrefValue(key: PrefKey<T>, default: T = key.default): T {
    if (LocalInspectionMode.current) {
        return default
    }
    val appContext = LocalContext.current.applicationContext
    return rememberPrefFlowState(appContext, key, default).value
}

/**
 * [default] overrides the value declared on [key] for settings whose default
 * depends on device capabilities, see `ColorSourcePrefUtil.DEFAULT_SOURCE`.
 */
@Composable
fun rememberPrefIntState(key: PrefKey<Int>, default: Int = key.default): MutableIntState {
    if (LocalInspectionMode.current) {
        return remember(key, default) { mutableIntStateOf(default) }
    }
    val appContext = LocalContext.current.applicationContext
    val state = rememberPrefFlowState(appContext, key, default)
    return remember(appContext, key, state) {
        object : MutableIntState {
            override var intValue: Int
                get() = state.value
                set(value) {
                    if (state.value != value) {
                        key.set(appContext, value)
                    }
                }

            override fun component1(): Int = intValue
            override fun component2(): (Int) -> Unit = { intValue = it }
        }
    }
}

@Composable
fun rememberPrefBoolState(key: PrefKey<Boolean>): MutableState<Boolean> {
    return rememberMappedPrefState(
        key = key,
        default = key.default,
        toUi = { it },
        toPref = { it },
    )
}

@Composable
fun rememberPrefColorState(key: PrefKey<Int>): MutableState<Color> {
    return rememberMappedPrefState(
        key = key,
        default = key.default,
        toUi = { Color(it) },
        toPref = { it.toArgb() },
    )
}

@Composable
private fun <S, T> rememberMappedPrefState(
    key: PrefKey<S>,
    default: S,
    toUi: (S) -> T,
    toPref: (T) -> S,
): MutableState<T> {
    if (LocalInspectionMode.current) {
        return remember(key, default) { mutableStateOf(toUi(default)) }
    }
    val appContext = LocalContext.current.applicationContext
    val state = rememberPrefFlowState(appContext, key, default)
    return remember(appContext, key, state) {
        object : MutableState<T> {
            override var value: T
                get() = toUi(state.value)
                set(value) {
                    val raw = toPref(value)
                    if (state.value != raw) {
                        key.set(appContext, raw)
                    }
                }

            override fun component1(): T = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

@Composable
private fun <T> rememberPrefFlowState(
    appContext: Context,
    key: PrefKey<T>,
    default: T,
): State<T> {
    val providedSnapshot = LocalAppSettingsSnapshot.current
    if (providedSnapshot != null) {
        return remember(providedSnapshot, key, default) {
            derivedStateOf { providedSnapshot.value.get(key, default) }
        }
    }
    val initial = remember(appContext, key, default) {
        if (key.isSet(appContext)) key.get(appContext) else default
    }
    val flow = remember(appContext, key, default) {
        key.flow(appContext, default)
    }
    return flow.collectAsState(initial = initial)
}
