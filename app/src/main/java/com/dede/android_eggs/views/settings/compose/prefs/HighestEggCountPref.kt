package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.preferences.AppSettings
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
    val interactionSource = remember { MutableInteractionSource() }
    val colors = SliderDefaults.colors()
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
                    colors = colors,
                    interactionSource = interactionSource,
                    thumb = { thumbState ->
                        SliderThumbWithBubble(
                            sliderState = thumbState,
                            interactionSource = interactionSource,
                            colors = colors,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}

/**
 * The default handle with a value bubble floating above it while the user is
 * dragging. The bubble is placed in a custom [Layout] so it overflows the
 * handle's 4dp-wide bounds without widening the thumb, which would otherwise
 * shift the track insets.
 */
@Composable
private fun SliderThumbWithBubble(
    sliderState: SliderState,
    interactionSource: MutableInteractionSource,
    colors: SliderColors,
) {
    val allLabel = stringResource(StringsR.string.pref_summary_highest_egg_count_all)
    val value = sliderState.value.roundToInt()
    val label = if (value == AppSettings.HIGHEST_EGG_COUNT_ALL) allLabel else value.toString()

    Layout(
        content = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                isVertical = false,
                colors = colors,
                enabled = true,
            )
            if (sliderState.isDragging) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    shadowElevation = 4.dp,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        },
    ) { measurables, constraints ->
        val handle = measurables[0].measure(constraints)
        val bubble = measurables.getOrNull(1)?.measure(
            constraints.copy(minWidth = 0, maxWidth = Constraints.Infinity)
        )
        layout(handle.width, handle.height) {
            handle.place(0, 0)
            if (bubble != null) {
                val halfBubble = bubble.width / 2f
                // Mirror SliderImpl's placement: the handle center sits at
                // (containerWidth - handle.width) * fraction + handle.width / 2.
                val thumbCenter = (constraints.maxWidth - handle.width) *
                    sliderState.coercedValueAsFraction + handle.width / 2f
                // Keep the bubble inside the slider container at the range ends.
                val centerX = thumbCenter.coerceIn(
                    halfBubble,
                    (constraints.maxWidth - halfBubble),
                )
                bubble.place(
                    x = (centerX - thumbCenter + (handle.width - bubble.width) / 2f).roundToInt(),
                    y = -bubble.height - 4.dp.roundToPx(),
                )
            }
        }
    }
}
