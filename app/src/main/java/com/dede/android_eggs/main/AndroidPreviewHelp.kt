package com.dede.android_eggs.main

import android.app.Activity
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.DialogAndroidTimelineBinding
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.ThemeUtils
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.SnapshotProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Singleton
import kotlin.math.roundToInt

object AndroidPreviewHelp {

    @Module
    @InstallIn(SingletonComponent::class)
    class PreviewEasterEgg : EasterEggProvider {
        @Provides
        @IntoSet
        @Singleton
        override fun provideEasterEgg(): BaseEasterEgg {
            return object : EasterEgg(
                R.drawable.android_15_logo,
                R.string.nickname_android_vanilla_ice_cream,
                R.string.nickname_android_vanilla_ice_cream,
                35,
                true
            ) {
                override fun provideEasterEgg(): Class<out Activity>? {
                    return null
                }

                override fun easterEggAction(context: Context): Boolean {
                    showTimelineDialog(
                        context,
                        R.drawable.android_15_platlogo,
                        R.string.nickname_android_vanilla_ice_cream
                    )
                    return true
                }

                override fun provideSnapshotProvider(): SnapshotProvider {
                    return object : SnapshotProvider() {
                        override fun create(context: Context): View {
                            return ImageView(context).apply {
                                setImageResource(R.drawable.android_15_platlogo)
                            }
                        }
                    }
                }

                override fun getReleaseDate(): Date {
                    val calendar = Calendar.getInstance(TimeZone.getDefault())
                    calendar.set(Calendar.YEAR, TIMELINE_YEAR)
                    calendar.set(Calendar.MONTH, Calendar.SEPTEMBER)
                    return calendar.time
                }
            }
        }

    }

    private const val TIMELINE_YEAR = 2024// android v
    const val API = 35// android v
    const val API_VERSION_NAME = "15"// android v

    private fun getTimelineMessage(context: Context): CharSequence {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        return if (year > TIMELINE_YEAR) {
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
                    Uri.parse(context.getString(R.string.url_android_releases))
                )
            }
            .show()
    }

}