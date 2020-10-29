package com.dede.android_eggs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.dede.android_eggs.BuildConfig.VERSION_CODE
import com.dede.android_eggs.BuildConfig.VERSION_NAME

/**
 * show pkg version
 *
 * @author hsh
 * @since 2020/10/29 10:48 AM
 */
class VersionPreference : Preference {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        summary = context.getString(R.string.summary_version, VERSION_NAME, VERSION_CODE)
    }
}