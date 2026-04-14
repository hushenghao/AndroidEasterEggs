package com.dede.android_eggs.views.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.PendingIntentCompat
import com.dede.basic.Utils
import com.dede.basic.cachedExecutor
import com.dede.basic.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Easter Eggs Analog clock widget.
 */
class AnalogClockAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidgetAsync(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?,
    ) {
        updateAppWidgetAsync(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        cachedExecutor.launch(Dispatchers.IO) {
            for (appWidgetId in appWidgetIds) {
                AnalogClockWidgetPrefs.clearClickAction(context, appWidgetId)
            }
        }
    }

}

private const val EXTRA_FROM_ANALOG_CLOCK_WIDGET_ACTION = "extra_from_analog_clock_widget_action"

internal fun updateAppWidgetAsync(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    cachedExecutor.launch(Dispatchers.IO) {
        val views = RemoteViews(context.packageName, R.layout.widget_easter_egg_analog_clock)
        views.setOnClickPendingIntent(R.id.analog_clock, null)

        val action = AnalogClockWidgetPrefs.getClickAction(context, appWidgetId)
        if (action != AnalogClockWidgetClickAction.NONE) {
            val launchIntent: Intent? = withTimeoutOrNull(300) {
                // binder call
                Utils.getLaunchIntent(context)
            }
            if (launchIntent != null) {
                launchIntent.putExtra(EXTRA_FROM_ANALOG_CLOCK_WIDGET_ACTION, action.ordinal)
                val intent = PendingIntentCompat.getActivity(
                    context, 0,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT,
                    false
                )
                views.setOnClickPendingIntent(R.id.analog_clock, intent)
            }
        }
        // binder call
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
