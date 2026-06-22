package com.dede.android_eggs.views.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.PendingIntentCompat
import com.dede.basic.Utils
import com.dede.basic.ioScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

/**
 * Easter Eggs Analog clock widget.
 */
class AnalogClockAppWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == Intent.ACTION_CONFIGURATION_CHANGED) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, AnalogClockAppWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            updateAppWidgetsAsync(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        updateAppWidgetsAsync(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?,
    ) {
        updateAppWidgetsAsync(context, appWidgetManager, intArrayOf(appWidgetId))
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        ioScope.launch {
            for (appWidgetId in appWidgetIds) {
                AnalogClockWidgetPrefs.clearConfig(context, appWidgetId)
            }
        }
    }

    private fun updateAppWidgetsAsync(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        ioScope.launch {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

}

private const val EXTRA_FROM_ANALOG_CLOCK_WIDGET_ACTION = "extra_from_analog_clock_widget_action"

internal suspend fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) = withContext(Dispatchers.IO) {
    val config = AnalogClockWidgetPrefs.getConfig(context, appWidgetId)
    val views = RemoteViews(context.packageName, config.dialStyle.layoutRes)
    views.setOnClickPendingIntent(R.id.analog_clock, null)

    if (config.clickAction != AnalogClockWidgetClickAction.NONE) {
        val launchIntent: Intent? = withTimeoutOrNull(300.milliseconds) {
            // binder call
            Utils.getLaunchIntent(context)
        }
        if (launchIntent != null) {
            launchIntent.putExtra(EXTRA_FROM_ANALOG_CLOCK_WIDGET_ACTION, config.clickAction.ordinal)
            val intent = PendingIntentCompat.getActivity(
                context, 0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
                false,
            )
            views.setOnClickPendingIntent(R.id.analog_clock, intent)
        }
    }
    // binder call
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
