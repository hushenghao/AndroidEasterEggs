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
import androidx.compose.ui.graphics.PathSegment
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
     * Converts a Compose [Path] to an SVG path data string (the `d` attribute of a `<path>` element).
     *
     * This reverses the transformation done by [vectorPath] / [PathBuilder.toPath]:
     * the [Path]'s internal segment list (flattening arcs to cubics via [Path.iterator])
     * is serialized back to SVG command strings.
     *
     * Note: elliptical arcs are approximated as cubic beziers during [Path] construction,
     * so the output uses `C` commands rather than `A` commands. The visual result is identical.
     */
    fun Path.toSvgPathData(): String {
        val sb = StringBuilder()
        for (segment in this) {
            when (segment.type) {
                PathSegment.Type.Move -> {
                    sb.append('M')
                    appendFloat(sb, segment.points[0])
                    sb.append(',')
                    appendFloat(sb, segment.points[1])
                }
                PathSegment.Type.Line -> {
                    sb.append('L')
                    appendFloat(sb, segment.points[2])
                    sb.append(',')
                    appendFloat(sb, segment.points[3])
                }
                PathSegment.Type.Quadratic -> {
                    sb.append('Q')
                    appendFloat(sb, segment.points[2])
                    sb.append(',')
                    appendFloat(sb, segment.points[3])
                    sb.append(' ')
                    appendFloat(sb, segment.points[4])
                    sb.append(',')
                    appendFloat(sb, segment.points[5])
                }
                PathSegment.Type.Conic -> {
                    // Conic (quadratic with weight) — approximate as Q for SVG compatibility
                    // Weight is at segment.weight; SVG arc doesn't support conic weight directly
                    sb.append('Q')
                    appendFloat(sb, segment.points[2])
                    sb.append(',')
                    appendFloat(sb, segment.points[3])
                    sb.append(' ')
                    appendFloat(sb, segment.points[4])
                    sb.append(',')
                    appendFloat(sb, segment.points[5])
                }
                PathSegment.Type.Cubic -> {
                    sb.append('C')
                    appendFloat(sb, segment.points[2])
                    sb.append(',')
                    appendFloat(sb, segment.points[3])
                    sb.append(' ')
                    appendFloat(sb, segment.points[4])
                    sb.append(',')
                    appendFloat(sb, segment.points[5])
                    sb.append(' ')
                    appendFloat(sb, segment.points[6])
                    sb.append(',')
                    appendFloat(sb, segment.points[7])
                }
                PathSegment.Type.Close -> sb.append('z')
                PathSegment.Type.Done -> {} // end of iteration, ignore
            }
        }
        return sb.toString()
    }

    /**
     * Appends a float value to [sb] in SVG-path-friendly format:
     * 1. Rounds to at most 3 decimal places (SVG path precision is well within this)
     * 2. Removes trailing `.0` for whole numbers (e.g. `15.0` → `15`)
     * 3. Removes unnecessary trailing zeros after the decimal point (e.g. `6.700` → `6.7`)
     */
    private fun appendFloat(sb: StringBuilder, value: Float) {
        // Round to 3 decimal places: multiply, truncate, divide back
        val rounded = (value * 1000f).toInt() / 1000f
        val s = rounded.toString()
        val len = s.length
        val last = len - 1
        // Remove trailing '.0'
        if (last >= 2 && s[last] == '0' && s[last - 1] == '.') {
            sb.append(s, 0, last - 1)
            return
        }
        // Remove unnecessary trailing zeros after the decimal point
        var i = last
        while (i > 0 && s[i] == '0' && s[i - 1] != '.') {
            i--
        }
        sb.append(s, 0, i + 1)
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
