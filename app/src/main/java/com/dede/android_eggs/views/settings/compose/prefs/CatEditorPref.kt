package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.android_eggs.ui.composes.icons.rounded.Cat
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.android_eggs.resources.R as StringR

@Composable
fun CatEditorPref() {
    val navigator = LocalNavigator.current
    SettingPref(
        leadingIcon = Icons.Rounded.Cat,
        title = stringResource(StringR.string.cat_editor),
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            navigator.navigate(EasterEggsDestination.CatEditor)
        }
    )
}
