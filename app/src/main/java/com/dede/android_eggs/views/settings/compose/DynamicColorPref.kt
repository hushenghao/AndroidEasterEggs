package com.dede.android_eggs.views.settings.compose

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.views.settings.compose.DynamicColorPrefUtil.DEFAULT
import com.dede.android_eggs.views.settings.compose.DynamicColorPrefUtil.KEY_DYNAMIC_COLOR
import com.dede.android_eggs.views.theme.isDynamicEnable
import com.google.android.material.color.DynamicColors

object DynamicColorPrefUtil {
    val DEFAULT =
        if (DynamicColors.isDynamicColorAvailable()) SettingPref.ON else SettingPref.OFF
    const val KEY_DYNAMIC_COLOR = "pref_key_dynamic_color"
    const val ACTION_DYNAMIC_COLOR_CHANGED = "ACTION_DYNAMIC_COLOR_CHANGED"

    fun isDynamicEnable(context: Context): Boolean {
        return SettingPref.getValue(context, KEY_DYNAMIC_COLOR, DEFAULT) == SettingPref.ON
    }
}

@Composable
fun DynamicColorPref() {
    var dynamicColorValue by rememberPrefIntState(KEY_DYNAMIC_COLOR, DEFAULT)
    SwitchPref(
        leadingIcon = imageVectorIcon(
            imageVector = Icons.Rounded.Palette,
            contentDescription = stringResource(R.string.pref_title_dynamic_color)
        ),
        title = stringResource(R.string.pref_title_dynamic_color),
        value = dynamicColorValue,
        onCheckedChange = {
            dynamicColorValue = if (it) SettingPref.ON else SettingPref.OFF
            isDynamicEnable = dynamicColorValue == SettingPref.ON
        }
    )
}