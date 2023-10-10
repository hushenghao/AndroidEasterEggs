package com.dede.android_eggs.views.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
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
        LanguagePref.resetApi23Locale()
    }
}

/**
 * Abs Setting Pref
 */
abstract class SettingPref(
    private val key: String? = null,
    options: List<Op>,
    default: Int = 0,
) : MaterialButtonToggleGroup.OnButtonCheckedListener {

    open class Op(
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

        override fun hashCode(): Int {
            return value
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Op

            if (value != other.value) return false

            return true
        }

    }

    private var _options: MutableList<Op> = options.toMutableList()

    private var selectedValue: Int = default

    open val options: List<Op>
        get() = _options

    open val currentSelectedValue: Int
        get() = selectedValue

    open val enable: Boolean = true
    open val title: CharSequence? = null

    @StringRes
    open val titleRes: Int = View.NO_ID

    private lateinit var binding: ItemSettingPrefGroupBinding

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

    protected fun updateOptions(options: List<Op>) {
        if (this._options !== options) {
            this._options = options.toMutableList()
        }

        val context = binding.btGroup.context
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
                if (text.isNullOrEmpty()) {
                    iconPadding = 0
                } else {
                    iconPadding = 4.dp
                    TooltipCompat.setTooltipText(this, text)
                }
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
    }

    open fun onCreateView(context: Context): View {
        binding = ItemSettingPrefGroupBinding.inflate(LayoutInflater.from(context))
        selectedValue = getValue(context, selectedValue)
        if (titleRes != View.NO_ID) {
            binding.tvTitle.setText(titleRes)
        } else if (title != null) {
            binding.tvTitle.text = title
        }
        updateOptions(_options)
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
        if (onPreOptionSelected(context, option)) {
            return
        }
        setValue(context, newValue)
        selectedValue = newValue
        onOptionSelected(context, option)
    }

    private fun selectedOption(option: Op) {
        if (::binding.isInitialized) {
            binding.btGroup.check(option.id)
        } else {
            selectedValue = option.value
        }
    }

    fun selectedOptionByValue(value: Int) {
        val selectedOp = options.find { it.value == value }
        if (selectedOp != null) {
            selectedOption(selectedOp)
        }
    }

    open fun onOptionSelected(context: Context, option: Op) {}

    open fun onPreOptionSelected(context: Context, option: Op): Boolean {
        return false
    }
}
