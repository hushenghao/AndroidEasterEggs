@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

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
import com.dede.android_eggs.views.settings.compose.utils.SystemIconMaskUtil
import sv.lib.squircleshape.SquircleShape

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

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
        return SystemIconMaskUtil.getIconMaskShape(LocalContext.current) ?: defaultSquare.toShape()
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
