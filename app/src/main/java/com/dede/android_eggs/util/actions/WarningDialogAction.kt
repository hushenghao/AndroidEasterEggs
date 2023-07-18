package com.dede.android_eggs.util.actions

import android.app.Activity
import android.graphics.Color
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.basic.createThemeWrapperContext
import com.dede.basic.getBoolean
import com.dede.basic.putBoolean
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.R as M3R


class WarningDialogAction : ActivityActionDispatcher.ActivityAction {

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
        val color =
            MaterialColors.getColor(wrapperContext, M3R.attr.colorControlNormal, Color.BLACK)
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