package com.dede.android_eggs.views.main.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.createRepeatWavyDrawable
import kotlin.math.roundToInt

@Preview
@Composable
fun PreviewWavy() {
    Box(
        modifier = Modifier.background(Color.White)
    ) {
        Wavy(R.drawable.ic_wavy_line_1, true)
    }
}


class DrawablePainter(private val drawable: Drawable, private val bounds: Rect) : Painter() {

    override val intrinsicSize: Size
        get() = Size(drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())

    override fun DrawScope.onDraw() {
        drawable.setBounds(
            bounds.left.roundToInt(),
            bounds.top.roundToInt(),
            bounds.right.roundToInt(),
            bounds.bottom.roundToInt()
        )
        drawable.draw(drawContext.canvas.nativeCanvas)
    }
}

@Composable
fun Wavy(res: Int, repeat: Boolean = false) {
    if (repeat) {
        val context = LocalContext.current
        var bounds by remember { mutableStateOf(Rect.Zero) }
        val bitmap = remember(res, context.theme, bounds) {
            createRepeatWavyDrawable(context, res)
        }
        Image(
            painter = DrawablePainter(bitmap, bounds),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    bounds = Rect(0f, 0f, it.width.toFloat(), it.height.toFloat())
                }
                .padding(vertical = 30.dp)
        )
    } else {
        Image(
            painter = painterResource(id = res),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 30.dp)
        )
    }
}