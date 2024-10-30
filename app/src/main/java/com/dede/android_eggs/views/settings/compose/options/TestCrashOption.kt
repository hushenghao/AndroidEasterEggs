package com.dede.android_eggs.views.settings.compose.options

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.runtime.Composable
import com.dede.android_eggs.crash.GlobalExceptionHandler
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.basic.uiHandler

@Composable
fun TestCrashOption() {
    SettingPref(
        leadingIcon = Icons.Rounded.SmartToy,
        title = "Test Crash",
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            uiHandler.post {
                GlobalExceptionHandler.testCrash()
            }
        }
    )
}
