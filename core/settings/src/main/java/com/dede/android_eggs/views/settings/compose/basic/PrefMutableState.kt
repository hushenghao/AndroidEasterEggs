package com.dede.android_eggs.views.settings.compose.basic

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.dede.android_eggs.util.pref

@Composable
fun rememberPrefColorState(key: String, default: Color): MutableState<Color> {
    val context = LocalContext.current
    return remember { PrefMutableColorState(context, key, default) }
}

@Composable
fun rememberPrefIntState(key: String, default: Int): MutableIntState {
    val context = LocalContext.current
    return remember { PrefMutableIntState(context, key, default) }
}

@Composable
fun rememberPrefBoolState(key: String, default: Boolean): MutableState<Boolean> {
    val context = LocalContext.current
    return remember { PrefMutableBooleanState(context, key, default) }
}

private class PrefMutableBooleanState(
    val context: Context,
    val key: String,
    default: Boolean,
) : MutableState<Boolean> {

    private val delegate = mutableStateOf(context.pref.getBoolean(key, default))

    override var value: Boolean
        get() = delegate.value
        set(value) {
            delegate.value = value
            context.pref.edit { putBoolean(key, value) }
        }

    override fun component1(): Boolean {
        return delegate.component1()
    }

    override fun component2(): (Boolean) -> Unit {
        return delegate.component2()
    }

}

private class PrefMutableColorState(
    val context: Context,
    val key: String,
    default: Color,
) : MutableState<Color> {

    private val delegate = mutableStateOf(
        Color(context.pref.getInt(key, default.toArgb()))
    )

    override var value: Color
        get() {
            return delegate.value
        }
        set(value) {
            delegate.value = value
            context.pref.edit { putInt(key, value.toArgb()) }
        }

    override fun component1(): Color {
        return delegate.component1()
    }

    override fun component2(): (Color) -> Unit {
        return delegate.component2()
    }
}

private class PrefMutableIntState(
    val context: Context,
    val key: String,
    default: Int,
) : MutableIntState {

    private val delegate = mutableIntStateOf(context.pref.getInt(key, default))

    override var intValue: Int
        get() {
            return delegate.intValue
        }
        set(value) {
            delegate.intValue = value
            context.pref.edit { putInt(key, value) }
        }

    override fun component1(): Int {
        return delegate.component1()
    }

    override fun component2(): (Int) -> Unit {
        return delegate.component2()
    }
}
