package com.dede.android_eggs.views.settings.compose.basic

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SwitchPref(
    state: MutableState<Boolean>,
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    onCheckedChange: (checked: Boolean) -> Unit = {},
) {
    var boolState by state
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
    state: MutableIntState,
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    onCheckedChange: (value: Int) -> Unit = {},
) {
    var intState by state
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