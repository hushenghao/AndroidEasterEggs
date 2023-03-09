package com.dede.android_eggs.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import androidx.core.graphics.withTranslation
import androidx.preference.Preference
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.basic.dp
import com.google.android.material.color.MaterialColors
import com.google.android.material.R as M3R


class VersionPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {

    init {
        isPersistent = false
        isCopyingEnabled = true
        title = context.getString(R.string.title_version)
        icon = FontIconsDrawable(context, Icons.INFO, 36f).apply {
            setPadding(12.dp, 6.dp, 0, 0)
        }
        val versionLabel = context.getString(
            R.string.label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        summary = SpannableStringBuilder(versionLabel)
            .append(" ")
            .append(" ", createImageSpan(context, R.drawable.ic_git_tree))
            .append(
                BuildConfig.GIT_HASH,
                ForegroundColorSpan(
                    MaterialColors.getColor(context, M3R.attr.colorAccent, Color.WHITE)
                ),
                AbsoluteSizeSpan(11, true)
            )
    }

    private fun createImageSpan(context: Context, res: Int): ImageSpan {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(context, res, ImageSpan.ALIGN_CENTER)
        } else {
            object : ImageSpan(context, res) {
                override fun draw(
                    canvas: Canvas,
                    text: CharSequence?,
                    start: Int,
                    end: Int,
                    x: Float,
                    top: Int,
                    y: Int,
                    bottom: Int,
                    paint: Paint,
                ) {
                    val d: Drawable = drawable
                    val transY = top + (bottom - top) / 2f - d.bounds.height() / 2f
                    canvas.withTranslation(x, transY) {
                        d.draw(this)
                    }
                }
            }
        }
    }

    private fun SpannableStringBuilder.append(
        text: CharSequence?,
        vararg whats: Any,
    ): SpannableStringBuilder {
        if (text.isNullOrEmpty()) return this

        val start = length
        append(text)
        for (what in whats) {
            setSpan(what, start, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        return this
    }
}