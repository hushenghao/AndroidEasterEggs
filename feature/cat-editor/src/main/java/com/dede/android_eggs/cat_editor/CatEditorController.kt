package com.dede.android_eggs.cat_editor

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.dede.android_eggs.cat_editor.CatEditorControllerImpl.SaverImpl.KEY_GRID_VISIBLE
import com.dede.android_eggs.util.pref


internal class CatEditorControllerImpl(private val seed: Long) : CatEditorController {

    internal object SaverImpl : Saver<CatEditorControllerImpl, Bundle> {
        private const val KEY_SEED = "seed"
        private const val KEY_COLORS = "colors"
        private const val KEY_SCALE = "scale"
        private const val KEY_OFFSET_X = "offset_x"
        private const val KEY_OFFSET_Y = "offset_y"
        private const val KEY_SELECTED_ENABLED = "selected_enabled"
        private const val KEY_SELECTED_PART = "selected_part"
        private const val KEY_GESTURES_ENABLED = "gestures_enabled"
        internal const val KEY_GRID_VISIBLE = "cat_editor_grid_visible"// pref key
        private const val KEY_MIRROR_MODE = "cat_editor_mirror_mode"

        override fun restore(value: Bundle): CatEditorControllerImpl {
            val seed = value.getLong(KEY_SEED)
            val colors = value.getIntArray(KEY_COLORS)
            val offsetX = value.getFloat(KEY_OFFSET_X)
            val offsetY = value.getFloat(KEY_OFFSET_Y)
            val scale = value.getFloat(KEY_SCALE)
            val selectedPart = value.getInt(KEY_SELECTED_PART)
            val selectedEnabled = value.getBoolean(KEY_SELECTED_ENABLED)
            val gesturesEnabled = value.getBoolean(KEY_GESTURES_ENABLED)
            val gridVisible = value.getBoolean(KEY_GRID_VISIBLE)
            val mirrorMode = value.getBoolean(KEY_MIRROR_MODE)

            val impl = CatEditorControllerImpl(seed).apply {
                if (colors != null) {
                    updateColors(colors.map(::Color))
                }
                offsetState.value = Offset(offsetX, offsetY)
                scaleState.floatValue = scale
                selectEnabledState.value = selectedEnabled
                selectedPartState.intValue = selectedPart
                gesturesEnabledState.value = gesturesEnabled
                gridVisibleState.value = gridVisible
                mirrorModeState.value = mirrorMode
            }
            return impl
        }

        override fun SaverScope.save(value: CatEditorControllerImpl): Bundle {
            return Bundle().apply {
                putLong(KEY_SEED, value.seed)
                putIntArray(KEY_COLORS, value.colorList.map(Color::toArgb).toIntArray())
                putFloat(KEY_OFFSET_X, value.offsetState.value.x)
                putFloat(KEY_OFFSET_Y, value.offsetState.value.y)
                putFloat(KEY_SCALE, value.scaleState.floatValue)
                putInt(KEY_SELECTED_PART, value.selectedPartState.intValue)
                putBoolean(KEY_SELECTED_ENABLED, value.selectEnabledState.value)
                putBoolean(KEY_GESTURES_ENABLED, value.gesturesEnabledState.value)
                putBoolean(KEY_GRID_VISIBLE, value.gridVisibleState.value)
                putBoolean(KEY_MIRROR_MODE, value.mirrorModeState.value)
            }
        }
    }

    val scaleState = mutableFloatStateOf(1f)

    val offsetState = mutableStateOf(Offset.Zero)

    val selectedPartState = mutableIntStateOf(CatEditorController.UNSELECTED_PART)

    private val gridVisibleState = mutableStateOf(false)

    private val mirrorModeState = mutableStateOf(false)

    private val selectEnabledState = mutableStateOf(true)

    private val gesturesEnabledState = mutableStateOf(true)

    val colorListVersionState = mutableIntStateOf(0)

    private val colorStateList = mutableStateListOf(*CatPartColors.colors(seed))

    override var defaultGraphicsLayerScale: Float = 1f
        set(value) {
            field = value
            resetGraphicsLayer()
        }

    // color list version is used to track changes in the color list
    override val colorListVersion: Int by colorListVersionState

    override val colorList: List<Color> = colorStateList

    override var selectPart: Int by selectedPartState

    override var isGridVisible: Boolean by gridVisibleState

    override var isMirrorMode: Boolean by mirrorModeState

    override var isSelectEnabled: Boolean by selectEnabledState

    override var isGesturesEnabled: Boolean by gesturesEnabledState

    override var zoom: Float
        get() = scaleState.floatValue
        set(value) {
            scaleState.floatValue = range(value, S_MAX, S_MIN)
        }

    override fun updateColors(colors: List<Color>) {
        colorStateList.clear()
        colorStateList.addAll(colors.toList())
        colorListVersionState.intValue += 1
        resetGraphicsLayer()
    }

    override fun setSelectedPartColor(color: Color) {
        if (hasSelectedPart) {
            colorStateList[selectPart] = color
            colorListVersionState.intValue += 1
            selectPart = CatEditorController.UNSELECTED_PART
        }
    }

    override fun resetGraphicsLayer() {
        scaleState.floatValue = defaultGraphicsLayerScale
        offsetState.value = Offset.Zero
        selectPart = CatEditorController.UNSELECTED_PART
    }
}

internal interface CatEditorController {

    companion object {
        const val UNSELECTED_PART = -1
    }

    var defaultGraphicsLayerScale: Float

    var isSelectEnabled: Boolean

    var isGesturesEnabled: Boolean

    var selectPart: Int

    var isGridVisible: Boolean

    var isMirrorMode: Boolean

    val colorListVersion: Int

    val colorList: List<Color>

    val hasSelectedPart: Boolean
        get() = selectPart != UNSELECTED_PART

    var zoom: Float

    fun resetGraphicsLayer()

    fun updateColors(colors: List<Color>)

    fun updateColors(seed: Long) {
        updateColors(CatPartColors.colors(seed).toList())
    }

    fun setSelectedPartColor(color: Color)

    fun getSelectedPartColor(default: Color): Color {
        if (!hasSelectedPart) return default
        return colorList[selectPart]
    }
}

@Composable
internal fun rememberCatEditorController(seed: Long = Utilities.randomSeed()): CatEditorController {
    val controller = rememberSaveable(saver = CatEditorControllerImpl.SaverImpl) {
        CatEditorControllerImpl(seed)
    }
    val context = LocalContext.current
    LaunchedEffect(controller) {
        controller.isGridVisible =
            context.pref.getBoolean(KEY_GRID_VISIBLE, controller.isGridVisible)
    }
    LaunchedEffect(controller.isGridVisible) {
        context.pref.edit {
            putBoolean(KEY_GRID_VISIBLE, controller.isGridVisible)
        }
    }
    return controller
}
