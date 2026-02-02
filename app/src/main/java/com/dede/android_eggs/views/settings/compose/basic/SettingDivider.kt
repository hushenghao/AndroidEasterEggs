package com.dede.android_eggs.views.settings.compose.basic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.views.main.compose.Wavy


@Composable
fun SettingDivider() {
    Wavy(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .padding(vertical = 16.dp),
    )
}
