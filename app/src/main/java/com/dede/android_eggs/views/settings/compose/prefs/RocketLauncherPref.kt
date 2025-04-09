package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.android.launcher2.RocketLauncher
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil

@Composable
fun RocketLauncherPref() {
    val context = LocalContext.current
    SettingPref(
        leadingIcon = Icons.Rounded.RocketLaunch,
        title = stringResource(com.android.launcher2.R.string.dream_name),
        desc = stringResource(com.android.launcher2.R.string.rocket_launcher_desc),
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            context.startActivity(Intent(context, RocketLauncher::class.java))
            LocalEvent.poster().post(SettingPrefUtil.ACTION_CLOSE_SETTING)
        }
    )
}
