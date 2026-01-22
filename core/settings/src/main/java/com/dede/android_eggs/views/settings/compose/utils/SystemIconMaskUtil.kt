package com.dede.android_eggs.views.settings.compose.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import androidx.compose.ui.graphics.Shape
import com.dede.basic.DefType
import com.dede.basic.getIdentifier

object SystemIconMaskUtil {

    private const val CONFIG_ICON_MASK = "config_icon_mask"

    private var isCalledGetSystemIconMaskShape = false
    private var sCachedSystemIconShape: Shape? = null

    fun getIconMaskShape(context: Context): Shape? {
        if (sCachedSystemIconShape != null || isCalledGetSystemIconMaskShape) {
            return sCachedSystemIconShape
        }

        isCalledGetSystemIconMaskShape = true
        val iconMask = getIconMaskPath(context)
        if (iconMask == null || TextUtils.isEmpty(iconMask)) {
            return null
        }
        return PathShape(iconMask).also { sCachedSystemIconShape = it }
    }

    private fun getIconMaskPath(context: Context): String? {
        var pathStr: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val resId = context.getIdentifier(CONFIG_ICON_MASK, DefType.STRING, "android")
            if (resId != Resources.ID_NULL) {
                try {
                    pathStr = context.resources.getString(resId)
                } catch (ignore: Resources.NotFoundException) {
                }
            }
        }
        return pathStr
    }
}
