package com.dede.android_eggs.views.main.compose

import android.graphics.Rect
import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.alterable_adaptive_icon.AlterableAdaptiveIcon
import com.dede.android_eggs.local_provider.currentOutInspectionMode
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.PathInflater
import com.dede.android_eggs.util.Receiver
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import com.dede.basic.isAdaptiveIconDrawable
import com.dede.basic.provider.EasterEgg


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
        var maskP by remember {
            mutableStateOf(PathInflater.inflate(maskPath).asComposePath())
        }
        LocalEvent.Receiver(IconShapePrefUtil.ACTION_CHANGED) {
            val newMaskPath = it.getStringExtra(SettingPrefUtil.EXTRA_VALUE)
            if (newMaskPath != null) {
                maskP = PathInflater.inflate(newMaskPath).asComposePath()
            }
        }
        var foregroundMatrix by remember { mutableStateOf(Matrix()) }
        var size by remember { mutableStateOf(IntSize.Zero) }
        if (sensor && size != IntSize.Zero) {
            val sensorGroup = LocalEasterEggLogoSensor.currentOutInspectionMode
            if (sensorGroup != null) {
                DisposableEffect(size) {
                    val floats = FloatArray(9)
                    val bounds = Rect(0, 0, size.width, size.height)
                    val listener = object : EasterEggLogoSensorMatrixConvert.Listener(bounds) {
                            override fun onUpdateMatrix(matrix: android.graphics.Matrix) {
                                matrix.getValues(floats)
                                foregroundMatrix = Matrix().apply {
                                    resetToPivotedTransform(
                                        translationX = floats[android.graphics.Matrix.MTRANS_X],
                                        translationY = floats[android.graphics.Matrix.MTRANS_Y]
                                    )
                                }
                            }
                        }
                    sensorGroup.register(listener)

                    onDispose {
                        sensorGroup.unregister(listener)
                    }
                }
            }
        }
        AlterableAdaptiveIcon(
            modifier = modifier.onSizeChanged { size = it },
            maskPath = maskP,
            foregroundMatrix = foregroundMatrix,
            res = res,
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
