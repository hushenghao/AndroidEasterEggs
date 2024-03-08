package com.dede.android_eggs.views.main.compose

import android.annotation.SuppressLint
import android.graphics.Matrix
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.main.EasterEggHelp
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.main.EasterEggLogoSensorMatrixConvert
import com.dede.android_eggs.views.settings.compose.IconShapePrefUtil
import com.dede.android_eggs.views.settings.compose.SettingPrefUtil
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

@SuppressLint("NewApi")
@Composable
fun EasterEggLogo(egg: EasterEgg, size: Dp = 44.dp, sensor: Boolean = false) {
    if (egg.supportAdaptiveIcon) {
        val context = LocalContext.current

        val maskPath: String? by remember { mutableStateOf(IconShapePrefUtil.getMaskPath(context)) }
        val drawable = remember(maskPath, egg.iconRes, context.theme) {
            AlterableAdaptiveIconDrawable(context, egg.iconRes, maskPath)
        }
        LocalEvent.receiver().register(IconShapePrefUtil.ACTION_CHANGED) {
            val newMaskPath = it.getStringExtra(SettingPrefUtil.EXTRA_VALUE)
            if (newMaskPath != null) {
                drawable.setMaskPath(newMaskPath)
            }
        }
        val drawablePainter = rememberDrawablePainter(drawable = drawable)
        val sensorGroup = LocalEasterEggLogoSensor.currentOutInspectionMode
        if (sensor && sensorGroup != null) {
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
        Image(
            painter = drawablePainter,
            contentDescription = stringResource(egg.nicknameRes),
            modifier = Modifier.size(size)
        )
    } else {
        Image(
            painter = painterResource(egg.iconRes),
            contentDescription = stringResource(egg.nicknameRes),
            modifier = Modifier.size(size)
        )
    }
}