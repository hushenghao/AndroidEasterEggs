package com.dede.android_eggs.views.settings.compose.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Path
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.dede.android_eggs.util.PathInflater
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    const val ACTION_CHANGED = "com.dede.easter_eggs.IconShapeChanged"

    fun getSystemIconMaskPath(context: Context): Path? {
        var pathStr: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val resId = getConfigResId(context.resources)
            if (resId != Resources.ID_NULL) {
                try {
                    pathStr = context.resources.getString(resId)
                } catch (ignore: Resources.NotFoundException) {
                }
            }
        }
        if (pathStr == null || TextUtils.isEmpty(pathStr)) {
            return null
        }
        return PathInflater.inflate(pathStr)
    }

    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getConfigResId(resources: Resources): Int {
        return resources.getIdentifier("config_icon_mask", "string", "android")
    }

    fun getIconShapeIndexOf(context: Context): Int {
        return SettingPrefUtil.getValue(context, KEY_ICON_SHAPE, 0)
    }

}
