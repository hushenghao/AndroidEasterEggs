package com.dede.android_eggs.views.settings.compose

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.os.bundleOf
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.resolveColor
import kotlinx.coroutines.launch
import com.dede.basic.dp as dip

object IconShapePrefUtil {

    const val KEY_ICON_SHAPE = "pref_key_override_icon_shape"

    const val ACTION_CHANGED = "com.dede.easter_eggs.IconShapeChanged"
    const val EXTRA_ICON_SHAPE_PATH = "extra_icon_shape_path"

    fun getMaskPath(context: Context): String {
        val index = SettingPref.getValue(context, KEY_ICON_SHAPE, 0)
        return getMaskPathByIndex(context, index)
    }

    private fun getMaskPathByIndex(context: Context, index: Int): String {
        val paths = context.resources.getStringArray(R.array.icon_shape_override_paths)
        return paths[index % paths.size]
    }

    fun createShapeIcon(context: Context, pathStr: String): Drawable {
        val bitmap = createBitmap(56.dip, 56.dip, Bitmap.Config.ARGB_8888)
        val shapePath = AlterableAdaptiveIconDrawable.getMaskPath(
            pathStr, bitmap.width, bitmap.height
        )
        val color = context.resolveColor(com.google.android.material.R.attr.colorSecondary)
        bitmap.applyCanvas {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = color
            drawPath(shapePath, paint)

            setBitmap(null)
        }
        return BitmapDrawable(context.resources, bitmap)
    }

    @SuppressLint("RestrictedApi")
    fun getCloverPath(context: Context): android.graphics.Path {
        val pathStr = context.getString(R.string.icon_shape_clover_path)
        return PathParser.createPathFromPathData(pathStr)
    }
}

class PathShape(pathData: String, private val pathSize: Float = 100f) : Shape {

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

@Composable
fun IconShapePref() {
    var currentIndex by rememberPrefIntState(IconShapePrefUtil.KEY_ICON_SHAPE, 0)
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    LaunchedEffect(key1 = currentIndex) {
        launch {
            lazyListState.scrollToItem(currentIndex)
        }
    }
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.RoundedCorner,
        title = stringResource(R.string.pref_title_icon_shape_override),
    ) {
        val items = stringArrayResource(R.array.icon_shape_override_paths)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            state = lazyListState,
        ) {
            itemsIndexed(items) { index: Int, path: String ->
                Card(
                    shape = PathShape(stringResource(R.string.icon_shape_clover_path)),
                    onClick = onClick@{
                        if (currentIndex == index) return@onClick
                        currentIndex = index
                        val extras = bundleOf(IconShapePrefUtil.EXTRA_ICON_SHAPE_PATH to path)
                        with(LocalEvent.poster(context)) {
                            post(IconShapePrefUtil.ACTION_CHANGED, extras)
                            post(SettingPref.ACTION_CLOSE_SETTING)
                        }
                    },
                    modifier = Modifier.size(44.dp)
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
                        if (index == currentIndex) {
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
        }
    }
}