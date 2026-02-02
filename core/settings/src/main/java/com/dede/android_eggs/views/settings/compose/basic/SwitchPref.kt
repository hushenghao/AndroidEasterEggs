package com.dede.android_eggs.views.settings.compose.basic

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
    var boolState by rememberPrefBoolState(key, default)
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            Switch(
                checked = boolState,
                modifier = Modifier.padding(end = 12.dp),
                onCheckedChange = {
                    boolState = it
                    onCheckedChange(it)
                })
        },
        onClick = {
            boolState = !boolState
            onCheckedChange(boolState)
        }
    )
}

@Composable
fun SwitchIntPref(
    key: String,
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    default: Int = SettingPrefUtil.OFF,
    onCheckedChange: (value: Int) -> Unit
) {
    var intState by rememberPrefIntState(key, default)
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            Switch(
                checked = intState == SettingPrefUtil.ON,
                modifier = Modifier.padding(end = 12.dp),
                onCheckedChange = {
                    intState = if (it) SettingPrefUtil.ON else SettingPrefUtil.OFF
                    onCheckedChange(intState)
                })
        },
        onClick = {
            intState = if (intState == SettingPrefUtil.ON) {
                SettingPrefUtil.OFF
            } else {
                SettingPrefUtil.ON
            }
            onCheckedChange(intState)
        }
    )
}