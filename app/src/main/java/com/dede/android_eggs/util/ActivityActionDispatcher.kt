package com.dede.android_eggs.util

import android.Manifest
import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.Icons
import com.dede.basic.createThemeWrapperContext
import com.dede.basic.getBoolean
import com.dede.basic.putBoolean
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.R as M3R

class ActivityActionDispatcher : Application.ActivityLifecycleCallbacks {

    interface ActivityAction {

        fun onCreate(activity: Activity) {}

        fun onStart(activity: Activity) {}

        fun onResume(activity: Activity) {}

        fun onPause(activity: Activity) {}
    }

    private class WarningDialogAction : ActivityAction {

        private class ActionInfo(
            val key: String,
            @StringRes val title: Int,
            @StringRes val message: Int,
        )

        companion object {
            private val target = mapOf(
                com.android_t.egg.PlatLogoActivity::class to ActionInfo(
                    "key_t_trypophobia_warning",
                    android.R.string.dialog_alert_title,
                    R.string.message_trypophobia_warning
                ),
                com.android_s.egg.PlatLogoActivity::class to ActionInfo(
                    "key_s_trypophobia_warning",
                    android.R.string.dialog_alert_title,
                    R.string.message_trypophobia_warning
                ),
            )
        }

        override fun onCreate(activity: Activity) {
            val info = target[activity.javaClass.kotlin] ?: return
            val agreed = activity.getBoolean(info.key, false)
            if (agreed) return

            val spanned = HtmlCompat.fromHtml(
                activity.getString(info.message),
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
            val wrapperContext = activity.createThemeWrapperContext()
            val icon = FontIconsDrawable(wrapperContext, Icons.Rounded.tips_and_updates, 48f)
            val color = MaterialColors.getColor(wrapperContext, M3R.attr.colorControlNormal, Color.BLACK)
            icon.setColor(color)
            MaterialAlertDialogBuilder(wrapperContext)
                .setIcon(icon)
                .setTitle(info.title)
                .setMessage(spanned)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    activity.putBoolean(info.key, true)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    activity.finish()
                }
                .show()
        }

    }

    private class PermissionRequestAction : ActivityAction {
        companion object {

            private val empty = emptyArray<String>()

            private val NOTIFICATION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arrayOf(Manifest.permission.POST_NOTIFICATIONS) else empty

            private val mapping = mapOf(
                com.android_t.egg.ComponentActivationActivity::class to NOTIFICATION,
                com.android_s.egg.ComponentActivationActivity::class to NOTIFICATION,
                com.android_r.egg.neko.NekoActivationActivity::class to NOTIFICATION,
                com.android_n.egg.neko.NekoActivationActivity::class to NOTIFICATION,
            )
        }

        override fun onCreate(activity: Activity) {
            val permissions = mapping[activity.javaClass.kotlin]
            if (permissions != null && permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(activity, permissions, 0)
            }
        }
    }

    companion object {
        fun register(application: Application) {
            application.registerActivityLifecycleCallbacks(ActivityActionDispatcher())
        }
    }

    private val actions = arrayListOf(
        PermissionRequestAction(),
        WarningDialogAction()
    )

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        for (action in actions) {
            action.onCreate(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        for (action in actions) {
            action.onStart(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        for (action in actions) {
            action.onResume(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        for (action in actions) {
            action.onPause(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}