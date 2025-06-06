package com.dede.android_eggs.views.settings.compose.options

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.runtime.Composable
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.basic.uiHandler

@Composable
fun TestCrashOption() {
    Option(
        leadingIcon = imageVectorIconBlock(Icons.Rounded.SentimentDissatisfied),
        title = "Test Crash",
        onClick = {
            uiHandler.post {
                throw IllegalStateException("This is a test crash triggered by TestCrashOption.")
            }
        }
    )
}
