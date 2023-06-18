package com.dede.android_eggs.startup

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import me.weishu.reflection.Reflection

class ReflectionInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val unseal = Reflection.unseal(context)
        if (unseal != 0) {
            Log.w("Reflection", "unseal fail: $unseal")
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

}