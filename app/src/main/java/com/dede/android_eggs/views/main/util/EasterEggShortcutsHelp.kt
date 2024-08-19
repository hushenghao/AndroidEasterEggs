package com.dede.android_eggs.views.main.util

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.dede.android_eggs.util.applyNotNull
import com.dede.basic.cachedExecutor
import com.dede.basic.provider.EasterEgg
import kotlin.math.min

object EasterEggShortcutsHelp {

    private const val DEFAULT_SHORTCUT_COUNT = 3
    private const val FORMAT_SHORTCUT_ID = "android_%d"

    private class UpdateShortcutsRunnable(
        private val context: Context,
        private val eggs: List<EasterEgg>
    ) : Runnable {

        override fun run() {
            val shortcutCount = min(
                ShortcutManagerCompat.getMaxShortcutCountPerActivity(context),
                DEFAULT_SHORTCUT_COUNT
            )
            val subEggs = if (eggs.size > shortcutCount) {
                eggs.subList(0, shortcutCount)
            } else {
                eggs
            }
            val dynamicShortcuts = ShortcutManagerCompat.getDynamicShortcuts(context)
            val removeShortcutIds = dynamicShortcuts.map { it.id }.toMutableList()
            for (egg in subEggs) {
                val shortcutId = FORMAT_SHORTCUT_ID.format(egg.id)
                for (shortcut in dynamicShortcuts) {
                    if (shortcutId == shortcut.id) {
                        removeShortcutIds.remove(shortcut.id)
                        break
                    }
                }

                val shortcutInfo = createShortcutInfo(context, egg)
                ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo)
            }
            if (removeShortcutIds.size > 0) {
                ShortcutManagerCompat.removeDynamicShortcuts(context, removeShortcutIds)
            }
        }
    }

    fun updateShortcuts(context: Context, eggs: List<EasterEgg>) {
        val providedEggs = eggs.filter { it.provideEasterEgg() != null }
        val appCtx = context.applicationContext
        cachedExecutor.execute(UpdateShortcutsRunnable(appCtx, providedEggs))
    }

    private fun createShortcutInfo(context: Context, egg: EasterEgg): ShortcutInfoCompat {
        val clazz = requireNotNull(egg.provideEasterEgg()) {
            "Unsupported easter egg, provide class == null!"
        }
        val label = context.getString(egg.nicknameRes)
        return ShortcutInfoCompat.Builder(context, FORMAT_SHORTCUT_ID.format(egg.id))
            .setRank(egg.id)
            .setIcon(IconCompat.createWithResource(context, egg.iconRes))
            .setShortLabel(label)
            .setLongLabel(label)
            .applyNotNull(clazz) {
                val intent = Intent(context, it)
                    .setAction(Intent.ACTION_VIEW)
                setIntent(intent)
            }
            .build()
    }
}