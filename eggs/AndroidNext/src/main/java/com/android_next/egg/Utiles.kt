package com.android_next.egg

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextLayoutResult
import com.dede.basic.utils.AppLocaleDateFormatter
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

internal fun Calendar.setDateZero(): Calendar {
    clear(Calendar.HOUR_OF_DAY)
    clear(Calendar.MINUTE)
    clear(Calendar.SECOND)
    clear(Calendar.MILLISECOND)
    return this
}

internal fun getReleaseDate(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(AndroidNextEasterEgg.RELEASE_YEAR, AndroidNextEasterEgg.RELEASE_MONTH, 1)
    calendar.setDateZero()
    return calendar
}

internal fun getLocaleFormatMonth(month: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, month)
    return AppLocaleDateFormatter.getInstance("MMMM").format(calendar.time)
}

internal fun TextLayoutResult.getFixedSize(): Size {
    val height = getLineBottom(lineCount - 1)
    var width = 0f
    for (index in 0..<lineCount) {
        width = max(getLineRight(index), width)
    }
    return Size(width, height)
}

internal fun Float.toRadians(): Float = Math.toRadians(this.toDouble()).toFloat()

internal fun Float.toDegrees(): Float = Math.toDegrees(this.toDouble()).toFloat()

// 获取圆弧终点
internal fun getArcEndPoint(
    rect: Rect,
    startAngleDegrees: Float,
    sweepAngleDegrees: Float
): Offset {
    // 1. 计算最终角度
    val endAngleDegrees = startAngleDegrees + sweepAngleDegrees

    // 2. 计算椭圆中心
    val centerX = rect.left + rect.width / 2f
    val centerY = rect.top + rect.height / 2f

    // 3. 计算半轴半径
    val radiusX = rect.width / 2f
    val radiusY = rect.height / 2f

    // 4. 转为弧度并计算坐标（注意：Y轴向下为正，正角度在屏幕坐标中顺时针旋转，直接套用数学公式即可）
    val rad = endAngleDegrees.toRadians()
    val endX = centerX + radiusX * cos(rad)
    val endY = centerY + radiusY * sin(rad)

    return Offset(endX, endY)
}
