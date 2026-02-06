package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.android_eggs.resources.R as StringsR

@Preview
@Composable
fun TimelinePref() {
    val navigator = LocalNavigator.current
    SettingPref(
        leadingIcon = Icons.Rounded.Timeline,
        title = stringResource(id = StringsR.string.label_timeline),
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            navigator.navigate(EasterEggsDestination.TimelineDialog)
        }
    )
}
