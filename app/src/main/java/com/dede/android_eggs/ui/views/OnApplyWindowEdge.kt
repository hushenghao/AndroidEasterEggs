package com.dede.android_eggs.ui.views

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


fun <T : View> T.onApplyWindowEdge(
    typeMask: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
    onApplyWindowEdge: T.(edge: Insets) -> Unit,
) {
    ViewCompat.setOnApplyWindowInsetsListener(this, OnApplyWindowInsetsListener { v, insets ->
        @Suppress("UNCHECKED_CAST")
        onApplyWindowEdge(v as T, insets.getInsets(typeMask))
        return@OnApplyWindowInsetsListener insets
    })
}