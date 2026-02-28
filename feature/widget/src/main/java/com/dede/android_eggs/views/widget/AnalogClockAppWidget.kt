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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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

}

private const val EXTRA_FROM_WIDGET = "extra_from_widget"

private fun updateAppWidgetAsync(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    cachedExecutor.execute {
        runBlocking(Dispatchers.IO) {
            val views = RemoteViews(context.packageName, R.layout.widget_easter_egg_analog_clock)

            val launchIntent: Intent? = withTimeoutOrNull(300) {
                // binder call
                Utils.getLaunchIntent(context)
            }
            if (launchIntent != null) {
                val intent = PendingIntentCompat.getActivity(
                    context, 0,
                    launchIntent.putExtra(EXTRA_FROM_WIDGET, true),
                    PendingIntent.FLAG_UPDATE_CURRENT,
                    false
                )
                views.setOnClickPendingIntent(R.id.analog_clock, intent)
            }
            // binder call
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
