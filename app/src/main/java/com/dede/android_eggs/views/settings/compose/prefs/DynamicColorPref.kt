package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.SwitchOption
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefIntState
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil.DEFAULT
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil.KEY_DYNAMIC_COLOR
import com.dede.android_eggs.views.theme.isDynamicColorEnable

@Preview
@Composable
fun DynamicColorPref() {
    var intState by rememberPrefIntState(key = KEY_DYNAMIC_COLOR, default = DEFAULT)
    SwitchOption(
        leadingIcon = imageVectorIconBlock(
            imageVector = Icons.Rounded.Palette,
            contentDescription = stringResource(R.string.pref_title_dynamic_color),
        ),
        title = stringResource(R.string.pref_title_dynamic_color),
        value = intState == SettingPrefUtil.ON,
        shape = OptionShapes.borderShape,
        onCheckedChange = {
            intState = if (it) SettingPrefUtil.ON else SettingPrefUtil.OFF
            isDynamicColorEnable = intState == SettingPrefUtil.ON
        }
    )
}