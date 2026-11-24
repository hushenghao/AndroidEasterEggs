package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.settings_ui.basic.SwitchPref
import com.dede.android_eggs.settings_ui.basic.rememberPrefBoolState
import com.dede.android_eggs.resources.R as StringsR

@Composable
fun DynamicEggColorsPref() {
    SwitchPref(
        state = rememberPrefBoolState(AppSettings.dynamicEggColors),
        leadingIcon = Icons.Rounded.Palette,
        title = stringResource(id = StringsR.string.pref_title_dynamic_egg_colors),
        desc = stringResource(id = StringsR.string.pref_summary_dynamic_egg_colors),
    )
}
