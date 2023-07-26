package com.dede.android_eggs.main.holders

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.util.StateSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.withStyledAttributes
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.dede.android_eggs.databinding.DialogAndroidTimelineBinding
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.isSystemNightMode
import com.dede.android_eggs.util.resolveColorStateList
import com.google.android.material.R
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.resources.MaterialAttributes
import java.util.*
import kotlin.math.roundToInt

@VHType(viewType = Egg.VIEW_TYPE_PREVIEW)
class PreviewHolder(view: View) : EggHolder(view) {

    companion object {
        private const val TIMELINE_YEAR_UDC = 2023// android udc
    }

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
        binding.tvSummary.text = getTimelineMessage(context)
        binding.cardView.setOnClickListener {
            showTimelineDialog(
                context,
                com.android_u.egg.R.drawable.u_android14_patch_adaptive,
                com.dede.android_eggs.R.string.title_android_u
            )
        }
    }

    private fun getTimelineMessage(context: Context): CharSequence {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        return if (year > TIMELINE_YEAR_UDC) {
            context.getString(com.dede.android_eggs.R.string.summary_android_release_pushed)
        } else {
            context.getString(com.dede.android_eggs.R.string.summary_android_waiting)
        }
    }

    private fun showTimelineDialog(
        context: Context,
        @DrawableRes iconResId: Int,
        @StringRes titleRes: Int,
    ) {
        val binding = DialogAndroidTimelineBinding.inflate(LayoutInflater.from(context))

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)// [0, 11]
        // Month    Progress    Calender.MONTH
        // Feb          0           1
        // ...
        // Jul          5           6
        // Aug          -           7
        if (year < TIMELINE_YEAR_UDC || (year == TIMELINE_YEAR_UDC && month < Calendar.FEBRUARY)) {
            // No preview
            binding.ivRelease.isVisible = false
            binding.progressTimeline.isVisible = false
        } else if (year == TIMELINE_YEAR_UDC && month in Calendar.FEBRUARY..Calendar.JULY) {
            // Preview
            binding.progressTimeline.isEnabled = false// Untouchable
            binding.progressTimeline.isVisible = true
            binding.ivRelease.isVisible = false
            val progress = month - 1
            binding.progressTimeline.progress = progress
            binding.scrollContent.doOnPreDraw {
                binding.progressTimeline.updatePadding(
                    left = (50f / 789f * it.width).roundToInt(),
                    right = (220f / 789f * it.width).roundToInt()
                )
                val x = (it.width * (progress / 8f)).roundToInt()
                binding.scrollView.smoothScrollTo(x, 0)
            }
        } else {
            // Final release
            binding.progressTimeline.isVisible = false
            binding.ivRelease.isVisible = true
            binding.scrollContent.doOnPreDraw {
                binding.scrollView.smoothScrollTo(it.width, 0)
            }
        }
        if (isSystemNightMode(context)) {
            binding.ivTimeline.drawable?.apply {
                val matrix = ColorMatrix()
                // Increase the overall brightness and more blue brightness
                matrix.setScale(1.3f, 1.5f, 2f, 1f)
                colorFilter = ColorMatrixColorFilter(matrix)
            }
        }

        MaterialAlertDialogBuilder(context)
            .setIcon(iconResId)
            .setTitle(titleRes)
            .setMessage(getTimelineMessage(context))
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(com.dede.android_eggs.R.string.label_timeline_releases) { _, _ ->
                CustomTabsBrowser.launchUrl(
                    context,
                    Uri.parse(context.getString(com.dede.android_eggs.R.string.url_android_releases))
                )
            }
            .show()
    }

}