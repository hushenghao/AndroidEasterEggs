package com.dede.android_eggs.main.holders

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.StateSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.withStyledAttributes
import com.dede.android_eggs.main.EggActionController
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.util.resolveColorStateList
import com.google.android.material.R
import com.google.android.material.color.MaterialColors
import com.google.android.material.resources.MaterialAttributes

@VHType(viewType = Egg.VIEW_TYPE_PREVIEW)
class PreviewHolder(view: View) : EggHolder(view) {

    @Suppress("SameParameterValue")
    private fun createHarmonizeWithPrimaryColorStateList(
        context: Context, @ColorInt color: Int,
    ): ColorStateList {
        val stateSet = intArrayOf(android.R.attr.state_pressed)
        val defaultColor = MaterialColors.harmonizeWithPrimary(context, color)

        var pressedColor = context.resolveColorStateList(
            R.attr.materialCardViewFilledStyle, R.attr.cardBackgroundColor
        )?.getColorForState(stateSet, defaultColor) ?: defaultColor
        pressedColor = MaterialColors.harmonize(color, pressedColor)

        return ColorStateList(
            arrayOf(stateSet, StateSet.WILD_CARD),
            intArrayOf(pressedColor, defaultColor)
        )
    }

    @SuppressLint("RestrictedApi")
    private fun getLightTextColor(context: Context, @AttrRes textAppearanceAttrRes: Int): Int {
        // always use dark mode color
        val wrapper = ContextThemeWrapper(context, R.style.Theme_Material3_DynamicColors_Dark)
        val value = MaterialAttributes.resolve(wrapper, textAppearanceAttrRes)
        var color = Color.WHITE
        if (value != null) {
            wrapper.withStyledAttributes(
                value.resourceId,
                intArrayOf(android.R.attr.textColor)
            ) {
                color = getColor(0, color)
            }
        }
        return color
    }

    override fun onBindViewHolder(egg: Egg) {
        super.onBindViewHolder(egg)
        val colorStateList =
            createHarmonizeWithPrimaryColorStateList(context, 0xFF073042.toInt())
        val titleTextColor = getLightTextColor(context, R.attr.textAppearanceHeadlineSmall)
        val summaryTextColor = getLightTextColor(context, R.attr.textAppearanceBodyMedium)
        binding.tvTitle.setTextColor(titleTextColor)
        binding.tvSummary.setTextColor(summaryTextColor)
        binding.cardView.setCardBackgroundColor(colorStateList)
        binding.tvSummary.text = EggActionController.getTimelineMessage(context)
        itemView.setOnClickListener {
            EggActionController.showTimelineDialog(
                context,
                com.dede.android_eggs.R.drawable.ic_android_udc,
                com.dede.android_eggs.R.string.title_android_u
            )
        }
    }
}