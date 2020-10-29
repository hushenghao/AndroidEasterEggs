package com.dede.android_eggs

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.preference.Preference

/**
 * Easter Egg Preference
 *
 * @author hsh
 * @since 2020/10/29 10:29 AM
 */
class EggPreference : Preference {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun setIntent(intent: Intent?) {
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        super.setIntent(intent)
    }
}