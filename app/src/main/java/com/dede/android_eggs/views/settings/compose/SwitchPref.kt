package com.dede.android_eggs.views.settings.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SwitchPref(
    leadingIcon: @Composable () -> Unit,
    title: String,
    value: Int = SettingPref.ON,
    onCheckedChange: (checked: Boolean) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                leadingIcon()
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                Switch(checked = value == SettingPref.ON, onCheckedChange = onCheckedChange)
            }
        }
    }
}