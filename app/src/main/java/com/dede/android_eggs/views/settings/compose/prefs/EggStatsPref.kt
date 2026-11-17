package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.navigation.LocalOverlayManager
import com.dede.android_eggs.navigation.OverlayRoute
import com.dede.android_eggs.settings_ui.basic.SettingPref
import com.dede.android_eggs.resources.R as StringsR

@Preview
@Composable
fun EggStatsPref() {
    val overlayManager = LocalOverlayManager.current
    SettingPref(
        leadingIcon = Icons.Rounded.Leaderboard,
        title = stringResource(StringsR.string.label_egg_stats),
        desc = stringResource(StringsR.string.label_egg_stats_desc),
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            overlayManager.show(OverlayRoute.EggStatsDialog)
        },
    )
}
