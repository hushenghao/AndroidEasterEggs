@file:Suppress("NOTHING_TO_INLINE")

package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.view.View
import android.view.Window
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.InsetsType
import com.google.android.material.internal.EdgeToEdgeUtils


object EdgeUtils {

    @SuppressLint("RestrictedApi")
    fun applyEdge(window: Window?) {
        if (window == null) return
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)
    }

    val DEFAULT_EDGE_MASK =
        WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()

    inline fun <T : View> T.onApplyWindowEdge(
        @InsetsType typeMask: Int = DEFAULT_EDGE_MASK,
        noinline onApplyWindowEdge: T.(edge: Insets) -> Unit,
    ) {
        this.onApplyWindowInsets {
            onApplyWindowEdge(this, it.getInsets(typeMask))
        }
    }

    inline fun <T : View> T.onApplyWindowInsets(noinline onApplyWindowInsets: T.(insets: WindowInsetsCompat) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(this, OnApplyWindowInsetsListener { v, insets ->
            @Suppress("UNCHECKED_CAST")
            onApplyWindowInsets(v as T, insets)
            return@OnApplyWindowInsetsListener insets
        })
    }

}