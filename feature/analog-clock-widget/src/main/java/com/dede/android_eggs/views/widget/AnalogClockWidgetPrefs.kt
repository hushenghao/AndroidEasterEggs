package com.dede.android_eggs.views.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

internal enum class AnalogClockWidgetClickAction(@StringRes val labelRes: Int) {
    OPEN_EGG(R.string.analog_clock_widget_action_open_egg),
    OPEN_APP(R.string.analog_clock_widget_action_open_app),
    NONE(R.string.analog_clock_widget_action_none);
}

internal enum class AnalogClockWidgetDialStyle(
    @DrawableRes val dialRes: Int,
    @LayoutRes val layoutRes: Int,
    @StringRes val nameRes: Int,
) {
    SIMPLE(
        R.drawable.clock_analog_dial_simple,
        R.layout.widget_easter_egg_analog_clock_simple,
        R.string.analog_clock_widget_dial_simple,
    ),
    ANDROID_ICONS(
        R.drawable.clock_analog_dial_android_icons,
        R.layout.widget_easter_egg_analog_clock_android_icons,
        R.string.analog_clock_widget_dial_android_icons,
    ),
    CINNAMON_BUN(
        R.drawable.clock_analog_dial_cinnamon_bun,
        R.layout.widget_easter_egg_analog_clock_cinnamon_bun,
        R.string.analog_clock_widget_dial_cinnamon_bun,
    );
}

internal object AnalogClockWidgetPrefs {
    private const val ANALOG_CLOCK_WIDGET_DATASTORE = "analog_clock_widget_preferences"

    private val Context.analogClockWidgetDataStore by preferencesDataStore(name = ANALOG_CLOCK_WIDGET_DATASTORE)

    private const val KEY_CLICK_ACTION_PREFIX = "analog_clock_widget_click_action_"
    private const val KEY_DIAL_STYLE_PREFIX = "analog_clock_widget_dial_style_"

    suspend fun getClickAction(context: Context, appWidgetId: Int): AnalogClockWidgetClickAction {
        return runCatching {
            val name = context.analogClockWidgetDataStore.data.first()[clickActionKey(appWidgetId)]
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
            preferences[clickActionKey(appWidgetId)] = clickAction.name
        }
    }

    suspend fun getDialStyle(context: Context, appWidgetId: Int): AnalogClockWidgetDialStyle {
        return runCatching {
            val name = context.analogClockWidgetDataStore.data.first()[dialStyleKey(appWidgetId)]
                ?: AnalogClockWidgetDialStyle.CINNAMON_BUN.name
            AnalogClockWidgetDialStyle.valueOf(name)
        }.getOrElse {
            AnalogClockWidgetDialStyle.CINNAMON_BUN
        }
    }

    suspend fun setDialStyle(
        context: Context,
        appWidgetId: Int,
        dialStyle: AnalogClockWidgetDialStyle,
    ) {
        context.analogClockWidgetDataStore.edit { preferences ->
            preferences[dialStyleKey(appWidgetId)] = dialStyle.name
        }
    }

    suspend fun clearClickAction(context: Context, appWidgetId: Int) {
        context.analogClockWidgetDataStore.edit { preferences ->
            preferences.remove(clickActionKey(appWidgetId))
            preferences.remove(dialStyleKey(appWidgetId))
        }
    }

    private fun clickActionKey(appWidgetId: Int) =
        stringPreferencesKey(KEY_CLICK_ACTION_PREFIX + appWidgetId)

    private fun dialStyleKey(appWidgetId: Int) =
        stringPreferencesKey(KEY_DIAL_STYLE_PREFIX + appWidgetId)
}
