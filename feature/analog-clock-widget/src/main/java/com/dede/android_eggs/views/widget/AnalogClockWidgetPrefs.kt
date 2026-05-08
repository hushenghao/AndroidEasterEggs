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

internal data class AnalogClockWidgetConfig(
    val clickAction: AnalogClockWidgetClickAction,
    val dialStyle: AnalogClockWidgetDialStyle,
)

internal object AnalogClockWidgetPrefs {
    private const val ANALOG_CLOCK_WIDGET_DATASTORE = "analog_clock_widget_preferences"

    private val Context.analogClockWidgetDataStore by preferencesDataStore(name = ANALOG_CLOCK_WIDGET_DATASTORE)

    private const val KEY_CLICK_ACTION_PREFIX = "analog_clock_widget_click_action_"
    private const val KEY_DIAL_STYLE_PREFIX = "analog_clock_widget_dial_style_"

    suspend fun getConfig(context: Context, appWidgetId: Int): AnalogClockWidgetConfig {
        val preferences = context.analogClockWidgetDataStore.data.first()
        return AnalogClockWidgetConfig(
            clickAction = preferences[clickActionKey(appWidgetId)].toClickAction(),
            dialStyle = preferences[dialStyleKey(appWidgetId)].toDialStyle(),
        )
    }

    suspend fun setConfig(
        context: Context,
        appWidgetId: Int,
        config: AnalogClockWidgetConfig,
    ) {
        context.analogClockWidgetDataStore.edit { preferences ->
            preferences[clickActionKey(appWidgetId)] = config.clickAction.name
            preferences[dialStyleKey(appWidgetId)] = config.dialStyle.name
        }
    }

    suspend fun clearConfig(context: Context, appWidgetId: Int) {
        context.analogClockWidgetDataStore.edit { preferences ->
            preferences.remove(clickActionKey(appWidgetId))
            preferences.remove(dialStyleKey(appWidgetId))
        }
    }

    private fun clickActionKey(appWidgetId: Int) =
        stringPreferencesKey(KEY_CLICK_ACTION_PREFIX + appWidgetId)

    private fun dialStyleKey(appWidgetId: Int) =
        stringPreferencesKey(KEY_DIAL_STYLE_PREFIX + appWidgetId)

    private fun String?.toClickAction(): AnalogClockWidgetClickAction {
        return runCatching {
            AnalogClockWidgetClickAction.valueOf(this ?: AnalogClockWidgetClickAction.OPEN_APP.name)
        }.getOrElse {
            AnalogClockWidgetClickAction.OPEN_APP
        }
    }

    private fun String?.toDialStyle(): AnalogClockWidgetDialStyle {
        return runCatching {
            AnalogClockWidgetDialStyle.valueOf(this ?: AnalogClockWidgetDialStyle.CINNAMON_BUN.name)
        }.getOrElse {
            AnalogClockWidgetDialStyle.CINNAMON_BUN
        }
    }
}
