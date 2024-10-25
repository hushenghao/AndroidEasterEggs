package com.dede.android_eggs.views.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.PendingIntentCompat
import com.dede.basic.cachedExecutor

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
            updateAppWidget(
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
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

}

private const val EXTRA_FROM_WIDGET = "extra_from_widget"

private fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val views = RemoteViews(context.packageName, R.layout.widget_easter_egg_analog_clock)

    var mainClass: Class<*>? = null
    try {
        mainClass = Class.forName("com.dede.android_eggs.views.main.EasterEggsActivity")
    } catch (e: Exception) {
        if (BuildConfig.DEBUG) {
            throw e
        }
    }
    if (mainClass != null) {
        val intent = PendingIntentCompat.getActivity(
            context, 0,
            Intent(context, mainClass)
                .putExtra(EXTRA_FROM_WIDGET, appWidgetId),
            PendingIntent.FLAG_UPDATE_CURRENT,
            false
        )
        views.setOnClickPendingIntent(R.id.analog_clock, intent)
    }

    cachedExecutor.execute {
        // Binder call
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
