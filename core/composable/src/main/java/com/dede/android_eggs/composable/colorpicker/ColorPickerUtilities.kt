package com.dede.android_eggs.composable.colorpicker

import android.content.Context
import android.content.Intent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import com.google.android.material.color.MaterialColors
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import android.graphics.Color as AndroidColor

object ColorPickerUtilities {

    const val ACTION_OPEN_EYE_DROPPER = "android.intent.action.OPEN_EYE_DROPPER"
    const val EXTRA_COLOR = "android.intent.extra.COLOR"

    private val hsv = floatArrayOf(0f, 0f, 1f)

    fun isEyeDropperSupported(context: Context): Boolean {
        val intent = Intent(ACTION_OPEN_EYE_DROPPER)
        return intent.resolveActivity(context.packageManager) != null
    }

    fun String.toColorOrNull(): Color? {
        if (this == "none") {
            return Color.Transparent
        }
        if (!this.startsWith("#") && this.length !in 4..9) {
            return null
        }
        var str = this
        if (this.length == 4) {
            val r = this[1].toString().repeat(2)
            val g = this[2].toString().repeat(2)
            val b = this[3].toString().repeat(2)
            str = "#$r$g$b"
        } else if (this.length == 5) {
            val a = this[1].toString().repeat(2)
            val r = this[2].toString().repeat(2)
            val g = this[3].toString().repeat(2)
            val b = this[4].toString().repeat(2)
            str = "#$a$r$g$b"
        }
        val colorInt = try {
            str.toColorInt()
        } catch (_: IllegalArgumentException) {
            return null
        }
        return Color(colorInt)
    }

    fun getHexColor(color: Color, withAlpha: Boolean): String {
        val argb = color.toArgb()
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        if (withAlpha) {
            val a = (argb shr 24) and 0xFF
            return String.format("#%02X%02X%02X%02X", a, r, g, b)
        } else {
            return String.format("#%02X%02X%02X", r, g, b)
        }
    }

    fun getHighlightColor(
        color: Color,
        lightColor: Color = Color.White,
        darkColor: Color = Color.DarkGray,
    ): Color {
        return if (MaterialColors.isColorLight(color.toArgb())) {
            darkColor
        } else {
            lightColor
        }
    }

    fun blendColor(color1: Color, color2: Color, ratio: Float): Color {
        return Color(ColorUtils.blendARGB(color1.toArgb(), color2.toArgb(), ratio))
    }

    fun Color.getHsv(): FloatArray {
        AndroidColor.colorToHSV(this.toArgb(), hsv)
        return hsv
    }

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

    fun rangeHsvPalettePoint(position: Offset, size: IntSize): Offset {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = min(centerX, centerY)

        val x = position.x - centerX
        val y = position.y - centerY
        val r = hypot(x, y)
        if (r > radius) {
            val angle = atan2(y, x)
            val newX = radius * cos(angle)
            val newY = radius * sin(angle)
            return Offset(centerX + newX, centerY + newY)
        }
        return position
    }
}
