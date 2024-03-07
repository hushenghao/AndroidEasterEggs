package com.dede.android_eggs.views.settings.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SwitchPref(
    key: String,
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    default: Boolean = false,
    onCheckedChange: (checked: Boolean) -> Unit
) {
    var intValue by rememberPrefIntState(
        key,
        if (default) SettingPref.ON else SettingPref.OFF
    )
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            Box(modifier = Modifier.padding(end = 10.dp)) {
                Switch(checked = intValue == SettingPref.ON, onCheckedChange = {
                    intValue = if (it) SettingPref.ON else SettingPref.OFF
                    onCheckedChange(it)
                })
            }
        },
    )
}