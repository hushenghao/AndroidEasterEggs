package com.dede.android_eggs.cat_editor

import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Region
import android.os.Build
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.toRegion
import java.util.Objects
import kotlin.math.roundToInt
import android.graphics.Matrix as AndroidMatrix

internal object Utilities {

    private const val TAG = "Utilities"

    fun randomSeed(): Long {
        return System.currentTimeMillis()
    }

    fun string2Seed(string: String): Long {
        val seed = string.toLongOrNull()
        if (seed != null) {
            return seed
        }

        val hash = string.hashCode().toLong()
        val noise = OpenSimplex2S.noise2_ImproveX(hash, 8.0, 4.0)
        Log.i(TAG, "noise: $noise")
        return Objects.hash(hash, noise).toLong()
    }

    fun Matrix.toInvert(): Matrix {
        return Matrix(values.copyOf()).apply { invert() }
    }

    private val floats = FloatArray(9)
    private val androidMatrix: AndroidMatrix = AndroidMatrix()

    fun Matrix.asAndroidMatrix(dest: AndroidMatrix? = androidMatrix): AndroidMatrix {
        val matrix = dest ?: AndroidMatrix()

        val srcArr = this.values
        val destArr = floats
        destArr[AndroidMatrix.MSCALE_X] = srcArr[Matrix.ScaleX]
        destArr[AndroidMatrix.MSKEW_X] = srcArr[Matrix.SkewX]
        destArr[AndroidMatrix.MTRANS_X] = srcArr[Matrix.TranslateX]
        destArr[AndroidMatrix.MSKEW_Y] = srcArr[Matrix.SkewY]
        destArr[AndroidMatrix.MSCALE_Y] = srcArr[Matrix.ScaleY]
        destArr[AndroidMatrix.MTRANS_Y] = srcArr[Matrix.TranslateY]
        destArr[AndroidMatrix.MPERSP_0] = srcArr[Matrix.Perspective0]
        destArr[AndroidMatrix.MPERSP_1] = srcArr[Matrix.Perspective1]
        destArr[AndroidMatrix.MPERSP_2] = srcArr[Matrix.Perspective2]
        matrix.setValues(destArr)
        return matrix
    }

    fun Path.getRegion(isClosePath: Boolean): Region {
        val boundsRegion = getBounds().toAndroidRectF().toRegion()
        if (!isClosePath) {
            return boundsRegion
        }
        val region = Region()
        region.setPath(asAndroidPath(), boundsRegion)
        return region
    }

    fun isPointInRegion(point: Offset, pointMatrix: Matrix, region: Region): Boolean {
        val p = pointMatrix.map(point)
        return region.contains(p.x.roundToInt(), p.y.roundToInt())
    }

    /**
     * PrintHelper writes the bitmap into the PDF without compositing alpha, so a
     * transparent background (the cat canvas is transparent) prints black.
     * Flatten the image onto a white background before printing. Hardware-backed
     * or immutable bitmaps are copied into a mutable software bitmap first,
     * since a software Canvas cannot draw hardware bitmaps.
     */
    fun Bitmap.toPrintBitmap(): Bitmap {
        if (!hasAlpha()) {
            return this
        }
        // Composite white behind the image in place — a single draw pass with
        // no extra bitmap allocation.
        val source = if (isMutable) this else copy(Bitmap.Config.ARGB_8888, true)
        return source.applyCanvas {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawColor(Color.WHITE, BlendMode.DST_OVER)
            } else {
                drawColor(Color.WHITE, PorterDuff.Mode.DST_OVER)
            }
        }
    }
}
