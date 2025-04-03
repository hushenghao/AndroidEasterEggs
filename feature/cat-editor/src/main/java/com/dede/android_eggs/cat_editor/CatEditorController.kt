package com.dede.android_eggs.cat_editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color


internal class CatEditorControllerImpl(speed: Long) : CatEditorController {
    val scaleState = mutableFloatStateOf(1f)
    val offsetState = mutableStateOf(Offset.Zero)

    val selectedPartState = mutableIntStateOf(-1)
    private val gridVisibleState = mutableStateOf(false)

    private val selectEnabledState = mutableStateOf(true)
    private val gesturesEnabledState = mutableStateOf(true)

    private val colorStateList = mutableStateListOf(*CatPartColors.colors(speed))

    override val colorList: List<Color> = colorStateList

    override var selectPart: Int by selectedPartState

    override var isGridVisible: Boolean by gridVisibleState

    override var isSelectEnabled: Boolean by selectEnabledState

    override var isGesturesEnabled: Boolean by gesturesEnabledState

    override fun updateColors(speed: Long) {
        val colors = CatPartColors.colors(speed)
        updateColors(colors.toList())
    }

    override fun updateColors(colors: List<Color>) {
        colorStateList.clear()
        colorStateList.addAll(colors)
        resetGraphicsLayer()
    }

    override fun setPartColor(part: Int, color: Color) {
        colorStateList[part] = color
        selectPart = -1
    }

    override fun resetGraphicsLayer() {
        scaleState.floatValue = 1f
        offsetState.value = Offset.Zero
    }
}

internal interface CatEditorController {

    var isSelectEnabled: Boolean

    var isGesturesEnabled: Boolean

    var selectPart: Int

    var isGridVisible: Boolean

    val colorList: List<Color>

    fun resetGraphicsLayer()

    fun updateColors(speed: Long)

    fun updateColors(colors: List<Color>)

    fun setPartColor(part: Int, color: Color)
}

@Composable
internal fun rememberCatEditorController(speed: Long = System.currentTimeMillis()): CatEditorController {
    return remember { CatEditorControllerImpl(speed) }
}
