package com.dede.android_eggs.util.actions

import android.app.Activity
import androidx.activity.EdgeToEdgeCompat
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp
import com.dede.basic.Utils.isPlatLogoActivity

internal class PlatLogoActivityAction : ActivityActionDispatcher.ActivityAction {

    override fun onPreCreate(activity: Activity) {
        if (activity.isPlatLogoActivity) {
            EdgeToEdgeCompat.enable(activity)
        }
    }

    override fun onCreate(activity: Activity) {
        if (activity.isPlatLogoActivity) {
            EasterEggShortcutsHelp.autoReportShortcutUsed(activity, activity.intent)
        }
    }
}
