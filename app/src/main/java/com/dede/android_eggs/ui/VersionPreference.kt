package com.dede.android_eggs.ui

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.dede.android_eggs.R
import com.dede.android_eggs.BuildConfig
import com.dede.basic.dp


class VersionPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {

    init {
        isPersistent = false
        isCopyingEnabled = true
        title = context.getString(R.string.title_version)
        icon = FontIconsDrawable(context, Icons.INFO, 36f).apply {
            setPadding(12.dp, 6.dp, 0, 0)
        }
        summary = context.getString(
            R.string.label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
    }
}