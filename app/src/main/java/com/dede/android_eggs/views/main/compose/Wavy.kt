package com.dede.android_eggs.views.main.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.createRepeatWavyDrawable

@Preview
@Composable
fun PreviewWavyRepeat() {
    Wavy(R.drawable.ic_wavy_line_1, true)
}

@Preview
@Composable
fun PreviewWavy() {
    Wavy(R.drawable.ic_wavy_line)
}


@Composable
fun Wavy(
    @DrawableRes res: Int,
    repeat: Boolean = false,
    tint: Color = MaterialTheme.colorScheme.secondary
) {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 26.dp)
    if (repeat) {
        val context = LocalContext.current
        val drawable = remember(res, context.theme) {
            createRepeatWavyDrawable(context, res).apply {
                setTint(tint.toArgb())
            }
        }
        DrawableImage(
            drawable = drawable,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = res),
            contentDescription = null,
            modifier = modifier
        )
    }
}