package com.dede.android_eggs.util.compose

import android.annotation.SuppressLint
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.PathParser

class PathShape(pathData: String, private val pathSize: Float = 100f) : Shape {

    @SuppressLint("RestrictedApi")
    private val shapePath = PathParser.createPathFromPathData(pathData).asComposePath()
    private val matrix = Matrix()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            addPath(shapePath)
            matrix.reset()
            matrix.scale(size.width / pathSize, size.height / pathSize)
            transform(matrix)
            close()
        }
        return Outline.Generic(path)
    }
}