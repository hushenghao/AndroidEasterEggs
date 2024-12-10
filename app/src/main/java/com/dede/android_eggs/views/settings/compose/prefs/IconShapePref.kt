package com.dede.android_eggs.views.settings.compose.prefs

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.composes.icons.rounded.Shapes
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.compose.PathShape
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefIntState
import com.dede.android_eggs.resources.R as StringsR
import com.dede.android_eggs.settings.R as SettingsR

private const val SPAN_COUNT = 5

@Preview
@Composable
fun IconShapePref() {
    var selectedIndex by rememberPrefIntState(IconShapePrefUtil.KEY_ICON_SHAPE, 0)
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Shapes,
        title = stringResource(StringsR.string.pref_title_icon_shape_override),
    ) {
        IconShapeGroup(selectedIndex) { index, path ->
            selectedIndex = index
            val extras = bundleOf(SettingPrefUtil.EXTRA_VALUE to path)
            with(LocalEvent.poster()) {
                post(IconShapePrefUtil.ACTION_CHANGED, extras)
                post(SettingPrefUtil.ACTION_CLOSE_SETTING)
            }
        }
    }
}

@Preview
@Composable
private fun IconShapeGroup(
    selectedIndex: Int = 0,
    onShapeClick: ((index: Int, path: String) -> Unit)? = null
) {
    val items = stringArrayResource(SettingsR.array.icon_shape_override_paths)
    Layout(
        content = {
            items.forEachIndexed { index, path ->
                Box(modifier = Modifier.padding(4.dp)) {
                    ShapeItem(
                        isChecked = index == selectedIndex,
                        path = path,
                        onClick = onClick@{
                            if (selectedIndex == index) return@onClick
                            onShapeClick?.invoke(index, path)
                        }
                    )
                }
            }
        },
        measurePolicy = { measurables, constraints ->
            val childConstraints = Constraints.fixedWidth(constraints.maxWidth / SPAN_COUNT)
            var height = 0
            val placeables = measurables.mapIndexed { index, measurable ->
                measurable.measure(childConstraints).also { placeable ->
                    if (index % SPAN_COUNT == 0) {
                        height += placeable.height
                    }
                }
            }
            layout(constraints.maxWidth, height) {
                var x: Int
                var y: Int
                placeables.forEachIndexed { index, placeable ->
                    x = index % SPAN_COUNT * placeable.width
                    y = index / SPAN_COUNT * placeable.height
                    placeable.placeRelative(x, y)
                }
            }
        }
    )
}

@Preview(widthDp = 56)
@Composable
private fun ShapeItem(
    modifier: Modifier = Modifier,
    isChecked: Boolean = false,
    path: String = stringResource(id = SettingsR.string.icon_shape_clover_path),
    onClick: () -> Unit = {}
) {
    Card(
        shape = PathShape(stringResource(SettingsR.string.icon_shape_clover_path)),
        onClick = onClick,
        modifier = modifier then Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (path.isEmpty()) {
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
                        .clip(PathShape(path))
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
