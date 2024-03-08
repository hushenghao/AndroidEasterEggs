package com.dede.android_eggs.views.settings.compose

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import androidx.core.os.bundleOf
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    const val ACTION_CHANGED = "com.dede.easter_eggs.IconShapeChanged"

    fun getMaskPath(context: Context): String {
        val index = SettingPrefUtil.getValue(context, KEY_ICON_SHAPE, 0)
        return getMaskPathByIndex(context, index)
    }

    private fun getMaskPathByIndex(context: Context, index: Int): String {
        val paths = context.resources.getStringArray(R.array.icon_shape_override_paths)
        return paths[index % paths.size]
    }

}

private class PathShape(pathData: String, private val pathSize: Float = 100f) : Shape {

    @SuppressLint("RestrictedApi")
    private val shapePath = PathParser.createPathFromPathData(pathData).asComposePath()
    private val matrix = Matrix()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            addPath(shapePath)
            matrix.reset()
            matrix.scale(size.width / pathSize, size.height / pathSize)
            transform(matrix)
            close()
        }
        return Outline.Generic(path)
    }
}

private const val SPAN_COUNT = 5

@Preview
@Composable
fun IconShapePref() {
    var currentIndex by rememberPrefIntState(IconShapePrefUtil.KEY_ICON_SHAPE, 0)
    val context = LocalContext.current
    val density = LocalDensity.current
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.RoundedCorner,
        title = stringResource(R.string.pref_title_icon_shape_override),
    ) {
        val items = stringArrayResource(R.array.icon_shape_override_paths)
        val lines = items.size / SPAN_COUNT + (if ((items.size % SPAN_COUNT) > 0) 1 else 0)
        var itemSize by remember { mutableStateOf(0.dp) }
        LazyVerticalGrid(
            columns = GridCells.Fixed(SPAN_COUNT),
            modifier = Modifier
                .fillMaxWidth()
                .onPlaced {
                    itemSize = with(density) {
                        (it.size.width.toFloat() / SPAN_COUNT).toDp()
                    }
                }
                .height((itemSize * lines))
        ) {
            itemsIndexed(items) { index, path ->
                Box(
                    modifier = Modifier
                        .size(itemSize)
                        .padding(4.dp)
                ) {
                    ShapeItem(
                        isChecked = index == currentIndex,
                        path = path,
                        onClick = onClick@{
                            if (currentIndex == index) return@onClick
                            currentIndex = index
                            val extras = bundleOf(SettingPrefUtil.EXTRA_VALUE to path)
                            with(LocalEvent.poster(context)) {
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

@Composable
private fun ShapeItem(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    path: String,
    onClick: () -> Unit
) {
    Card(
        shape = PathShape(stringResource(R.string.icon_shape_clover_path)),
        onClick = onClick,
        modifier = modifier
    ) {
        Box {
            Box(modifier = Modifier.padding(10.dp)) {
                if (path.isEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.ic_android_classic),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = PathShape(path),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
                    ) {}
                }
            }
            if (isChecked) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(22.dp)
                        .padding(2.dp)
                )
            }
        }
    }
}