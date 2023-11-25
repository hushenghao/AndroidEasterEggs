package com.dede.android_eggs.main

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.dede.android_eggs.databinding.DialogAndroidTimelineBinding
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.ThemeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar
import kotlin.math.roundToInt

class AndroidPreviewHelp {
    companion object {
        private const val TIMELINE_YEAR_UDC = 2023// android udc
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

    fun showTimelineDialog(
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
        if (ThemeUtils.isSystemNightMode(context)) {
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