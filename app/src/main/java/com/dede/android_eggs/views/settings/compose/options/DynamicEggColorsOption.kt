package com.dede.android_eggs.views.settings.compose.options

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.settings_state.rememberPrefBoolState
import com.dede.android_eggs.settings_ui.basic.OptionShapes
import com.dede.android_eggs.settings_ui.basic.SwitchOption
import com.dede.android_eggs.settings_ui.basic.imageVectorIconBlock
import com.dede.android_eggs.resources.R as StringsR

@Composable
fun DynamicEggColorsOption(shape: Shape = OptionShapes.borderShape) {
    var dynamicEggColors by rememberPrefBoolState(AppSettings.dynamicEggColors)
    SwitchOption(
        shape = shape,
        leadingIcon = imageVectorIconBlock(imageVector = Icons.Rounded.ColorLens),
        title = stringResource(id = StringsR.string.pref_title_dynamic_egg_colors),
        desc = stringResource(id = StringsR.string.pref_summary_dynamic_egg_colors),
        checked = dynamicEggColors,
        onCheckedChange = {
            dynamicEggColors = it
        },
    )
}
