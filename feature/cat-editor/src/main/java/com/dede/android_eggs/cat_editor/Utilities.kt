package com.dede.android_eggs.cat_editor

import android.graphics.Region
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toRegion
import com.google.android.material.color.MaterialColors
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import android.graphics.Color as AndroidColor

internal object Utilities {

    fun getHexColor(color: Color): String {
        val argb = color.toArgb()
        val a = (argb shr 24) and 0xFF
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF

        return String.format("#%02X%02X%02X%02X", a, r, g, b)
    }

    fun getHighlightColor(color: Color): Color {
        return if (MaterialColors.isColorLight(color.toArgb())) {
            Color.DarkGray
        } else {
            Color.White
        }
    }

    fun blendColor(color1: Color, color2: Color, ratio: Float): Color {
        return Color(ColorUtils.blendARGB(color1.toArgb(), color2.toArgb(), ratio))
    }

    fun Color.getHsv(): FloatArray {
        AndroidColor.colorToHSV(this.toArgb(), hsv)
        return hsv
    }

    private val hsv = floatArrayOf(0f, 0f, 1f)

    fun getHsvPalettePointByColor(color: Color, size: IntSize): Offset {
        val hsv = color.getHsv()
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = hsv[1] * min(centerX, centerY)
        val pointX = (radius * cos(Math.toRadians(hsv[0].toDouble())) + centerX)
        val pointY = (-radius * sin(Math.toRadians(hsv[0].toDouble())) + centerY)
        return Offset(pointX.toFloat(), pointY.toFloat())
    }

    fun getHsvPaletteColorByPoint(point: Offset, size: IntSize, value: Float = 1f): FloatArray {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = min(centerX, centerY)

        val x = point.x - centerX
        val y = point.y - centerY
        val r = sqrt(x * x + y * y)
        val hue = (atan2(y.toDouble(), -x.toDouble()) / Math.PI * 180f).toFloat() + 180
        val saturation = max(0f, min(1f, (r / radius)))
        hsv[0] = hue
        hsv[1] = saturation
        hsv[2] = value
        return hsv
    }

    fun rangeHsvPaletteColor(color: Color): Color {
        val hsv = color.getHsv()
        val hue = max(0f, min(360f, hsv[0]))
        val saturation = max(0f, min(1f, hsv[1]))
        return Color.hsv(hue, saturation, 1f)
    }

    /**
     * Restrict the point to be within the circle of the given size.
     */
    fun rangeHsvPalettePoint(position: Offset, size: IntSize): Offset {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = min(centerX, centerY)

        val x = position.x - centerX
        val y = position.y - centerY
        val r = sqrt(x * x + y * y)
        if (r > radius) {
            val angle = atan2(y, x)
            val newX = radius * cos(angle)
            val newY = radius * sin(angle)
            return Offset(centerX + newX, centerY + newY)
        }
        return position
    }

    fun Matrix.toInvert(): Matrix {
        return Matrix(values.copyOf()).apply { invert() }
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

}
