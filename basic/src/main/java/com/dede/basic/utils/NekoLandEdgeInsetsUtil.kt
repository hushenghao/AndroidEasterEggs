package com.dede.basic.utils

import android.app.Activity
import androidx.activity.EdgeToEdgeCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object NekoLandEdgeInsetsUtil {

    @JvmStatic
    fun applyWindowInsets(activity: Activity) {
        ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView) { v, insets ->
            val insetsObj = insets.getInsets(EdgeToEdgeCompat.EDGE_INSETS_MASK)
            v.setPadding(
                insetsObj.left,
                insetsObj.top,
                insetsObj.right,
                insetsObj.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}
