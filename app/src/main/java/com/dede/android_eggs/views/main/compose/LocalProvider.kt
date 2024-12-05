@file:Suppress("NOTHING_TO_INLINE")

package com.dede.android_eggs.views.main.compose

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalInspectionMode
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert

private inline fun noLocalProvidedFor(name: String): Nothing {
    throw IllegalStateException("CompositionLocal %s not present".format(name))
}

private const val TAG = "LocalProvider"

private inline fun noLocalProvidedForLog(name: String) {
    Log.i(TAG, "CompositionLocal %s not present".format(name))
}

val <T> ProvidableCompositionLocal<T>.currentOutInspectionMode: T?
    @ReadOnlyComposable
    @Composable
    get() = if (LocalInspectionMode.current) null else current

val LocalEasterEggLogoSensor = staticCompositionLocalOf<EasterEggLogoSensorMatrixConvert> {
    noLocalProvidedFor("LocalEasterEggLogoSensor")
}

val LocalKonfettiState = staticCompositionLocalOf {
    noLocalProvidedForLog("LocalKonfettiState")
    mutableStateOf(false)
}

