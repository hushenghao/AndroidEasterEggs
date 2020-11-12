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
    }

    private val outlineProvider: ViewOutlineProvider?

    private var className: String? = null

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
        arrays.recycle()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        val icon = holder?.findViewById(android.R.id.icon) as? ImageView ?: return
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            // support adaptive-icon
            icon.clipToOutline = outlineProvider != null
            icon.outlineProvider = outlineProvider
        }
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

    private class OvalOutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setOval(0, 0, view.width, view.height)
        }
    }

    private class CornersOutlineProvider(val radius: Float) : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
    }

    override fun setIntent(intent: Intent?) {
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        super.setIntent(intent)
    }
}