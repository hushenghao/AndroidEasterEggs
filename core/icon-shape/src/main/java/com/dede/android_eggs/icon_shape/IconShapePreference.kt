package com.dede.android_eggs.icon_shape

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import com.dede.android_eggs.preferences.AppSettings
import com.dede.basic.globalContext

/**
 * The shape the user picked for icon masking, held as Compose state so every icon
 * that reads it re-renders as soon as the picker changes it.
 *
 * This is the only live instance for [AppSettings.iconShape]. `PrefMutableState` in
 * `:core:settings-ui` would do the same job, but that module depends on this one, so
 * the state has to live on this side of the boundary.
 */
object IconShapePreference {

    val selectedIndex: MutableIntState = PrefSelectedIndex()

    private class PrefSelectedIndex : MutableIntState {

        private val delegate = mutableIntStateOf(AppSettings.iconShape.get(globalContext))

        override var intValue: Int
            get() = delegate.intValue
            set(value) {
                delegate.intValue = value
                AppSettings.iconShape.set(globalContext, value)
            }

        override fun component1(): Int {
            return delegate.component1()
        }

        override fun component2(): (Int) -> Unit {
            return delegate.component2()
        }
    }
}
