package com.dede.android_eggs.views.settings.compose.groups

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.settings_ui.basic.ExpandOptionsPref
import com.dede.android_eggs.settings_ui.basic.OptionShapes
import com.dede.android_eggs.settings_ui.basic.SettingPrefUtil
import com.dede.android_eggs.settings_ui.basic.SwitchOption
import com.dede.android_eggs.settings_ui.basic.imageVectorIconBlock
import com.dede.android_eggs.views.main.compose.EasterEggBackdropBitmapCache
import com.dede.android_eggs.views.settings.compose.options.HighestEggCountOption
import com.dede.android_eggs.views.settings.compose.options.IconVisualEffectsOption
import com.dede.android_eggs.views.settings.compose.options.IconVisualEffectsPrefUtil
import com.dede.android_eggs.resources.R as StringsR

@Preview
@Composable
fun UiStyleGroup() {
    var hazeBlur by SettingPrefUtil.hazeBlurState
    var cardBackdropBlur by SettingPrefUtil.eggItemBackdropBlurState

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.AutoAwesome,
        title = stringResource(StringsR.string.pref_title_ui_style),
    ) {
        SwitchOption(
            shape = OptionShapes.firstShape(),
            leadingIcon = imageVectorIconBlock(imageVector = Icons.Rounded.BlurOn),
            title = stringResource(StringsR.string.pref_title_haze_blur),
            desc = stringResource(StringsR.string.pref_summary_haze_blur),
            checked = hazeBlur,
            onCheckedChange = {
                hazeBlur = it
            },
        )
        SwitchOption(
            leadingIcon = imageVectorIconBlock(imageVector = Icons.Rounded.Gradient),
            title = stringResource(StringsR.string.pref_title_card_backdrop_blur),
            desc = stringResource(StringsR.string.pref_summary_card_backdrop_blur),
            checked = cardBackdropBlur,
            onCheckedChange = {
                cardBackdropBlur = it
                if (!it) {
                    EasterEggBackdropBitmapCache.clear()
                }
            },
        )
        if (IconVisualEffectsPrefUtil.isSupported()) {
            IconVisualEffectsOption()
        }
        HighestEggCountOption(
            shape = OptionShapes.lastShape(),
        )
    }
}
