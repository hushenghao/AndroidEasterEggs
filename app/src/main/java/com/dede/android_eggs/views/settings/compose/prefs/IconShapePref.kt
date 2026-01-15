@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.star
import com.dede.android_eggs.R
import com.dede.android_eggs.alterable_adaptive_icon.PathShape
import com.dede.android_eggs.ui.composes.icons.rounded.Shapes
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefIntState
import sv.lib.squircleshape.SquircleShape
import com.dede.android_eggs.resources.R as StringsR

private const val SPAN_COUNT = 5

@Preview
@Composable
fun IconShapePref() {
    var selectedIndex by rememberPrefIntState(IconShapePrefUtil.KEY_ICON_SHAPE, 0)
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Shapes,
        title = stringResource(StringsR.string.pref_title_icon_shape_override),
    ) {
        IconShapeGridLayout {
            polygonItems.forEachIndexed { index, roundedPolygon ->
                Box(modifier = Modifier.padding(4.dp)) {
                    ShapeItem(
                        isChecked = index == selectedIndex,
                        polygon = roundedPolygon.toShapePlusNullable(),
                        onClick = onClick@{
                            if (selectedIndex == index) return@onClick
                            selectedIndex = index
                            val extras = bundleOf(SettingPrefUtil.EXTRA_VALUE to index)
                            with(LocalEvent.poster()) {
                                post(IconShapePrefUtil.ACTION_CHANGED, extras)
                                post(SettingPrefUtil.ACTION_CLOSE_SETTING)
                            }
                        }
                    )
                }
            }
        }
    }
}

fun getIconShapeRoundedPolygon(context: Context): RoundedPolygon? {
    val index = IconShapePrefUtil.getIconShapeIndexOf(context)
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
    if (shape == null) {
        val path = IconShapePrefUtil.getSystemIconMaskPath(LocalContext.current)
        if (path != null) {
            return PathShape(path)
        }
        return defaultSquare.toShape()
    }
    return shape
}

@Composable
private fun RoundedPolygon?.toShapePlusNullable(): Shape? {
    if (this == null) return null
    if (this == _fakeSquircle) return SquircleShape()
    return this.toShape()
}

private val defaultSquare = MaterialShapes.Square

@Suppress("ObjectPropertyName")
private val _fakeSquircle = RoundedPolygon.rectangle()

private val polygonItems: Array<RoundedPolygon?> = arrayOf(
    null,
    defaultSquare,
    // Squircle
    _fakeSquircle,
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

@Composable
private fun IconShapeGridLayout(spanCount: Int = SPAN_COUNT, content: @Composable () -> Unit) {
    Layout(
        content = content,
        measurePolicy = { measurables, constraints ->
            val childConstraints = Constraints.fixedWidth(constraints.maxWidth / spanCount)
            var height = 0
            val placeables = measurables.mapIndexed { index, measurable ->
                measurable.measure(childConstraints).also { placeable ->
                    if (index % spanCount == 0) {
                        height += placeable.height
                    }
                }
            }
            layout(constraints.maxWidth, height) {
                var x: Int
                var y: Int
                placeables.forEachIndexed { index, placeable ->
                    x = index % spanCount * placeable.width
                    y = index / spanCount * placeable.height
                    placeable.placeRelative(x, y)
                }
            }
        }
    )
}

@Composable
private fun ShapeItem(
    modifier: Modifier = Modifier,
    isChecked: Boolean = false,
    polygon: Shape? = MaterialShapes.Circle.toShape(),
    onClick: () -> Unit = {}
) {
    Card(
        shape = MaterialShapes.Cookie4Sided.toShape(),
        onClick = onClick,
        modifier = modifier then Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (polygon == null) {
                Image(
                    painter = painterResource(R.drawable.ic_android_classic),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize(0.56f),
                )
            } else {
                Box(
                    modifier = Modifier
                        .clip(polygon)
                        .background(colorScheme.primary)
                        .fillMaxSize(0.56f)
                )
            }
            if (isChecked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.36f)
                    )
                }
            }
        }
    }
}
