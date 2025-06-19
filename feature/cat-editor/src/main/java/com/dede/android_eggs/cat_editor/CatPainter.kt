package com.dede.android_eggs.cat_editor

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.dede.android_eggs.cat_editor.Utilities.asAndroidMatrix

@Composable
internal fun rememberCatPainter(cat: Cat): CatPainter {
    return remember(cat) { CatPainter(cat) }
}

internal class CatPainter(private val cat: Cat) : Painter() {

    private val partColor: (index: Int) -> Color = { cat.colors[it] }

    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        val canvasSize = Size(this.size.minDimension, this.size.minDimension)

        val canvasMatrix = createCanvasMatrix(canvasSize)

        val bitmap: Bitmap? = if (useAndroidCanvasDraw) createAndroidBitmap(canvasSize) else null

        if (useAndroidCanvasDraw && bitmap != null) {
            // android canvas draw
            androidCanvasDraw(
                canvasMatrix.asAndroidMatrix(),
                bitmap,
                partColor
            )
        } else {
            // compose canvas draw
            composeCanvasDraw(
                canvasMatrix,
                partColor
            )
        }
    }
}
