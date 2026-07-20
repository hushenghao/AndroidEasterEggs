@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.utils.SystemIconMaskUtil
import com.dede.android_eggs.views.settings.compose.utils.rotated
import sv.lib.squircleshape.SquircleShape
import kotlin.random.Random

@Stable
sealed interface ShapeSpec {
    @Stable
    data class Polygon(val polygon: RoundedPolygon) : ShapeSpec
    data object System : ShapeSpec
    data object Squircle : ShapeSpec
    data object Random : ShapeSpec
}

@Composable
fun ShapeSpec.toShape(): Shape = when (this) {
    is ShapeSpec.Polygon -> polygon.toShape()
    is ShapeSpec.System -> {
        SystemIconMaskUtil.getIconMaskShape(LocalContext.current)
            ?: MaterialShapes.Circle.toShape()
    }
    is ShapeSpec.Squircle -> squircleShape
    is ShapeSpec.Random -> {
        val index = remember { Random.nextInt(IconShapePrefUtil.indexOfRandom) }
        IconShapePrefUtil.getIconShape(index)
    }
}

private val squircleShape = SquircleShape()

private val circleShapeSpec = ShapeSpec.Polygon(MaterialShapes.Circle)

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    @Composable
    fun getIconShape(index: Int = SettingPrefUtil.iconShapeValueState.intValue): Shape {
        return shapeSpecs.getOrElse(index) { circleShapeSpec }.toShape()
    }

    fun providerPolygonItems(): Array<RoundedPolygon> {
        return shapeSpecs.filterIsInstance<ShapeSpec.Polygon>().map { it.polygon }.toTypedArray()
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

    val shapeSpecs: List<ShapeSpec> = [
        ShapeSpec.System,
        ShapeSpec.Polygon(MaterialShapes.Square),
        // Squircle
        ShapeSpec.Squircle,
        circleShapeSpec,
        // CornerSE
        ShapeSpec.Polygon(
            RoundedPolygon(
                vertices = floatArrayOf(1f, 1f, -1f, 1f, -1f, -1f, 1f, -1f),
                perVertexRounding = listOf(
                    CornerRounding(0.4f),
                    CornerRounding(1f),
                    CornerRounding(1f),
                    CornerRounding(1f),
                ),
            ).normalized()
        ),

        ShapeSpec.Polygon(MaterialShapes.Pill),
        ShapeSpec.Polygon(MaterialShapes.Slanted),
        ShapeSpec.Polygon(MaterialShapes.Arch),
        ShapeSpec.Polygon(MaterialShapes.Ghostish),
        ShapeSpec.Polygon(MaterialShapes.Gem),

        ShapeSpec.Polygon(MaterialShapes.Cookie4Sided),
        ShapeSpec.Polygon(MaterialShapes.Cookie6Sided),
        ShapeSpec.Polygon(MaterialShapes.Cookie7Sided),
        ShapeSpec.Polygon(MaterialShapes.Cookie9Sided),
        ShapeSpec.Polygon(MaterialShapes.Cookie12Sided),

        ShapeSpec.Polygon(MaterialShapes.Clover4Leaf),
        ShapeSpec.Polygon(cloverLeaf(6, -60f)),
        ShapeSpec.Polygon(MaterialShapes.Clover8Leaf),
        // Scallop
        ShapeSpec.Polygon(scallop(8)),
        ShapeSpec.Polygon(scallop(10)),
        ShapeSpec.Polygon(scallop(13)),

        ShapeSpec.Polygon(polygon(4, rotate = 90f)),
        ShapeSpec.Polygon(polygon(5, rotate = 126f)),
        ShapeSpec.Polygon(polygon(6)),
        ShapeSpec.Polygon(polygon(8)),
        ShapeSpec.Polygon(MaterialShapes.Sunny),

        ShapeSpec.Polygon(MaterialShapes.ClamShell),
        ShapeSpec.Polygon(MaterialShapes.Bun),
        ShapeSpec.Polygon(MaterialShapes.Flower),
        ShapeSpec.Polygon(MaterialShapes.PuffyDiamond),
        ShapeSpec.Polygon(MaterialShapes.PixelCircle),

        ShapeSpec.Polygon(
            RoundedPolygon.star(
                numVerticesPerRadius = 17,// android 17
                innerRadius = 0.88f,
                rounding = CornerRounding(0.08f),
                innerRounding = CornerRounding.Unrounded,
            ).rotated(17f).normalized()
        ),

        ShapeSpec.Random
    ]

    internal val indexOfRandom: Int = shapeSpecs.size - 1
}
