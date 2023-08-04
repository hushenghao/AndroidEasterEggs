package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.view.Window
import com.google.android.material.internal.EdgeToEdgeUtils


object EdgeUtils {

    @SuppressLint("RestrictedApi")
    fun applyEdge(window: Window?) {
        if (window == null) return
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)
    }

}