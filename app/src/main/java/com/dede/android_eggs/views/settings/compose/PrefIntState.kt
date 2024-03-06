package com.dede.android_eggs.views.settings.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.dede.android_eggs.util.pref

@Composable
fun rememberPrefIntState(key: String, default: Int): MutableIntState {
    val context = LocalContext.current
    return remember { PrefMutableIntState(context, key, default) }
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
            context.pref.edit().putInt(key, value).apply()
        }

    override fun component1(): Int {
        return delegate.component1()
    }

    override fun component2(): (Int) -> Unit {
        return delegate.component2()
    }
}