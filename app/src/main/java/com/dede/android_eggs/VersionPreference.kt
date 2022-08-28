package com.dede.android_eggs

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.ScaleDrawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.dede.android_eggs.BuildConfig.VERSION_CODE
import com.dede.android_eggs.BuildConfig.VERSION_NAME

/**
 * show pkg version
 *
 * @author hsh
 * @since 2020/10/29 10:48 AM
 */
class VersionPreference : ChromeTabPreference {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        summary = context.getString(R.string.summary_version, VERSION_NAME, VERSION_CODE)
        val bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo_color_pgyer)
        val drawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
        drawable.isCircular = true
        icon = ScaleDrawable(drawable, Gravity.CENTER, 1f, 1f).apply {
            level = 8500
        }
    }
}