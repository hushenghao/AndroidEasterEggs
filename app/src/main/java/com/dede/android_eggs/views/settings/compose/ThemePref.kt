package com.dede.android_eggs.views.settings.compose

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.Brightness7
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.BrightnessLow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.util.compose.bottom
import com.dede.android_eggs.util.compose.top
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.settings.compose.ThemePrefUtil.ACTION_NIGHT_MODE_CHANGED
import com.dede.android_eggs.views.settings.compose.ThemePrefUtil.DARK
import com.dede.android_eggs.views.settings.compose.ThemePrefUtil.FOLLOW_SYSTEM
import com.dede.android_eggs.views.settings.compose.ThemePrefUtil.KEY_NIGHT_MODE
import com.dede.android_eggs.views.settings.compose.ThemePrefUtil.LIGHT
import com.dede.android_eggs.views.settings.compose.ThemePrefUtil.OLED
import com.dede.android_eggs.views.theme.themeMode

object ThemePrefUtil {

    const val OLED = -2
    const val LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val DARK = AppCompatDelegate.MODE_NIGHT_YES
    const val FOLLOW_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    const val KEY_NIGHT_MODE = "pref_key_night_mode"

    const val ACTION_NIGHT_MODE_CHANGED = "action_night_mode_changed"
    fun isOLEDMode(context: Context): Boolean {
        return context.pref.getInt(KEY_NIGHT_MODE, FOLLOW_SYSTEM) == OLED
    }

    fun getThemeModeValue(context: Context): Int {
        return context.pref.getInt(KEY_NIGHT_MODE, FOLLOW_SYSTEM)
    }

    fun apply(context: Context) {
        var mode = getThemeModeValue(context)
        if (mode == OLED) {
            mode = DARK
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}

@Composable
fun ThemePref() {
    val context = LocalContext.current
    var themeModeValue by rememberPrefIntState(KEY_NIGHT_MODE, FOLLOW_SYSTEM)
    val onOptionClick = click@{ mode: Int ->
        themeModeValue = mode
        themeMode = themeModeValue
        var appCompatMode = mode
        if (appCompatMode == OLED) {
            appCompatMode = DARK
        }
        if (appCompatMode == AppCompatDelegate.getDefaultNightMode()) {
            if ((mode == OLED) != ThemeUtils.isOLEDTheme(context)) {
                ThemeUtils.recreateActivityIfPossible(context)
                LocalEvent.poster(context).post(ACTION_NIGHT_MODE_CHANGED)
            }
            return@click
        }
        AppCompatDelegate.setDefaultNightMode(appCompatMode)
        LocalEvent.poster(context).post(ACTION_NIGHT_MODE_CHANGED)
    }



    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.BrightnessAuto,
        title = stringResource(R.string.pref_title_theme),
    ) {
        ValueOption(
            shape = MaterialTheme.shapes.small.top(16.dp),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Brightness7,
                contentDescription = stringResource(R.string.summary_theme_light_mode)
            ),
            title = stringResource(R.string.summary_theme_light_mode),
            trailingContent = radioButtonBlock(themeModeValue == LIGHT),
            value = LIGHT,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Brightness4,
                contentDescription = stringResource(R.string.summary_theme_dark_mode)
            ),
            title = stringResource(R.string.summary_theme_dark_mode),
            trailingContent = radioButtonBlock(themeModeValue == DARK),
            value = DARK,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.BrightnessLow,
                contentDescription = "OLED"
            ),
            title = "OLED",
            trailingContent = radioButtonBlock(themeModeValue == OLED),
            value = OLED,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shape = MaterialTheme.shapes.small.bottom(16.dp),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.BrightnessAuto,
                contentDescription = stringResource(R.string.summary_system_default)
            ),
            title = stringResource(R.string.summary_system_default),
            trailingContent = radioButtonBlock(themeModeValue == FOLLOW_SYSTEM),
            value = FOLLOW_SYSTEM,
            onOptionClick = onOptionClick,
        )
    }
}
