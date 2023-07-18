package com.dede.android_eggs.main

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.os.Build
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.SplitUtils
import com.dede.android_eggs.util.actions.AppTaskManager
import com.dede.android_eggs.util.applyIf
import com.dede.basic.cancel
import com.dede.basic.delay
import com.dede.basic.dp


object EggActionHelp {

    private const val ACTIVITY_TASK_FLAGS =
        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS

    private fun createIntent(context: Context, egg: Egg): Intent? {
        if (egg.targetClass == null) return null
        return Intent(Intent.ACTION_VIEW)
            .setClass(context, egg.targetClass)
            .applyIf(!SplitUtils.isActivityEmbedded(context)) {
                addFlags(ACTIVITY_TASK_FLAGS)
            }
            .applyIf(egg.extras != null) {
                putExtras(egg.extras!!)
            }
    }

    fun launchEgg(context: Context, egg: Egg) {
        val task = AppTaskManager.getInstance()
            .findActivityTask(context, egg.targetClass)
        if (task != null) {
            task.moveToFront()
            return
        }
        val intent = createIntent(context, egg) ?: return
        context.startActivity(intent)
    }

    fun enableShortcut(egg: Egg): Boolean {
        return egg.shortcutKey != null && egg.targetClass != null
    }

    fun addShortcut(context: Context, egg: Egg) {
        if (egg.shortcutKey == null || !enableShortcut(egg)) return
        val intent = createIntent(context, egg) ?: return

        val icon = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val bitmap = AlterableAdaptiveIconDrawable(context, egg.iconRes)
                .toBitmap(48.dp, 48.dp)
            IconCompat.createWithBitmap(bitmap)
        } else {
            IconCompat.createWithResource(context, egg.iconRes)
        }
        val shortcut = ShortcutInfoCompat.Builder(context, egg.shortcutKey)
            .setIcon(icon)
            .setIntent(intent)
            .setShortLabel(context.getString(egg.eggNameRes))
            .build()

        val callback = PinShortcutReceiver.registerCallbackWithTimeout(context)
        ShortcutManagerCompat.requestPinShortcut(context, shortcut, callback)
    }

    private class PinShortcutReceiver : BroadcastReceiver() {

        companion object {
            private const val ACTION = "com.dede.android_eggs.PIN_SHORTCUT"
            private val token = Any()

            private var receiver: PinShortcutReceiver? = null
            private fun getPendingIntent(context: Context): PendingIntent {
                val result = Intent(ACTION)
                    .setPackage(context.packageName)
                var flags = PendingIntent.FLAG_UPDATE_CURRENT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    flags = flags or PendingIntent.FLAG_MUTABLE
                }
                return PendingIntent.getBroadcast(context.applicationContext, 0, result, flags)
            }

            fun registerCallbackWithTimeout(context: Context): IntentSender {
                var receiver = this.receiver
                if (receiver == null) {
                    receiver = PinShortcutReceiver()
                    val intentFilter = IntentFilter(ACTION)
                    val appCtx = context.applicationContext
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        appCtx.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
                    } else {
                        appCtx.registerReceiver(receiver, intentFilter)
                    }
                    this.receiver = receiver
                } else {
                    cancel(token)
                }
                delay(3000, token) { unregister(context) }

                return getPendingIntent(context).intentSender
            }

            private fun unregister(context: Context) {
                if (receiver != null) {
                    context.applicationContext.unregisterReceiver(receiver)
                }
                receiver = null
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, R.string.toast_shortcut_added, Toast.LENGTH_SHORT).show()
            unregister(context)
            cancel(token)
        }
    }
}