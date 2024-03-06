package com.dede.android_eggs.views.settings.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.os.bundleOf
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.resolveColor
import com.dede.android_eggs.views.settings.SettingsPrefs
import com.google.accompanist.drawablepainter.rememberDrawablePainter
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
}

@Composable
fun IconShapePref() {
    var currentIndex by rememberPrefIntState(IconShapePrefUtil.KEY_ICON_SHAPE, 0)
    val context = LocalContext.current
    OptionsPref(
        leadingIcon = imageVectorIcon(
            imageVector = Icons.Rounded.RoundedCorner,
            contentDescription = stringResource(R.string.pref_title_icon_shape_override)
        ),
        title = stringResource(R.string.pref_title_icon_shape_override),
    ) {
        val items = stringArrayResource(R.array.icon_shape_override_paths)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(items) { index: Int, path: String ->
                val painter: Painter = if (path.isEmpty()) {
                    painterResource(R.drawable.ic_android_classic)
                } else {
                    val drawable = remember(path) {
                        IconShapePrefUtil.createShapeIcon(context, path)
                    }
                    rememberDrawablePainter(drawable = drawable)
                }
                Card(
                    shape = shapes.small,
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceColorAtElevation(2.dp),
                        disabledContainerColor = colorScheme.surfaceColorAtElevation(6.dp)
                    ),
                    enabled = index != currentIndex,
                    onClick = {
                        currentIndex = index
                        val extras = bundleOf(IconShapePrefUtil.EXTRA_ICON_SHAPE_PATH to path)
                        with(LocalEvent.poster(context)) {
                            post(IconShapePrefUtil.ACTION_CHANGED, extras)
                            post(SettingsPrefs.ACTION_CLOSE_SETTING)
                        }
                    },
                ) {
                    Box(modifier = Modifier.padding(6.dp)) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(30.dp),
                        )
                    }
                }
            }
        }
    }
}