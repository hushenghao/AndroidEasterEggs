package com.dede.android_eggs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Easter Egg Preference
 *
 * @author hsh
 * @since 2020/10/29 10:29 AM
 */
class EggPreference : Preference {

    companion object {
        private const val MODE_DEFAULT = 0
        private const val MODE_CORNERS = 1
        private const val MODE_OVAL = 2

        var showSuffix = true
        private val suffixRegex = Regex("\\s*\\(.+\\)")
        private val versionRegex = Regex("Android\\s*\\d+(.\\d)*(.\\*)?")

        private const val ACTIVITY_TASK_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS
    }

    private val outlineProvider: ViewOutlineProvider?

    private val iconPadding: Int

    private val finalTitle: CharSequence?
    private val finalSummary: CharSequence?
    private val versionComment: String?

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val arrays = context.obtainStyledAttributes(attrs, R.styleable.EggPreference)
        val supportAdaptiveIcon =
            arrays.getInt(R.styleable.EggPreference_supportAdaptiveIcon, MODE_DEFAULT)
        outlineProvider = when (supportAdaptiveIcon) {
            MODE_CORNERS -> {
                val radius = arrays.getDimension(R.styleable.EggPreference_iconRadius, 0f)
                CornersOutlineProvider(radius)
            }
            MODE_OVAL -> OvalOutlineProvider()
            else -> null
        }
        val className = arrays.getString(R.styleable.EggPreference_android_targetClass)
        if (className != null) {
            val intent = Intent()
                .setClassName(context, className)
            setIntent(intent)
        }
        iconPadding =
            arrays.getDimensionPixelSize(R.styleable.EggPreference_iconPadding, 0)
        versionComment = arrays.getString(R.styleable.EggPreference_versionComment)
        arrays.recycle()

        finalTitle = title
        finalSummary = summary
        isPersistent = false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val title = holder.findViewById(android.R.id.title) as? TextView
        if (title != null) {
            if (!showSuffix && finalTitle != null) {
                title.text = finalTitle.replace(suffixRegex, "")
            }

            if (versionComment != null) {
                val text = title.text.toString()
                val result = versionRegex.find(text)
                if (result != null) {
                    val range = result.range
                    title.movementMethod = LinkMovementMethod.getInstance()
                    title.highlightColor = Color.TRANSPARENT
                    val span = SpannableString(text)
                    if (range.first > 0) {
                        span.setSpan(
                            DefaultClickSpan(this),
                            0,
                            range.first,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                    if (range.last + 1 < text.length) {
                        span.setSpan(
                            DefaultClickSpan(this),
                            range.last + 1,
                            text.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    span.setSpan(
                        CommentClickSpan(icon, finalTitle, versionComment),
                        range.first,
                        range.last + 1,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    title.text = span
                }
            }
        }

        if (!showSuffix && finalSummary != null) {
            val summary = holder.findViewById(android.R.id.summary) as? TextView
            summary?.text = finalSummary.replace(suffixRegex, "")
        }

        val icon = holder.findViewById(android.R.id.icon) as? ImageView ?: return
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || icon.drawable !is AdaptiveIconDrawable) {
            // support adaptive-icon
            icon.clipToOutline = outlineProvider != null
            icon.outlineProvider = outlineProvider
        }
        icon.setPadding(iconPadding)
    }

    @SuppressLint("RestrictedApi")
    override fun performClick() {
        val intent = this.intent
        if (intent != null) {
            intent.addFlags(ACTIVITY_TASK_FLAGS)
            super.performClick()
            return
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(finalTitle)
            .setMessage(finalSummary)
            .setIcon(icon)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private class DefaultClickSpan(val preference: Preference) : ClickableSpan() {
        @SuppressLint("RestrictedApi")
        override fun onClick(widget: View) {
            preference.performClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }

    private class CommentClickSpan(
        val icon: Drawable?,
        val title: CharSequence?,
        val message: CharSequence?
    ) : ClickableSpan() {
        override fun onClick(widget: View) {
            MaterialAlertDialogBuilder(widget.context)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }

        override fun updateDrawState(ds: TextPaint) {
            //super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    private class CornersOutlineProvider(val radius: Float) : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(
                view.paddingLeft, view.paddingTop,
                view.width - view.paddingRight,
                view.height - view.paddingBottom, radius
            )
        }
    }

}