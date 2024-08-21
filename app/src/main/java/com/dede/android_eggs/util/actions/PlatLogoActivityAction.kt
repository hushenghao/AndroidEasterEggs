package com.dede.android_eggs.util.actions

import android.app.Activity
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp
import com.dede.basic.isPlatLogoActivity
import com.dede.basic.platLogoEdge2Edge

class PlatLogoActivityAction : ActivityActionDispatcher.ActivityAction {

    override fun onPreCreate(activity: Activity) {
        if (activity.isPlatLogoActivity) {
            activity.platLogoEdge2Edge()
        }
    }

    override fun onCreate(activity: Activity) {
        if (activity.isPlatLogoActivity) {
            EasterEggShortcutsHelp.reportShortcutUsed(activity, activity.intent)
        }
    }
}
