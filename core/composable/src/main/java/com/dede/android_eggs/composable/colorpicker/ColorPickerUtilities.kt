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
import android.graphics.Color as AndroidColor

object ColorPickerUtilities {

    const val ACTION_OPEN_EYE_DROPPER = "android.intent.action.OPEN_EYE_DROPPER"
    const val EXTRA_COLOR = "android.intent.extra.COLOR"

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
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(this.toArgb(), hsv)
        return hsv
    }

    /**
     * Square SV palette: x axis is saturation (0..1, left to right), y axis is
     * value inverted (top is fully bright, bottom is black).
     */
    fun getSvPalettePointByColor(color: Color, size: IntSize): Offset {
        val hsv = color.getHsv()
        val x = hsv[1] * size.width
        val y = (1f - hsv[2]) * size.height
        return Offset(x, y)
    }

    fun getSvPaletteColorByPoint(point: Offset, size: IntSize, hue: Float): FloatArray {
        val saturation = (point.x / size.width).coerceIn(0f, 1f)
        val value = 1f - (point.y / size.height).coerceIn(0f, 1f)
        return floatArrayOf(hue, saturation, value)
    }

    fun rangeSvPalettePoint(position: Offset, size: IntSize): Offset {
        return Offset(
            position.x.coerceIn(0f, size.width.toFloat()),
            position.y.coerceIn(0f, size.height.toFloat()),
        )
    }
}
