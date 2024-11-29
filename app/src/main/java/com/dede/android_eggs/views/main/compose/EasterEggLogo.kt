package com.dede.android_eggs.views.main.compose

import android.graphics.Matrix
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.Receiver
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import com.dede.basic.isAdaptiveIconDrawable
import com.dede.basic.provider.EasterEgg
import com.google.accompanist.drawablepainter.rememberDrawablePainter


@Preview(widthDp = 200)
@Composable
fun PreviewEasterEggLogo() {
    val easterEggs = EasterEggHelp.previewEasterEggs()
    LazyVerticalGrid(columns = GridCells.Adaptive(44.dp)) {
        items(easterEggs) {
            EasterEggLogo(egg = it)
        }
    }
}

@Composable
fun EasterEggLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    mask: String? = null,
    sensor: Boolean = false,
    @DrawableRes res: Int,
) {
    val context = LocalContext.current
    val isAdaptiveIcon = remember(res) { context.isAdaptiveIconDrawable(res) }
    if (isAdaptiveIcon) {
        val maskPath = remember(mask) {
            mask ?: IconShapePrefUtil.getMaskPath(context)
        }
        val drawable = remember(maskPath, res, context.theme) {
            AlterableAdaptiveIconDrawable(context, res, maskPath)
        }
        LocalEvent.Receiver(IconShapePrefUtil.ACTION_CHANGED) {
            val newMaskPath = it.getStringExtra(SettingPrefUtil.EXTRA_VALUE)
            if (newMaskPath != null) {
                drawable.setMaskPath(newMaskPath)
            }
        }
        val drawablePainter = rememberDrawablePainter(drawable = drawable)
        if (sensor) {
            val sensorGroup = LocalEasterEggLogoSensor.currentOutInspectionMode
            if (sensorGroup != null) {
                val listener = remember(drawable.bounds) {
                    object : EasterEggLogoSensorMatrixConvert.Listener(drawable.bounds) {
                        override fun onUpdateMatrix(matrix: Matrix) {
                            drawable.setForegroundMatrix(matrix)
                        }
                    }
                }
                DisposableEffect(drawable) {
                    sensorGroup.register(listener)
                    onDispose {
                        sensorGroup.unregister(listener)
                    }
                }
            }
        }
        Image(
            painter = drawablePainter,
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(res),
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}

@Composable
fun EasterEggLogo(egg: EasterEgg, size: Dp = 44.dp, sensor: Boolean = false) {
    EasterEggLogo(
        modifier = Modifier.size(size),
        res = egg.iconRes,
        contentDescription = stringResource(egg.nicknameRes),
        sensor = sensor
    )
}
