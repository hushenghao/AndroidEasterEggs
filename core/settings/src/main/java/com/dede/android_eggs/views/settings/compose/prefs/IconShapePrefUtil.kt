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

    @Composable
    fun getIconShape(index: Int = SettingPrefUtil.iconShapeValueState.intValue): Shape {
        val polygon = polygonItems.getOrNull(index)
        return polygon.toShapePlus()
    }

    @Composable
    private fun RoundedPolygon?.toShapePlus(): Shape {
        val shape = this.toShapeNullable()
        if (shape != null) {
            return shape
        }
        return SystemIconMaskUtil.getIconMaskShape(LocalContext.current) ?: defaultCircle.toShape()
    }

    @Composable
    fun RoundedPolygon?.toShapeNullable(): Shape? {
        if (this == null) return null
        if (this == fakeSquircle) return SquircleShape()
        return this.toShape()
    }

    private val defaultCircle = MaterialShapes.Circle

    private val fakeSquircle = RoundedPolygon.rectangle()

    fun providerPolygonItems(): Array<RoundedPolygon> {
        return polygonItems.filterNotNull().filter { it != fakeSquircle }.toTypedArray()
    }

    val polygonItems: Array<RoundedPolygon?> = arrayOf(
        null,
        MaterialShapes.Square,
        // Squircle
        fakeSquircle,
        defaultCircle,
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

        // Triangle
        RoundedPolygon(
            numVertices = 3,
            rounding = CornerRounding(0.2f),
        ).normalized(),
        MaterialShapes.Arch,
        MaterialShapes.Ghostish,
        MaterialShapes.Gem,
        MaterialShapes.Sunny,

        // Hexagon
        RoundedPolygon(
            numVertices = 6,
            rounding = CornerRounding(0.2f),
        ).normalized(),
        MaterialShapes.Flower,
        MaterialShapes.Cookie12Sided,
        MaterialShapes.SoftBurst,
        MaterialShapes.PixelCircle,
    )

}
