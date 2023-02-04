package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.view.Window
import com.google.android.material.internal.EdgeToEdgeUtils

object WindowEdgeUtilsAccessor {

    @SuppressLint("RestrictedApi")
    fun applyEdgeToEdge(window: Window, edgeToEdgeEnabled: Boolean) {
        EdgeToEdgeUtils.applyEdgeToEdge(window, edgeToEdgeEnabled)
    }
}