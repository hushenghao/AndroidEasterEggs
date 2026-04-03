package com.dede.android_eggs.views.settings.compose.widgets

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface WidgetPreviewProvider {
    val order: Int

    @get:StringRes
    val descriptionRes: Int

    @Composable
    fun Preview(modifier: Modifier = Modifier)

    fun requestPin(context: Context)
}
