package com.dede.android_eggs.views.settings.compose.prefs

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.ValueOption
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.views.settings.compose.basic.radioButtonBlock
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefIntState
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil.ACTION_NIGHT_MODE_CHANGED
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil.AMOLED
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil.DARK
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil.FOLLOW_SYSTEM
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil.KEY_NIGHT_MODE
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil.LIGHT
import com.dede.android_eggs.views.theme.themeMode


@Preview
@Composable
fun ThemePref() {
    val context = LocalContext.current
    var themeModeValue by rememberPrefIntState(KEY_NIGHT_MODE, FOLLOW_SYSTEM)
    val onOptionClick = click@{ mode: Int ->
        themeModeValue = mode
        themeMode = themeModeValue
        var appCompatMode = mode
        if (appCompatMode == AMOLED) {
            appCompatMode = DARK
        }
        if (appCompatMode == AppCompatDelegate.getDefaultNightMode()) {
            if ((mode == AMOLED) != ThemeUtils.isOLEDTheme(context)) {
                ThemeUtils.recreateActivityIfPossible(context)
                LocalEvent.poster().post(ACTION_NIGHT_MODE_CHANGED)
            }
            return@click
        }
        AppCompatDelegate.setDefaultNightMode(appCompatMode)
        LocalEvent.poster().post(ACTION_NIGHT_MODE_CHANGED)
    }

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Brush,
        title = stringResource(R.string.pref_title_theme),
    ) {
        ValueOption(
            shape = OptionShapes.firstShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.BrightnessAuto,
                contentDescription = stringResource(R.string.summary_system_default)
            ),
            title = stringResource(R.string.summary_system_default),
            trailingContent = radioButtonBlock(themeModeValue == FOLLOW_SYSTEM),
            value = FOLLOW_SYSTEM,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.LightMode,
                contentDescription = stringResource(R.string.summary_theme_light_mode)
            ),
            title = stringResource(R.string.summary_theme_light_mode),
            trailingContent = radioButtonBlock(themeModeValue == LIGHT),
            value = LIGHT,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.DarkMode,
                contentDescription = stringResource(R.string.summary_theme_dark_mode)
            ),
            title = stringResource(R.string.summary_theme_dark_mode),
            trailingContent = radioButtonBlock(themeModeValue == DARK),
            value = DARK,
            onOptionClick = onOptionClick,
        )
        ValueOption(
            shape = OptionShapes.lastShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Contrast,
                contentDescription = stringResource(id = R.string.summary_theme_amoled_mode)
            ),
            title = stringResource(id = R.string.summary_theme_amoled_mode),
            trailingContent = radioButtonBlock(themeModeValue == AMOLED),
            value = AMOLED,
            onOptionClick = onOptionClick,
        )

        if (DynamicColorPrefUtil.isSupported()) {
            Spacer(modifier = Modifier.height(1.dp))

            DynamicColorPref()
        }

    }
}
