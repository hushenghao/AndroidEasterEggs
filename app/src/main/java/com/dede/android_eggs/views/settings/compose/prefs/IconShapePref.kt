@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.ui.composes.icons.rounded.Shapes
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefIntState
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil.shapeSpecs
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
            shapeSpecs.forEachIndexed { index, shapeSpec ->
                ShapeItem(
                    modifier = Modifier.padding(4.dp),
                    isSelected = index == selectedIndex,
                    shapeSpec = shapeSpec,
                    onClick = onClick@{
                        if (selectedIndex == index) return@onClick
                        selectedIndex = index
                        SettingPrefUtil.iconShapeValueState.intValue = index
                    }
                )
            }
        }
    }
}

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
    isSelected: Boolean = false,
    shapeSpec: ShapeSpec = ShapeSpec.Polygon(MaterialShapes.Circle),
    onClick: () -> Unit = {}
) {
    val shape = shapeSpec.toShape()
    FilledTonalIconButton(
        shape = shape,
        onClick = onClick,
        modifier = modifier then Modifier.aspectRatio(1f),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (isSelected) colorScheme.primaryContainer else colorScheme.surface,
            contentColor = colorScheme.onSurface,
        ),
    ) {
        val shapeColor = colorScheme.onSurfaceVariant
        when (shapeSpec) {
            is ShapeSpec.Random -> {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    contentDescription = null,
                    tint = shapeColor,
                )
            }
            is ShapeSpec.System -> {
                Icon(
                    imageVector = Icons.Rounded.Android,
                    contentDescription = null,
                    tint = shapeColor,
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .border(1.6.dp, shapeColor, shape)
                        .size(24.dp)
                )
            }
        }
    }
}
