package com.dede.android_eggs.views.settings.compose.prefs

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.SwitchIntPref
import com.dede.android_eggs.resources.R as StringsR

object IconVisualEffectsPrefUtil {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}

@Composable
fun IconVisualEffectsPref() {
    SwitchIntPref(
        state = SettingPrefUtil.iconVisualEffectsState,
        leadingIcon = Icons.Rounded.Animation,
        title = stringResource(StringsR.string.pref_title_icon_visual_effects),
    )
}
