package com.dede.android_eggs.ui

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.DialogAndroidTimelineBinding
import com.dede.android_eggs.util.ChromeTabsBrowser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar
import kotlin.math.roundToInt

class TimelinePreference(context: Context, attrs: AttributeSet?) : EggPreference(context, attrs) {

    companion object {
        private const val TIMELINE_YEAR = 2023
    }

    init {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        summary = if (year > TIMELINE_YEAR) {
            context.getString(R.string.summary_android_release_pushed)
        } else {
            context.getString(R.string.summary_android_waiting)
        }
    }

    override fun performClick() {
        val binding = DialogAndroidTimelineBinding.inflate(LayoutInflater.from(context))

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)// [0, 11]
        // Month    Progress    Calender.MONTH
        // Feb          0           1
        // ...
        // Jul          5           6
        // Aug          -           7
        if (year < TIMELINE_YEAR || (year == TIMELINE_YEAR && month < Calendar.FEBRUARY)) {
            // No preview
            binding.ivRelease.isVisible = false
            binding.progressTimeline.isVisible = false
        } else if (year == TIMELINE_YEAR && month in Calendar.FEBRUARY..Calendar.JULY) {
            // Preview
            binding.progressTimeline.isEnabled = false// Untouchable
            binding.progressTimeline.isVisible = true
            binding.ivRelease.isVisible = false
            val progress = month - 1
            binding.progressTimeline.progress = progress
            binding.scrollContent.doOnPreDraw {
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

        MaterialAlertDialogBuilder(context)
            .setIcon(icon)
            .setTitle(title)
            .setMessage(summary)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(R.string.label_timeline_releases) { _, _ ->
                ChromeTabsBrowser.launchUrl(
                    context,
                    Uri.parse(context.getString(R.string.url_android_releases))
                )
            }
            .show()
    }
}