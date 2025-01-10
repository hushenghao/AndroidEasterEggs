package com.dede.android_eggs.views.main.util

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.os.Build
import androidx.core.app.PendingIntentCompat
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.applyIf
import com.dede.android_eggs.util.applyNotNull
import com.dede.basic.Utils
import com.dede.basic.cachedExecutor
import com.dede.basic.cancel
import com.dede.basic.delay
import com.dede.basic.dp
import com.dede.basic.provider.EasterEgg
import com.dede.basic.toast
import kotlin.math.min

object EasterEggShortcutsHelp {

    private const val EXTRA_SHORTCUT_ID = "extra_shortcut_id"

    private const val DEFAULT_SHORTCUT_COUNT = 3

    private const val FORMAT_DYNAMIC_SHORTCUT_ID = "dynamic_shortcut_android_%d"
    private const val FORMAT_PIN_SHORTCUT_ID = "android_%d"

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
                val shortcutId = FORMAT_DYNAMIC_SHORTCUT_ID.format(egg.apiLevel)
                for (shortcut in dynamicShortcuts) {
                    if (shortcutId == shortcut.id) {
                        removeShortcutIds.remove(shortcut.id)
                        break
                    }
                }
                if (!isSupportShortcut(egg)) {
                    continue
                }
                val shortcutInfo = createShortcutInfo(context, shortcutId, egg, false)
                ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo)
            }
            if (removeShortcutIds.size > 0) {
                ShortcutManagerCompat.removeDynamicShortcuts(context, removeShortcutIds)
            }
        }
    }

    fun updateShortcuts(context: Context, eggs: List<EasterEgg>) {
        val providedEggs = eggs.filter { it.actionClass != null }
        val appCtx = context.applicationContext
        cachedExecutor.execute(UpdateShortcutsRunnable(appCtx, providedEggs))
    }

    private fun createShortcutInfo(
        context: Context,
        shortcutId: String,
        egg: EasterEgg,
        isPinShortcut: Boolean = true
    ): ShortcutInfoCompat {
        val clazz = requireNotNull(egg.actionClass) {
            "EasterEgg unsupported shortcut, provide class == null!"
        }
        val label = context.getString(egg.nicknameRes)

        val icon = if (isPinShortcut && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val bitmap = AlterableAdaptiveIconDrawable(context, egg.iconRes)
                .toBitmap(48.dp, 48.dp)
            IconCompat.createWithBitmap(bitmap)
        } else {
            IconCompat.createWithResource(context, egg.iconRes)
        }
        return ShortcutInfoCompat.Builder(context, shortcutId)
            .setIcon(icon)
            .setShortLabel(label)
            .setLongLabel(label)
            .applyIf(!isPinShortcut) {
                setRank(egg.apiLevel)
            }
            .applyNotNull(clazz) {
                val eggIntent = createTargetIntent(context, clazz)
                    .putExtra(EXTRA_SHORTCUT_ID, shortcutId)

                val mainIntent = Utils.getLaunchIntent(context)
                if (mainIntent != null) {
                    mainIntent.setAction(Intent.ACTION_VIEW)
                    setIntents(arrayOf(mainIntent, eggIntent))
                } else {
                    setIntent(eggIntent)
                }
            }
            .build()
    }

    private fun createTargetIntent(context: Context, targetClass: Class<out Activity>): Intent {
        return Intent(Intent.ACTION_VIEW).setClass(context, targetClass)
    }

    fun isSupportShortcut(egg: EasterEgg): Boolean {
        return egg.actionClass != null
    }

    fun pinShortcut(context: Context, egg: EasterEgg) {
        if (!isSupportShortcut(egg)) return

        val shortcutId = FORMAT_PIN_SHORTCUT_ID.format(egg.apiLevel)
        val shortcut = createShortcutInfo(context, shortcutId, egg, true)
        val callback = PinShortcutReceiver.registerCallbackWithTimeout(context)
        ShortcutManagerCompat.requestPinShortcut(context, shortcut, callback)
    }

    fun autoReportShortcutUsed(context: Context, intent: Intent) {
        val shortcutId = intent.getStringExtra(EXTRA_SHORTCUT_ID) ?: return
        ShortcutManagerCompat.reportShortcutUsed(context, shortcutId)
    }

    private class PinShortcutReceiver : BroadcastReceiver() {

        companion object {
            private const val ACTION = "com.dede.android_eggs.PIN_SHORTCUT"

            private val token = Any()
            private var receiver: PinShortcutReceiver? = null

            private fun getPendingIntent(context: Context): PendingIntent? {
                return PendingIntentCompat.getBroadcast(
                    context.applicationContext,
                    0,
                    Intent(ACTION).setPackage(context.packageName),
                    PendingIntent.FLAG_UPDATE_CURRENT,
                    false
                )
            }

            fun registerCallbackWithTimeout(context: Context): IntentSender? {
                var receiver = receiver
                if (receiver == null) {
                    receiver = PinShortcutReceiver()
                    val intentFilter = IntentFilter(ACTION)
                    val appCtx = context.applicationContext
                    ContextCompat.registerReceiver(
                        appCtx, receiver, intentFilter, ContextCompat.RECEIVER_EXPORTED
                    )
                    Companion.receiver = receiver
                } else {
                    cancel(token)
                }
                delay(3000, token) { unregister(context) }

                return getPendingIntent(context)?.intentSender
            }

            private fun unregister(context: Context) {
                if (receiver != null) {
                    context.applicationContext.unregisterReceiver(receiver)
                }
                receiver = null
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            context.toast(com.dede.android_eggs.resources.R.string.toast_shortcut_added)
            unregister(context)
            cancel(token)
        }
    }

}
