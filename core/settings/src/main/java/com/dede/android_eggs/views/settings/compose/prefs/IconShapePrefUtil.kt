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
import kotlin.random.Random

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    @Composable
    fun getIconShape(index: Int = SettingPrefUtil.iconShapeValueState.intValue): Shape {
        val polygon = polygonItems.getOrNull(index) ?: defaultCircle
        return polygon.toShapePlus()
    }

    fun RoundedPolygon.isSystemShape(): Boolean = this === systemShape

    fun RoundedPolygon.isRandomPolygon(): Boolean = this === randomPolygon

    internal fun RoundedPolygon.isSquircle(): Boolean = this === fakeSquircle

    @Composable
    fun RoundedPolygon.toShapePlus(): Shape {
        if (this.isRandomPolygon()) {
            val index = Random.nextInt(indexOfRandom)
            return getIconShape(index)
        }
        if (this.isSystemShape()) {
            return SystemIconMaskUtil.getIconMaskShape(LocalContext.current)
                ?: defaultCircle.toShape()
        }
        if (this.isSquircle()) {
            return SquircleShape()
        }
        return this.toShape()
    }

    private val defaultCircle = MaterialShapes.Circle

    private val fakeSquircle = RoundedPolygon.rectangle()
    private val systemShape = RoundedPolygon.rectangle()
    private val randomPolygon = RoundedPolygon.rectangle()

    private val privatePolygonSet = setOf(systemShape, fakeSquircle, randomPolygon)

    fun providerPolygonItems(): Array<RoundedPolygon> {
        return polygonItems.filter { !privatePolygonSet.contains(it) }.toTypedArray()
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

    private fun cloverLeaf(numVertices: Int, rotate: Float = 0f): RoundedPolygon {
        return RoundedPolygon.star(
            numVerticesPerRadius = numVertices,
            innerRadius = .5f,
            rounding = CornerRounding(1f),
            innerRounding = CornerRounding.Unrounded,
            centerY = 0.5f,
            centerX = 0.5f,
        ).rotated(rotate).normalized()
    }

    private fun polygon(numVertices: Int, rotate: Float = 0f): RoundedPolygon {
        return RoundedPolygon(
            numVertices = numVertices,
            rounding = cornerRound30,
        ).rotated(rotate).normalized()
    }

    val polygonItems: Array<RoundedPolygon> = arrayOf(
        systemShape,
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
        cloverLeaf(6, -60f),
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

        RoundedPolygon.star(
            numVerticesPerRadius = 17,// android 17
            innerRadius = 0.88f,
            rounding = CornerRounding(0.08f),
            innerRounding = CornerRounding.Unrounded,
        ).rotated(17f).normalized(),

        randomPolygon
    )

    internal val indexOfRandom: Int = polygonItems.size - 1

}
