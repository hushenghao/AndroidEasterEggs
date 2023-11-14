package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.android_u.egg.AndroidUEasterEgg
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.main.AndroidBaseEasterEgg
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import kotlin.math.roundToInt


@Preview
@Composable
fun PreviewEasterEggLogo() {
    val easterEgg1 = AndroidUEasterEgg.provideEasterEgg() as EasterEgg
    val easterEggs = (AndroidBaseEasterEgg.provideEasterEgg() as EasterEggGroup).eggs
    val easterEgg2 = easterEggs.first()
    val easterEgg3 = easterEggs.last()
    Row {
        EasterEggLogo(egg = easterEgg1, preview = true)
        EasterEggLogo(egg = easterEgg2, preview = true)
        EasterEggLogo(egg = easterEgg3, preview = true)
    }
}

@Composable
fun EasterEggLogo(egg: EasterEgg, size: Dp = 44.dp, preview: Boolean = false) {
    if (egg.supportAdaptiveIcon) {
        val px = with(LocalDensity.current) { size.toPx().roundToInt() }
        val context = LocalContext.current
        var maskPath: String? by remember { mutableStateOf(IconShapePref.getMaskPath(context)) }
        if (!preview) {
            LocalEvent.receiver(LocalLifecycleOwner.current)
                .register(IconShapePref.ACTION_CHANGED) {
                    maskPath = it.getStringExtra(IconShapePref.EXTRA_ICON_SHAPE_PATH)
                }
        }

        val bitmap = remember(maskPath, egg, context.theme, px) {
            AlterableAdaptiveIconDrawable(context, egg.iconRes, maskPath)
                .toBitmap(px, px)
                .asImageBitmap()
        }
        Image(
            bitmap = bitmap,
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