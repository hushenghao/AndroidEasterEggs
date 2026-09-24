@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalGridApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.icon_shape.IconShapePrefUtil.shapeSpecs
import com.dede.android_eggs.icon_shape.IconShapePreference
import com.dede.android_eggs.icon_shape.ShapeSpec
import com.dede.android_eggs.icon_shape.toShape
import com.dede.android_eggs.settings_ui.basic.ExpandOptionsPref
import com.dede.android_eggs.ui.composes.icons.rounded.Shapes
import com.dede.android_eggs.resources.R as StringsR

private const val SPAN_COUNT = 5

@Preview
@Composable
fun IconShapePref() {
    var selectedIndex by IconShapePreference.selectedIndex
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Shapes,
        title = stringResource(StringsR.string.pref_title_icon_shape_override),
    ) {
        // Percentage tracks: fr floors each track at min-content (a 48dp icon button), and
        // 5 tracks plus gaps can overflow the narrower end pane; percentage divides the
        // available width exactly instead.
        Grid(
            config = {
                repeat(SPAN_COUNT) { column(1f / SPAN_COUNT) }
                gap(8.dp)
            },
        ) {
            shapeSpecs.forEachIndexed { index, shapeSpec ->
                ShapeItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    isSelected = index == selectedIndex,
                    shapeSpec = shapeSpec,
                    onClick = onClick@{
                        if (selectedIndex == index) return@onClick
                        selectedIndex = index
                    }
                )
            }
        }
    }
}

@Composable
private fun ShapeItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    shapeSpec: ShapeSpec = ShapeSpec.Polygon(MaterialShapes.Circle),
    onClick: () -> Unit = {},
) {
    val shape = shapeSpec.toShape()
    FilledTonalIconButton(
        shape = shape,
        onClick = onClick,
        modifier = modifier,
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
                        .size(24.dp), // MaterialIconDimension = 24f
                )
            }
        }
    }
}
