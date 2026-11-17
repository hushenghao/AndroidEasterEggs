package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.settings_ui.basic.SettingPref
import com.dede.android_eggs.settings_ui.basic.SettingPrefUtil
import kotlin.math.roundToInt
import com.dede.android_eggs.resources.R as StringsR

private const val HIGHEST_EGG_COUNT_MIN = 0f
private const val HIGHEST_EGG_COUNT_MAX = 4f

@Composable
fun HighestEggCountPref() {
    val state = SettingPrefUtil.highestEggCountState
    val sliderState = rememberSliderState(
        value = state.intValue.toFloat(),
        // 5 discrete stops (0-4) between the range ends; 4 means every egg.
        steps = 3,
        trackRange = HIGHEST_EGG_COUNT_MIN..HIGHEST_EGG_COUNT_MAX,
    )
    SettingPref(
        leadingIcon = Icons.Rounded.Star,
        title = stringResource(StringsR.string.pref_title_highest_egg_count),
        desc = stringResource(StringsR.string.pref_desc_highest_egg_count),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 10.dp),
            ) {
                Slider(
                    state = sliderState,
                    onValueChange = {
                        sliderState.value = it
                        state.intValue = it.roundToInt()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                // Tick labels under the slider; SpaceBetween centers the middle
                // labels on the tick positions and keeps the ends aligned.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "2",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "3",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(StringsR.string.pref_summary_highest_egg_count_all),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
    )
}
