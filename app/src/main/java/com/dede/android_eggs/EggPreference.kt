package com.dede.android_eggs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

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
    }

    private val outlineProvider: ViewOutlineProvider?

    private val className: String?
    private val iconPadding: Int

    private val finalTitle: CharSequence?

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
        className = arrays.getString(R.styleable.EggPreference_android_targetClass)
        iconPadding =
            arrays.getDimensionPixelSize(R.styleable.EggPreference_iconPadding, 0)
        arrays.recycle()

        finalTitle = title
        isPersistent = false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        if (!showSuffix && finalTitle != null) {
            val title = holder.findViewById(android.R.id.title) as? TextView
            title?.text = finalTitle.replace(suffixRegex,"")
        }

        val icon = holder.findViewById(android.R.id.icon) as? ImageView ?: return
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            // support adaptive-icon
            icon.clipToOutline = outlineProvider != null
            icon.outlineProvider = outlineProvider
        }
        icon.setPadding(iconPadding)
    }

    @SuppressLint("RestrictedApi")
    override fun performClick() {
        val className = className
        if (className != null) {
            val context = context
            val intent = Intent()
                .setClassName(context, className)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            context.startActivity(intent)
            return
        }
        super.performClick()
    }

    private class CornersOutlineProvider(val radius: Float) : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(view.paddingLeft, view.paddingTop,
                view.width - view.paddingRight,
                view.height - view.paddingBottom, radius)
        }
    }

    override fun setIntent(intent: Intent?) {
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        super.setIntent(intent)
    }
}