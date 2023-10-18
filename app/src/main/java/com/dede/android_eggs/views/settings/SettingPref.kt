package com.dede.android_eggs.views.settings

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import com.dede.android_eggs.util.pref
import com.google.android.material.internal.ContextUtils
import java.lang.ref.WeakReference

/**
 * Abs Setting Pref
 */
abstract class SettingPref(private val key: String? = null, options: List<Op>, default: Int = 0) {

    companion object {
        @SuppressLint("RestrictedApi")
        fun recreateActivityIfPossible(context: Context) {
            val activity = ContextUtils.getActivity(context)
            activity?.recreate()
        }
    }

    interface PrefViewListener {
        fun onSelectedOptionChange(op: Op)

        fun onUpdateOptions(options: List<Op>)
    }

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
            return value == other.value
        }

    }

    private var _options: MutableList<Op> = options.toMutableList()

    private var _selectedValue: Int = default

    open val options: List<Op>
        get() = _options

    open val currentSelectedValue: Int
        get() = _selectedValue

    open val enable: Boolean = true
    open val title: CharSequence? = null

    @StringRes
    open val titleRes: Int = View.NO_ID

    private var viewListener: WeakReference<PrefViewListener>? = null

    fun setViewListener(listener: PrefViewListener) {
        viewListener = WeakReference(listener)
    }

    private fun getViewListener(): PrefViewListener? {
        return viewListener?.get()
    }

    protected open fun getValue(context: Context, default: Int): Int {
        if (key == null) return default
        return context.pref.getInt(key, default)
    }

    protected open fun setValue(context: Context, value: Int): Boolean {
        if (key == null) return false
        context.pref.edit().putInt(key, value).apply()
        return true
    }

    fun getSelectedOption(context: Context): Op? {
        _selectedValue = getValue(context, _selectedValue)
        return findOptionByValue(_selectedValue)
    }

    fun setSelectedOptionByValue(context: Context, value: Int) {
        val selectedOp = findOptionByValue(value)
        if (selectedOp != null) {
            preformOptionSelected(context, selectedOp)
            getViewListener()?.onSelectedOptionChange(selectedOp)
        }
    }

    fun findOptionById(id: Int): Op? {
        return options.find { it.id == id }
    }

    private fun findOptionByValue(value: Int): Op? {
        return options.find { it.value == value }
    }

    fun preformOptionSelected(context: Context, option: Op) {
        val newValue = option.value
        if (newValue == _selectedValue) {
            return
        }
        if (onPreOptionSelected(context, option)) {
            return
        }
        setValue(context, newValue)
        _selectedValue = newValue
        apply(context, option)
    }

    fun updateOptions(options: List<Op>) {
        if (_options === options) {
            return
        }
        _options = options.toMutableList()
        getViewListener()?.onUpdateOptions(_options)
    }

    fun apply(context: Context) {
        if (!enable) return
        val option = getSelectedOption(context) ?: return
        apply(context, option)
    }

    abstract fun apply(context: Context, option: Op)

    open fun onPreOptionSelected(context: Context, option: Op): Boolean {
        return false
    }
}
