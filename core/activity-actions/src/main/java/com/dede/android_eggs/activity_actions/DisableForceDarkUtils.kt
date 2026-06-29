package com.dede.android_eggs.activity_actions

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi

object DisableForceDarkUtils {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun disableForceDark(activity: Activity) {
        activity.theme.applyStyle(R.style.ThemeOverlay_Disable_ForceDark, true)
    }
}
