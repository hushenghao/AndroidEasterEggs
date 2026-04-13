package com.dede.android_eggs.views.widget

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

internal enum class AnalogClockWidgetClickAction {
    OPEN_EGG,
    OPEN_APP,
    NONE;
}


internal object AnalogClockWidgetPrefs {
    private const val ANALOG_CLOCK_WIDGET_DATASTORE = "analog_clock_widget_preferences"

    private val Context.analogClockWidgetDataStore by preferencesDataStore(name = ANALOG_CLOCK_WIDGET_DATASTORE)

    private const val KEY_CLICK_ACTION_PREFIX = "analog_clock_widget_click_action_"

    suspend fun getClickAction(context: Context, appWidgetId: Int): AnalogClockWidgetClickAction {
        return runCatching {
            val name = context.analogClockWidgetDataStore.data.first()[key(appWidgetId)]
                ?: AnalogClockWidgetClickAction.OPEN_APP.name
            AnalogClockWidgetClickAction.valueOf(name)
        }.getOrElse {
            AnalogClockWidgetClickAction.OPEN_APP
        }
    }

    suspend fun setClickAction(
        context: Context,
        appWidgetId: Int,
        clickAction: AnalogClockWidgetClickAction,
    ) {
        context.analogClockWidgetDataStore.edit { preferences ->
            preferences[key(appWidgetId)] = clickAction.name
        }
    }

    suspend fun clearClickAction(context: Context, appWidgetId: Int) {
        context.analogClockWidgetDataStore.edit { preferences ->
            preferences.remove(key(appWidgetId))
        }
    }

    private fun key(appWidgetId: Int) = stringPreferencesKey(KEY_CLICK_ACTION_PREFIX + appWidgetId)
}
