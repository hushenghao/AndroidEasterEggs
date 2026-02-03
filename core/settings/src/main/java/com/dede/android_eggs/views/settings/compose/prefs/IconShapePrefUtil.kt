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
import com.dede.android_eggs.views.settings.compose.utils.rotated
import sv.lib.squircleshape.SquircleShape

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    @Composable
    fun getIconShape(index: Int = SettingPrefUtil.iconShapeValueState.intValue): Shape {
        val polygon = polygonItems.getOrNull(index)
        val shape = polygon.toShapeWithSystem()
        if (shape != null) {
            return shape
        }
        return SystemIconMaskUtil.getIconMaskShape(LocalContext.current) ?: defaultCircle.toShape()
    }

    @Composable
    fun RoundedPolygon?.toShapeWithSystem(): Shape? {
        if (this == null) return null
        if (this == fakeSquircle) return SquircleShape()
        return this.toShape()
    }

    private val defaultCircle = MaterialShapes.Circle

    private val fakeSquircle = RoundedPolygon.rectangle()

    fun providerPolygonItems(): Array<RoundedPolygon> {
        return polygonItems.filterNotNull().filter { it != fakeSquircle }.toTypedArray()
    }

    private val cornerRound30 = CornerRounding(0.3f)

    private fun scallop(numVertices: Int): RoundedPolygon {
        return RoundedPolygon.star(
            numVerticesPerRadius = numVertices,
            innerRadius = .6f,
            rounding = cornerRound30,
            innerRounding = cornerRound30
        ).normalized()
    }

    private fun polygon(numVertices: Int, rotate: Float = 0f): RoundedPolygon {
        return RoundedPolygon(
            numVertices = numVertices,
            rounding = cornerRound30,
        ).rotated(rotate).normalized()
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

        MaterialShapes.Pill,
        MaterialShapes.Slanted,
        MaterialShapes.Arch,
        MaterialShapes.Ghostish,
        MaterialShapes.Gem,

        MaterialShapes.Cookie4Sided,
        MaterialShapes.Cookie6Sided,
        MaterialShapes.Cookie7Sided,
        MaterialShapes.Cookie9Sided,
        MaterialShapes.Cookie12Sided,

        MaterialShapes.Clover4Leaf,
        MaterialShapes.Clover8Leaf,
        // Scallop
        scallop(8),
        scallop(10),
        scallop(13),

        polygon(4, rotate = 90f),
        polygon(5, rotate = 126f),
        polygon(6),
        polygon(8),
        MaterialShapes.Sunny,

        MaterialShapes.ClamShell,
        MaterialShapes.Bun,
        MaterialShapes.Flower,
        MaterialShapes.PuffyDiamond,
        MaterialShapes.PixelCircle,
    )

}
