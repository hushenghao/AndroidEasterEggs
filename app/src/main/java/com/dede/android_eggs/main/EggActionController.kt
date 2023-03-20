package com.dede.android_eggs.main

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.util.applyIf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.R as M3R


class EggActionController(val context: Context) {

    companion object {
        private const val ACTIVITY_TASK_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS
    }

    private fun createIntent(@StringRes classRes: Int): Intent? {
        if (classRes == -1) return null
        return Intent(Intent.ACTION_VIEW)
            .setClassName(context, context.getString(classRes))
            .addFlags(ACTIVITY_TASK_FLAGS)
    }

    fun openEgg(egg: Egg) {
        val intent = createIntent(egg.targetClassRes) ?: return
        if (egg.extras != null)
            intent.putExtras(egg.extras)
        context.startActivity(intent)
    }

    fun showVersionCommentDialog(egg: Egg) {
        showDialogInternal(
            egg.iconRes,
            egg.androidRes,
            egg.versionCommentRes,
            egg.shortcutKey,
            context.getString(egg.eggNameRes),
            createIntent(egg.targetClassRes)
        )
    }

    private fun showDialogInternal(
        @DrawableRes iconResId: Int,
        @StringRes title: Int,
        @StringRes message: Int,
        shortcutKey: String?,
        shortcutLabel: String?,
        shortcutIntent: Intent?,
    ) {
        val supportShortcut =
            shortcutKey != null && shortcutLabel != null && shortcutIntent != null &&
                    ShortcutManagerCompat.isRequestPinShortcutSupported(context)
        MaterialAlertDialogBuilder(
            context,
            M3R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setIcon(iconResId)
            .setTitle(title)
            .setMessage(message)
            .applyIf(supportShortcut) {
                setNeutralButton(R.string.label_add_shortcut) { _, _ ->
                    val icon = IconCompat.createWithResource(context, iconResId)
                    val shortcut = ShortcutInfoCompat.Builder(context, shortcutKey!!)
                        .setIcon(icon)
                        .setIntent(shortcutIntent!!)
                        .setShortLabel(shortcutLabel!!)
                        .build()
                    ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
                }
            }
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}