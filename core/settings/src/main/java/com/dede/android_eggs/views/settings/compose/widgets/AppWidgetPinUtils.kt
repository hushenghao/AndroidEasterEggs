package com.dede.android_eggs.views.settings.compose.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.os.Build

object AppWidgetPinUtils {
    fun requestPinWidget(
        context: Context,
        widgetClass: Class<out AppWidgetProvider>,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        if (!appWidgetManager.isRequestPinAppWidgetSupported) {
            return
        }

        val componentName = ComponentName(context, widgetClass)
        appWidgetManager.requestPinAppWidget(componentName, null, null)
    }
}
