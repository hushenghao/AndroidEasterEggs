package com.dede.android_eggs.views.settings.compose.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.dede.android_eggs.settings.R
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    const val ACTION_CHANGED = "com.dede.easter_eggs.IconShapeChanged"

    fun getSystemMaskPath(context: Context): String {
        var pathStr = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val resId = getConfigResId(context.resources)
            if (resId != Resources.ID_NULL) {
                pathStr = context.resources.getString(resId)
            }
        }
        if (TextUtils.isEmpty(pathStr)) {
            pathStr = context.resources.getString(R.string.icon_shape_circle_path)
        }
        return pathStr
    }

    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getConfigResId(resources: Resources): Int {
        return resources.getIdentifier("config_icon_mask", "string", "android")
    }

    fun getMaskPath(context: Context): String {
        val index = SettingPrefUtil.getValue(context, KEY_ICON_SHAPE, 0)
        var path = getMaskPathByIndex(context, index)
        if (path.isEmpty()) {
            path = getSystemMaskPath(context)
        }
        return path
    }

    private fun getMaskPathByIndex(context: Context, index: Int): String {
        val paths = context.resources.getStringArray(R.array.icon_shape_override_paths)
        if (index < 0 || index > paths.size - 1) {
            return ""
        }
        return paths[index]
    }

}
