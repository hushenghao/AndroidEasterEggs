package com.dede.android_eggs.util.actions

import android.app.Activity
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.basic.platLogoEdge2Edge

class PlatLogoEdge2EdgeAction : ActivityActionDispatcher.ActivityAction {

    override fun onCreate(activity: Activity) {
        if (activity.javaClass.simpleName == "PlatLogoActivity") {
            activity.platLogoEdge2Edge()
        }
    }
}