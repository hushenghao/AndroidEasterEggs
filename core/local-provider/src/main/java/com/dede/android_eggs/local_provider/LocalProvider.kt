@file:Suppress("NOTHING_TO_INLINE")

package com.dede.android_eggs.local_provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalInspectionMode

inline fun noLocalProvidedFor(name: String): Nothing {
    throw IllegalStateException("CompositionLocal %s not present".format(name))
}

val <T> ProvidableCompositionLocal<T>.currentOutInspectionMode: T?
    @ReadOnlyComposable
    @Composable
    get() = if (LocalInspectionMode.current) null else current
