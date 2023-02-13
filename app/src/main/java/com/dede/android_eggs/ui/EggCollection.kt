package com.dede.android_eggs.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder

/**
 * Egg
 *
 * @author hsh
 * @since 2020/11/11 2:31 PM
 */
class EggCollection : PreferenceCategory {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var clickCount = 0
    private var isChecked = false

    init {
        isPersistent = true
    }

    private fun canApplyChecked(): Boolean {
        return context.resources.configuration.smallestScreenWidthDp >= 400
    }

    override fun onClick() {
        if (!canApplyChecked()) {
            return
        }

        if (clickCount < 4) {
            clickCount++
            return
        }
        clickCount = 0
        val newValue = !isChecked()
        if (callChangeListener(newValue)) {
            setChecked(newValue)
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Boolean {
        return a.getBoolean(index, false)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        setChecked(getPersistedBoolean(defaultValue as? Boolean ?: false))
    }

    fun setChecked(newValue: Boolean) {
        val changed = newValue != this.isChecked
        if (changed) {
            this.isChecked = newValue
            persistBoolean(newValue)
            notifyChanged()
        }
    }

    fun isChecked(): Boolean {
        return isChecked
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun isSelectable(): Boolean {
        return true
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        holder.itemView.updateLayoutParams<MarginLayoutParams> {
            topMargin = 0
        }
        super.onBindViewHolder(holder)
    }
}