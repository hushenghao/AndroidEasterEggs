package com.dede.android_eggs.cat_editor.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.dede.android_eggs.cat_editor.Cat
import com.dede.android_eggs.cat_editor.model.CatParts

@Composable
internal fun rememberCatPainter(cat: Cat): CatPainter {
    return remember(cat) { CatPainter(cat) }
}

internal class CatPainter(private val cat: Cat) : Painter() {

    private val partColor: (index: Int) -> Color = { cat.colors[it] }

    private var lastSize: Size = Size.Unspecified
    private var cachedPaths: List<Path> = emptyList()
    private var cachedScale: Float = 0f

    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        val minDimension = this.size.minDimension
        if (minDimension <= 0f) return

        if (lastSize != this.size) {
            lastSize = this.size
            val canvasSize = Size(minDimension, minDimension)
            val canvasMatrix = createCanvasMatrix(canvasSize)
            cachedPaths = createTransformedCatPaths(canvasMatrix)
            cachedScale = minDimension / CatParts.VIEW_PORT_SIZE
        }

        if (cat.isMirrorMode) {
            this.drawContext.transform.scale(-1f, 1f)
        }

        composeCanvasDraw(cachedPaths, cachedScale, partColor)
    }
}
