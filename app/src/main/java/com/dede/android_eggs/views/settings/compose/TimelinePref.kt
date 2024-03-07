package com.dede.android_eggs.views.settings.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.views.main.compose.LocalFragmentManager
import com.dede.android_eggs.views.main.compose.currentOutInspectionMode
import com.dede.android_eggs.views.timeline.AndroidTimelineFragment

@Composable
fun TimelinePref() {
    val fragmentManager = LocalFragmentManager.currentOutInspectionMode
    SettingPref(
        leadingIcon = Icons.Rounded.Timeline,
        title = stringResource(id = R.string.label_timeline),
        trailingContent = {
            Box(modifier = Modifier.padding(end = 10.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                    contentDescription = null,
                )
            }
        },
        onClick = {
            if (fragmentManager != null) {
                AndroidTimelineFragment.show(fragmentManager)
            }
        }
    )
}