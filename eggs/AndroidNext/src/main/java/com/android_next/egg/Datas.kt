package com.android_next.egg

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

internal data class LabelExtra(
    @StringRes val labelRes: Int,
    val color: Color,
    val offsetXPercent: Float,
    val offsetYPercent: Float = 35.2f / 180f,
)

internal val labelExtras = arrayOf(
    LabelExtra(
        labelRes = R.string.label_timeline_developer_previews,
        color = Color(0xFF_54585D),
        offsetXPercent = 25f / 789f
    ),
    LabelExtra(
        labelRes = R.string.label_timeline_beta_release,
        color = Color(0xFF_3A7AF2),
        offsetXPercent = 210f / 789f
    ),
    LabelExtra(
        labelRes = R.string.label_timeline_platform_stability,
        color = Color(0xFF_2E9E49),
        offsetXPercent = 466f / 789f
    ),
)

internal data class Colors(
    val textColor: Color,
    val selectedTextColor: Color,
    val shapeColor: Color,
)

internal data class MonthExtra(
    val colors: Colors,
    val nightColors: Colors,
    val offsetXPercent: Float,
    val offsetYPercent: Float = 135 / 180f,
)

internal val monthExtras = arrayOf(
    MonthExtra(
        colors = Colors(
            textColor = Color.Black,
            selectedTextColor = Color.White,
            shapeColor = Color(0xFF_54585D)
        ),
        nightColors = Colors(
            textColor = Color(0xFF_DDDDDD),
            selectedTextColor = Color(0xFF_EEEEEE),
            shapeColor = Color(0xFF_54585D)
        ),
        offsetXPercent = 49 / 789f,
    ),
    MonthExtra(
        colors = Colors(
            textColor = Color.Black,
            selectedTextColor = Color.White,
            shapeColor = Color(0xFF_54585D)
        ),
        nightColors = Colors(
            textColor = Color(0xFF_DDDDDD),
            selectedTextColor = Color(0xFF_EEEEEE),
            shapeColor = Color(0xFF_54585D)
        ),
        offsetXPercent = 153 / 789f,
    ),
    MonthExtra(
        colors = Colors(
            textColor = Color.Black,
            selectedTextColor = Color.White,
            shapeColor = Color(0xFF_3A7AF2)
        ),
        nightColors = Colors(
            textColor = Color(0xFF_DDDDDD),
            selectedTextColor = Color(0xFF_EEEEEE),
            shapeColor = Color(0xFF_3A7AF2)
        ),
        offsetXPercent = 257 / 789f,
    ),
    MonthExtra(
        colors = Colors(
            textColor = Color.Black,
            selectedTextColor = Color.White,
            shapeColor = Color(0xFF_3A7AF2)
        ),
        nightColors = Colors(
            textColor = Color(0xFF_DDDDDD),
            selectedTextColor = Color(0xFF_EEEEEE),
            shapeColor = Color(0xFF_3A7AF2)
        ),
        offsetXPercent = 361 / 789f,
    ),
    MonthExtra(
        colors = Colors(
            textColor = Color.Black,
            selectedTextColor = Color.White,
            shapeColor = Color(0xFF_3A7AF2)
        ),
        nightColors = Colors(
            textColor = Color(0xFF_DDDDDD),
            selectedTextColor = Color(0xFF_EEEEEE),
            shapeColor = Color(0xFF_3A7AF2)
        ),
        offsetXPercent = 465 / 789f,
    ),
    MonthExtra(
        colors = Colors(
            textColor = Color.Black,
            selectedTextColor = Color.White,
            shapeColor = Color(0xFF_3A7AF2)
        ),
        nightColors = Colors(
            textColor = Color(0xFF_DDDDDD),
            selectedTextColor = Color(0xFF_EEEEEE),
            shapeColor = Color(0xFF_3A7AF2)
        ),
        offsetXPercent = 569 / 789f,
    ),
    MonthExtra(
        colors = Colors(
            textColor = Color(0xFF_2E9E49),
            selectedTextColor = Color.White,
            shapeColor = Color(0xFF_2E9E49)
        ),
        nightColors = Colors(
            textColor = Color(0xFF_2E9E49),
            selectedTextColor = Color(0xFF_EEEEEE),
            shapeColor = Color(0xFF_2E9E49)
        ),
        offsetXPercent = 717 / 789f,
    ),
)
