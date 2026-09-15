package com.dede.android_eggs.icon_shape

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.dede.android_eggs.util.PathInflater

internal const val DEFAULT_PATH_SIZE = 100f

class PathShape(
    private val path: Path,
    private val pathSize: Float = DEFAULT_PATH_SIZE
) : Shape {

    constructor(pathData: String, pathSize: Float = DEFAULT_PATH_SIZE) :
            this(PathInflater.inflate(pathData).asComposePath(), pathSize)

    private val matrix = Matrix()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            addPath(path)
            matrix.reset()
            matrix.scale(size.width / pathSize, size.height / pathSize)
            transform(matrix)
            close()
        }
        return Outline.Generic(path)
    }
}
