@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.star
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.utils.PathShape
import sv.lib.squircleshape.SquircleShape

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    const val ACTION_CHANGED = "com.dede.easter_eggs.IconShapeChanged"

    private fun getSystemIconMaskShape(context: Context): Shape? {
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
        return PathShape(pathStr)
    }

    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getConfigResId(resources: Resources): Int {
        return resources.getIdentifier("config_icon_mask", "string", "android")
    }

    fun getIconShapeRoundedPolygon(context: Context): RoundedPolygon? {
        val index = SettingPrefUtil.getValue(context, KEY_ICON_SHAPE, 0)
        return polygonItems.getOrNull(index)
    }

    @Composable
    fun getIconShapePref(): Shape {
        val roundedPolygon = getIconShapeRoundedPolygon(LocalContext.current)
        return roundedPolygon.toShapePlus()
    }

    @Composable
    fun RoundedPolygon?.toShapePlus(): Shape {
        val shape = this.toShapePlusNullable()
        if (shape != null) {
            return shape
        }
        return getSystemIconMaskShape(LocalContext.current) ?: defaultSquare.toShape()
    }


    @Composable
    fun RoundedPolygon?.toShapePlusNullable(): Shape? {
        if (this == null) return null
        if (this == fakeSquircle) return SquircleShape()
        return this.toShape()
    }

    private val defaultSquare = MaterialShapes.Square

    private val fakeSquircle = RoundedPolygon.rectangle()

    val polygonItems: Array<RoundedPolygon?> = arrayOf(
        null,
        defaultSquare,
        // Squircle
        fakeSquircle,
        MaterialShapes.Circle,
        // CornerSE
        RoundedPolygon(
            vertices = floatArrayOf(1f, 1f, -1f, 1f, -1f, -1f, 1f, -1f),
            perVertexRounding = listOf(
                CornerRounding(0.4f),
                CornerRounding(1f),
                CornerRounding(1f),
                CornerRounding(1f),
            ),
        ).normalized(),

        MaterialShapes.Cookie4Sided,
        // Scallop
        RoundedPolygon.star(
            numVerticesPerRadius = 13,
            innerRadius = .9f,
            rounding = CornerRounding(.2f),
            innerRounding = CornerRounding(.3f)
        ).normalized(),
        MaterialShapes.Clover8Leaf,
        MaterialShapes.Pill,
        RoundedPolygon.star(
            numVerticesPerRadius = 10,
            innerRadius = .6f,
            rounding = CornerRounding(.3f),
            innerRounding = CornerRounding(.3f)
        ).normalized(),
    )

}
