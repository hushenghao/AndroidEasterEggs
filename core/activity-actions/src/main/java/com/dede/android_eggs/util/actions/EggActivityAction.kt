package com.dede.android_eggs.util.actions

import android.app.Activity
import androidx.activity.EdgeToEdgeCompat
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp

internal class EggActivityAction : ActivityActionDispatcher.ActivityAction {

    private val edgeToEdgePagers: Set<Class<out Activity>> = setOf(
        com.android_o.egg.octo.Ocquarium::class.java,
        com.android_m.egg.MLandActivity::class.java,
        com.android_l.egg.LLandActivity::class.java,
        com.android_k.egg.DessertCase::class.java,
        com.android_j.egg.BeanBag::class.java,
        com.android_i.egg.Nyandroid::class.java,
    )

    private val Activity.isPlatLogoActivity: Boolean
        get() = javaClass.simpleName == "PlatLogoActivity"

    override fun onPreCreate(activity: Activity) {
        if (activity.isPlatLogoActivity || edgeToEdgePagers.contains(activity.javaClass)) {
            EdgeToEdgeCompat.enable(activity)
        }
    }

    override fun onCreate(activity: Activity) {
        if (activity.isPlatLogoActivity) {
            EasterEggShortcutsHelp.autoReportShortcutUsed(activity, activity.intent)
        }
    }
}
