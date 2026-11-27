package com.dede.android_eggs.views.settings.compose.options

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.settings_ui.basic.OptionShapes
import com.dede.android_eggs.settings_ui.basic.SettingPrefUtil
import com.dede.android_eggs.settings_ui.basic.SwitchOption
import com.dede.android_eggs.settings_ui.basic.imageVectorIconBlock
import com.dede.android_eggs.resources.R as StringsR

object IconVisualEffectsPrefUtil {

    // The effect matrix only applies to an adaptive icon's foreground layer
    // (AlterableAdaptiveIconDrawable#setForegroundMatrix), and adaptive icons
    // (AdaptiveIconDrawable) only exist from API 26. Simulating adaptive icons
    // below API 26 with VectorDrawable + layer-list looked bad, so the feature
    // is simply hidden on Android 8.0-.
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}

@Composable
fun IconVisualEffectsOption(shape: Shape = OptionShapes.defaultShape) {
    var intState by SettingPrefUtil.iconVisualEffectsState
    SwitchOption(
        shape = shape,
        leadingIcon = imageVectorIconBlock(imageVector = Icons.Rounded.Animation),
        title = stringResource(StringsR.string.pref_title_icon_visual_effects),
        checked = intState == SettingPrefUtil.ON,
        onCheckedChange = {
            intState = if (it) {
                SettingPrefUtil.ON
            } else {
                SettingPrefUtil.OFF
            }
        },
    )
}
