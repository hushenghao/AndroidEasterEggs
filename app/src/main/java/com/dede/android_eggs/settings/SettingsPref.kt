package com.dede.android_eggs.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ItemSettingPrefGroupBinding
import com.dede.android_eggs.settings.SettingPref.Op.Companion.isEnable
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.pref
import com.dede.basic.dp
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions
import com.google.android.material.color.MaterialColors
import com.google.android.material.internal.ContextUtils
import com.google.android.material.internal.EdgeToEdgeUtils
import java.util.*
import com.google.android.material.R as M3R


private fun createFontIcon(context: Context, unicode: String): FontIconsDrawable {
    return FontIconsDrawable(context, unicode, M3R.attr.colorSecondary, 24f)
}

object SettingsPrefs {

    const val ACTION_CLOSE_SETTING = "com.dede.easter_eggs.CloseSetting"

    fun providerPrefs(): List<SettingPref> = listOf(
        NightModePref(),
        IconShapePref(),
        IconVisualEffectsPref(),
        EdgePref(),
        DynamicColorPref(),
    ).filter { it.enable }

    fun apply(context: Context) {
        NightModePref().apply(context)
        DynamicColorPref().apply(context)
    }
}

abstract class SettingPref(
    private val key: String? = null,
    private val options: List<Op>,
    default: Int = 0,
) : MaterialButtonToggleGroup.OnButtonCheckedListener {

    data class Op(
        val value: Int,
        val title: CharSequence? = null,
        @StringRes val titleRes: Int = View.NO_ID,
        val iconUnicode: String? = null,
    ) {

        val id = ViewCompat.generateViewId()

        var iconMaker: ((context: Context) -> Drawable)? = null

        companion object {
            const val ON = 1
            const val OFF = 0

            fun Op?.isEnable(): Boolean {
                return this?.value == ON
            }
        }
    }

    private var selectedValue: Int = default

    open val enable: Boolean = true
    open val title: CharSequence? = null

    @StringRes
    open val titleRes: Int = View.NO_ID

    private fun getValue(context: Context, default: Int): Int {
        if (key == null) return default
        return context.pref.getInt(key, default)
    }

    private fun setValue(context: Context, value: Int): Boolean {
        if (key == null) return false
        context.pref.edit().putInt(key, value).apply()
        return true
    }

    fun getSelectedOp(context: Context): Op? {
        selectedValue = getValue(context, selectedValue)
        return findOptionByValue(selectedValue)
    }

    fun apply(context: Context) {
        val option = getSelectedOp(context) ?: return
        onOptionSelected(context, option)
    }

    open fun onCreateView(context: Context): View {
        val binding = ItemSettingPrefGroupBinding.inflate(LayoutInflater.from(context))
        selectedValue = getValue(context, selectedValue)
        if (titleRes != View.NO_ID) {
            binding.tvTitle.setText(titleRes)
        } else if (title != null) {
            binding.tvTitle.text = title
        }
        binding.btGroup.removeAllViewsInLayout()
        for (op in options) {
            val button = MaterialButton(
                context,
                null,
                M3R.attr.materialButtonOutlinedStyle
            ).apply {
                id = op.id
                if (op.titleRes != View.NO_ID) {
                    text = context.getString(op.titleRes)
                } else if (op.title != null) {
                    text = op.title
                }
                if (op.iconMaker != null) {
                    icon = op.iconMaker!!.invoke(context)
                } else if (op.iconUnicode != null) {
                    icon = createFontIcon(context, op.iconUnicode)
                }
                iconTint = MaterialColors.getColorStateListOrNull(context, M3R.attr.colorSecondary)
                iconPadding = if (text.isNullOrEmpty()) 0 else 4.dp
                setPadding(8.dp, 0, 12.dp, 0)
                minWidth = 0
                minimumWidth = 0
                isSaveEnabled = false
            }
            binding.btGroup.addView(button)
        }
        val op = findOptionByValue(selectedValue)
        if (op != null) {
            binding.btGroup.check(op.id)
        }
        binding.btGroup.addOnButtonCheckedListener(this)
        return binding.root
    }

    override fun onButtonChecked(
        group: MaterialButtonToggleGroup,
        checkedId: Int,
        isChecked: Boolean,
    ) {
        if (isChecked) {
            val op = findOptionById(checkedId) ?: return
            preformOptionSelected(group.context, op)
        }
    }

    private fun findOptionById(id: Int): Op? {
        return options.find { it.id == id }
    }

    private fun findOptionByValue(value: Int): Op? {
        return options.find { it.value == value }
    }

    private fun preformOptionSelected(context: Context, option: Op) {
        val newValue = option.value
        if (newValue == selectedValue) {
            return
        }
        setValue(context, newValue)
        selectedValue = newValue
        onOptionSelected(context, option)
    }

    open fun onOptionSelected(context: Context, option: Op) {}
}

private fun createShapeIcon(context: Context, index: Int): Drawable {
    val bitmap = createBitmap(20.dp, 20.dp, Bitmap.Config.ARGB_8888)
    val pathStr = IconShapePref.getMaskPathByIndex(context, index)
    val shapePath = AlterableAdaptiveIconDrawable.getMaskPath(
        pathStr, bitmap.width, bitmap.height
    )
    bitmap.applyCanvas {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        drawPath(shapePath, paint)

        setBitmap(null)
    }
    return BitmapDrawable(context.resources, bitmap)
}

private fun iconShapeOp(index: Int): SettingPref.Op {
    return SettingPref.Op(index).apply {
        iconMaker = {
            createShapeIcon(it, this.value)
        }
    }
}

class IconShapePref : SettingPref(
    "pref_key_override_icon_shape",
    listOf(
        Op(-1, titleRes = R.string.summary_follow_system, iconUnicode = Icons.Rounded.android),
        iconShapeOp(0),
        iconShapeOp(1),
        iconShapeOp(2),
        iconShapeOp(3),
        iconShapeOp(4),
        iconShapeOp(5),
        iconShapeOp(6),
    ),
    -1
) {
    companion object {
        const val ACTION_CHANGED = "com.dede.easter_eggs.IconShapeChanged"

        fun getMaskPath(context: Context): String {
            val index = IconShapePref().getSelectedOp(context)?.value ?: 0
            return getMaskPathByIndex(context, index)
        }

        fun getMaskPathByIndex(context: Context, index: Int): String {
            val paths = context.resources.getStringArray(R.array.icon_shape_override_paths)
            return paths[index % paths.size]
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_icon_shape_override

    override fun onOptionSelected(context: Context, option: Op) {
        LocalEvent.get(context).apply {
            post(ACTION_CHANGED)
            post(SettingsPrefs.ACTION_CLOSE_SETTING)
        }
    }
}

class IconVisualEffectsPref : SettingPref(
    "pref_key_icon_visual_effects",
    listOf(
        Op(Op.ON, titleRes = R.string.preference_on, iconUnicode = Icons.Outlined.animation),
        Op(Op.OFF, titleRes = R.string.preference_off)
    ),
    Op.OFF
) {
    companion object {
        const val ACTION_CHANGED = "com.dede.android_eggs.IconVisualEffectsChanged"
        const val EXTRA_VALUE = "extra_value"

        fun isEnable(context: Context): Boolean {
            return IconVisualEffectsPref().getSelectedOp(context).isEnable()
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_icon_visual_effects

    override fun onOptionSelected(context: Context, option: Op) {
        LocalEvent.get(context).apply {
            post(SettingsPrefs.ACTION_CLOSE_SETTING)
            post(ACTION_CHANGED, bundleOf(EXTRA_VALUE to option.isEnable()))
        }
    }
}

class EdgePref : SettingPref(
    "pref_key_edge",
    listOf(
        Op(Op.ON, titleRes = R.string.preference_on, iconUnicode = Icons.Rounded.fullscreen),
        Op(Op.OFF, titleRes = R.string.preference_off, iconUnicode = Icons.Rounded.fullscreen_exit)
    ),
    Op.ON
) {
    companion object {

        const val ACTION_CHANGED = "com.dede.android_eggs.EdgeToEdgeChanged"

        @SuppressLint("RestrictedApi")
        fun applyEdge(context: Context, window: Window) {
            val edgeToEdgeEnabled = EdgePref().getSelectedOp(context).isEnable()
            EdgeToEdgeUtils.applyEdgeToEdge(window, edgeToEdgeEnabled)
        }

    }

    override val titleRes: Int
        get() = R.string.pref_title_edge

    @SuppressLint("RestrictedApi")
    override fun onOptionSelected(context: Context, option: Op) {
        LocalEvent.get(context).post(ACTION_CHANGED)
        ContextUtils.getActivity(context)?.recreate()
    }
}

class DynamicColorPref : SettingPref(
    "pref_key_dynamic_color",
    listOf(
        Op(Op.ON, titleRes = R.string.preference_on, iconUnicode = Icons.Rounded.palette),
        Op(Op.OFF, titleRes = R.string.preference_off)
    ),
    if (DynamicColors.isDynamicColorAvailable()) Op.ON else Op.OFF
), DynamicColors.Precondition, DynamicColors.OnAppliedCallback {

    override val titleRes: Int
        get() = R.string.pref_title_dynamic_color
    override val enable: Boolean
        get() = DynamicColors.isDynamicColorAvailable()

    override fun onOptionSelected(context: Context, option: Op) {
        DynamicColors.applyToActivitiesIfAvailable(
            context.applicationContext as Application,
            DynamicColorsOptions.Builder()
                .setPrecondition(this)
                .setOnAppliedCallback(this)
                .build()
        )
        @Suppress("RestrictedApi")
        ContextUtils.getActivity(context)?.recreate()
    }

    override fun shouldApplyDynamicColors(activity: Activity, theme: Int): Boolean {
        if (activity is AppCompatActivity) {
            return getSelectedOp(activity).isEnable()
        }
        return false
    }

    override fun onApplied(activity: Activity) {
        HarmonizedColors.applyToContextIfAvailable(
            activity, HarmonizedColorsOptions.createMaterialDefaults()
        )
    }
}

class NightModePref : SettingPref(
    "pref_key_night_mode",
    listOf(
        Op(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            titleRes = R.string.summary_follow_system,
            iconUnicode = Icons.Rounded.brightness_auto
        ),
        Op(
            AppCompatDelegate.MODE_NIGHT_NO,
            titleRes = R.string.summary_theme_light_mode,
            iconUnicode = Icons.Rounded.brightness_7
        ),
        Op(
            AppCompatDelegate.MODE_NIGHT_YES,
            titleRes = R.string.summary_theme_dark_mode,
            iconUnicode = Icons.Rounded.brightness_4
        )
    ),
    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
) {
    companion object {
        fun isSystemNightMode(context: Context): Boolean {
            return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_theme

    override fun onOptionSelected(context: Context, option: Op) {
        val mode = option.value
        if (mode == AppCompatDelegate.getDefaultNightMode()) {
            return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}