package com.dede.android_eggs.views.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import com.dede.android_eggs.databinding.ItemSettingPrefGroupBinding
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.settings.prefs.DynamicColorPref
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.android_eggs.views.settings.prefs.IconVisualEffectsPref
import com.dede.android_eggs.views.settings.prefs.LanguagePref
import com.dede.android_eggs.views.settings.prefs.NightModePref
import com.dede.basic.dp
import com.dede.basic.requireDrawable
import com.google.android.material.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.color.MaterialColors


object SettingsPrefs {

    const val ACTION_CLOSE_SETTING = "com.dede.easter_eggs.CloseSetting"

    fun providerPrefs(): List<SettingPref> = listOf(
        NightModePref(),
        LanguagePref(),
        IconShapePref(),
        IconVisualEffectsPref(),
        DynamicColorPref(),
    ).filter { it.enable }

    fun apply(context: Context) {
        NightModePref().apply(context)
        DynamicColorPref().apply(context)
    }
}

/**
 * Abs Setting Pref
 */
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
        @DrawableRes val iconRes: Int = View.NO_ID,
    ) {

        val id = ViewCompat.generateViewId()

        var iconMaker: ((context: Context, view: View) -> Drawable)? = null

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

    open fun getValue(context: Context, default: Int): Int {
        if (key == null) return default
        return context.pref.getInt(key, default)
    }

    open fun setValue(context: Context, value: Int): Boolean {
        if (key == null) return false
        context.pref.edit().putInt(key, value).apply()
        return true
    }

    fun getSelectedOp(context: Context): Op? {
        selectedValue = getValue(context, selectedValue)
        return findOptionByValue(selectedValue)
    }

    open fun apply(context: Context) {
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
                R.attr.materialButtonOutlinedStyle
            ).apply {
                id = op.id
                if (op.titleRes != View.NO_ID) {
                    text = context.getString(op.titleRes)
                } else if (op.title != null) {
                    text = op.title
                }
                val iconMaker = op.iconMaker
                if (iconMaker != null) {
                    icon = iconMaker.invoke(context, this)
                } else if (op.iconUnicode != null) {
                    icon = FontIconsDrawable(context, op.iconUnicode, R.attr.colorSecondary, 24f)
                } else if (op.iconRes != View.NO_ID) {
                    icon = context.requireDrawable(op.iconRes)
                    iconSize = 24.dp
                }
                iconTint = MaterialColors.getColorStateListOrNull(context, R.attr.colorSecondary)
                iconPadding = if (text.isNullOrEmpty()) 0 else 4.dp
                setPadding(12.dp, 0, 12.dp, 0)
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
