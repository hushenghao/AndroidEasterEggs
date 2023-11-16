package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.main.EasterEggModules
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
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

@Composable
fun EasterEggLogo(egg: EasterEgg, size: Dp = 44.dp) {
    if (egg.supportAdaptiveIcon) {
        val px = with(LocalDensity.current) { size.toPx().roundToInt() }
        val context = LocalContext.current

        var maskPath: String? by remember { mutableStateOf(IconShapePref.getMaskPath(context)) }
        LocalEvent.receiver().register(IconShapePref.ACTION_CHANGED) {
            maskPath = it.getStringExtra(IconShapePref.EXTRA_ICON_SHAPE_PATH)
        }

        val drawable = remember(maskPath, egg, context.theme) {
            AlterableAdaptiveIconDrawable(context, egg.iconRes, maskPath)
        }
        DrawableImage(
            drawable = drawable,
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