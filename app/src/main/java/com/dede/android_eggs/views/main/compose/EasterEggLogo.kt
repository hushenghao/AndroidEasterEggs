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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.main.EasterEggModules
import com.dede.android_eggs.views.main.EasterEggsActivity
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dagger.Module
import kotlin.math.roundToInt


@Preview(widthDp = 200)
@Composable
fun PreviewEasterEggLogo() {
    val module = EasterEggModules::class.java.getAnnotation(Module::class.java)
    val baseEasterEggs = module.includes.map {
        val instance = try {
            it.java.getField("INSTANCE").get(null)
        } catch (e: Exception) {
            it.java.getConstructor().newInstance()
        }
        val provider = instance as EasterEggProvider
        provider.provideEasterEgg()
    }
    val easterEggs = EasterEggModules.providePureEasterEggList(baseEasterEggs)
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(easterEggs) {
            EasterEggLogo(egg = it)
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun EasterEggLogo(egg: EasterEgg, size: Dp = 44.dp, sensor: Boolean = false) {
    if (egg.supportAdaptiveIcon) {
        val px = with(LocalDensity.current) { size.toPx().roundToInt() }
        val context = LocalContext.current

        val maskPath: String? by remember { mutableStateOf(IconShapePref.getMaskPath(context)) }
        val drawable = remember(maskPath, egg, context.theme) {
            AlterableAdaptiveIconDrawable(context, egg.iconRes, maskPath)
        }
        LocalEvent.receiver().register(IconShapePref.ACTION_CHANGED) {
            val maskPath = it.getStringExtra(IconShapePref.EXTRA_ICON_SHAPE_PATH)
            if (maskPath != null) {
                drawable.setMaskPath(maskPath)
            }
        }
        val drawablePainter = rememberDrawablePainter(drawable = drawable)
        if (sensor) {
            val listener = remember(drawable) {
                object : EasterEggsActivity.Sensor.Update(
                    IntSize(drawable.bounds.width(), drawable.bounds.height())
                ) {
                    override fun onUpdate(matrix: Matrix) {
                        drawable.setForegroundMatrix(matrix)
                    }
                }
            }
            val sensorGroup = LocalEasterEggLogoSensor.current
            DisposableEffect(egg, px) {
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