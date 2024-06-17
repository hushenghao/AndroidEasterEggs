package com.dede.android_eggs.views.settings.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.R
import com.dede.android_eggs.views.timeline.TimelineList

@Preview
@Composable
fun TimelinePref() {
    val showSheetState = remember { mutableStateOf(false) }

    TimelineList(showSheetState)

    SettingPref(
        leadingIcon = Icons.Rounded.Timeline,
        title = stringResource(id = R.string.label_timeline),
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            showSheetState.value = true
        }
    )
}
