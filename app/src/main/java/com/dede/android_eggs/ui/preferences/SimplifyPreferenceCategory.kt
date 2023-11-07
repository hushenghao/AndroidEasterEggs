package com.dede.android_eggs.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder

class SimplifyPreferenceCategory @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : PreferenceCategory(context, attrs) {

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        // Simplify top margin
        holder.itemView.updateLayoutParams<MarginLayoutParams> {
            topMargin = 0
        }
    }
}