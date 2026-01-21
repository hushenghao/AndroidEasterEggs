@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
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
import com.dede.basic.DefType
import com.dede.basic.getIdentifier
import sv.lib.squircleshape.SquircleShape

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    private var sCachedSystemIconShape: Shape? = null

    private fun getSystemIconMaskShape(context: Context): Shape? {
        if (sCachedSystemIconShape != null) {
            return sCachedSystemIconShape
        }
        var pathStr: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val resId = context.getIdentifier("config_icon_mask", DefType.STRING, "android")
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
        return PathShape(pathStr).also { sCachedSystemIconShape = it }
    }

    fun getIconShapeRoundedPolygon(index: Int): RoundedPolygon? {
        return polygonItems.getOrNull(index)
    }

    @Composable
    fun getIconShapePref(): Shape {
        return getIconShapeRoundedPolygon(SettingPrefUtil.iconShapeValueState.value).toShapePlus()
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
