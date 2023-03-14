package com.dede.android_eggs.main

import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.DialogAndroidTimelineBinding
import com.dede.android_eggs.settings.NightModePref
import com.dede.android_eggs.ui.SupportAdaptiveIconTransformation
import com.dede.android_eggs.util.ChromeTabsBrowser
import com.dede.android_eggs.util.IconShapeOverride
import com.dede.android_eggs.util.applyIf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.math.roundToInt
import com.google.android.material.R as M3R


class EggActionController(val context: Context) {

    companion object {
        const val ACTIVITY_TASK_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS

        fun ImageRequest.Builder.applySupportAdaptiveIcon(
            context: Context,
            supportAdaptiveIcon: Boolean,
        ) {
            if (!supportAdaptiveIcon || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return
            }
            val shapePath = IconShapeOverride.getAppliedValue(context)
            if (!IconShapeOverride.isSquareShape(context, shapePath)) {
                if (!TextUtils.isEmpty(shapePath)) {
                    transformations(SupportAdaptiveIconTransformation(shapePath))
                } else {
                    transformations(CircleCropTransformation())
                }
            }
        }

        const val EXTRA_O_POINT = "isOreoPoint"

        const val KEY_EGG_T = "key_egg_t"
        const val KEY_EGG_S = "key_egg_s"
        const val KEY_EGG_R = "key_egg_r"
        const val KEY_EGG_Q = "key_egg_q"
        const val KEY_EGG_P = "key_egg_p"
        const val KEY_EGG_O_POINT = "key_egg_o_1"
        const val KEY_EGG_O = "key_egg_o"
        const val KEY_EGG_N = "key_egg_n"
        const val KEY_EGG_M = "key_egg_m"
        const val KEY_EGG_L = "key_egg_l"
        const val KEY_EGG_K = "key_egg_k"
        const val KEY_EGG_J = "key_egg_j"
        const val KEY_EGG_I = "key_egg_i"
        const val KEY_EGG_H = "key_egg_h"
        const val KEY_EGG_G = "key_egg_g"

        private const val TIMELINE_YEAR = 2023

        fun getTimelineMessage(context: Context): CharSequence {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            return if (year > TIMELINE_YEAR) {
                context.getString(R.string.summary_android_release_pushed)
            } else {
                context.getString(R.string.summary_android_waiting)
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
            if (NightModePref.isSystemNightMode(context)) {
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
                .setNeutralButton(R.string.label_timeline_releases) { _, _ ->
                    ChromeTabsBrowser.launchUrl(
                        context,
                        Uri.parse(context.getString(R.string.url_android_releases))
                    )
                }
                .show()
        }
    }

    private fun createIntent(@StringRes classRes: Int): Intent? {
        if (classRes == -1) return null
        return Intent(Intent.ACTION_VIEW)
            .setClassName(context, context.getString(classRes))
            .addFlags(ACTIVITY_TASK_FLAGS)
    }

    fun onClick(egg: EasterEggListFragment.Egg) {
        val intent = createIntent(egg.targetClassRes) ?: return
        if (egg.extras != null)
            intent.putExtras(egg.extras)
        context.startActivity(intent)
    }

    fun showVersionCommentDialog(egg: EasterEggListFragment.Egg) {
        showDialogInternal(
            egg.iconRes,
            egg.androidRes,
            egg.versionCommentRes,
            egg.shortcutKey,
            context.getString(egg.eggNameRes),
            createIntent(egg.targetClassRes)
        )
    }

    private fun showDialogInternal(
        @DrawableRes iconResId: Int,
        @StringRes title: Int,
        @StringRes message: Int,
        shortcutKey: String?,
        shortcutLabel: String?,
        shortcutIntent: Intent?,
    ) {
        val supportShortcut =
            shortcutKey != null && shortcutLabel != null && shortcutIntent != null &&
                    ShortcutManagerCompat.isRequestPinShortcutSupported(context)
        MaterialAlertDialogBuilder(
            context,
            M3R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setIcon(iconResId)
            .setTitle(title)
            .setMessage(message)
            .applyIf(supportShortcut) {
                setNeutralButton(R.string.label_add_shortcut) { _, _ ->
                    val icon = IconCompat.createWithResource(context, iconResId)
                    val shortcut = ShortcutInfoCompat.Builder(context, shortcutKey!!)
                        .setIcon(icon)
                        .setIntent(shortcutIntent!!)
                        .setShortLabel(shortcutLabel!!)
                        .build()
                    ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
                }
            }
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}