package com.dede.android_eggs.main

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.util.applyIf


object EggActionHelp {

    private const val ACTIVITY_TASK_FLAGS =
        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS

    private fun createIntent(context: Context, egg: Egg): Intent? {
        if (egg.targetClassRes == -1) return null
        return Intent(Intent.ACTION_VIEW)
            .setClassName(context, context.getString(egg.targetClassRes))
            .addFlags(ACTIVITY_TASK_FLAGS)
            .applyIf(egg.extras != null) {
                putExtras(egg.extras!!)
            }
    }

    fun launchEgg(context: Context, egg: Egg) {
        val intent = createIntent(context, egg) ?: return
        context.startActivity(intent)
    }

    fun supportShortcut(context: Context, egg: Egg): Boolean {
        if (egg.shortcutKey == null) return false
        if (egg.targetClassRes == -1) return false
        return ShortcutManagerCompat.isRequestPinShortcutSupported(context)
    }

    fun addShortcutDialog(context: Context, egg: Egg) {
        if (egg.shortcutKey == null) return
        val intent = createIntent(context, egg) ?: return
        if (!supportShortcut(context, egg)) return

        val icon = IconCompat.createWithResource(context, egg.iconRes)
        val shortcut = ShortcutInfoCompat.Builder(context, egg.shortcutKey)
            .setIcon(icon)
            .setIntent(intent)
            .setShortLabel(context.getString(egg.eggNameRes))
            .build()
        ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
    }
}