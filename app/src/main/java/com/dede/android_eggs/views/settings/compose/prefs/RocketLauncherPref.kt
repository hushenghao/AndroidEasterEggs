package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallMerge
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.android.launcher2.RocketLauncher
import com.android.launcher2.RocketLauncherPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.RadioOption
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefIntState
import com.dede.android_eggs.resources.R as StringR

fun launchRocketLauncher(context: Context) {
    context.startActivity(Intent(context, RocketLauncher::class.java))
}

@Composable
fun RocketLauncherPref() {
    val context = LocalContext.current
    val expandedState = rememberSaveable { mutableStateOf(false) }
    val currentIconsValueState = rememberPrefIntState(
        RocketLauncherPrefUtil.KEY_ROCKET_LAUNCHER_ICONS_SOURCE,
        RocketLauncherPrefUtil.VALUE_DEFAULT
    )
    ExpandOptionsPref(
        expandedState = expandedState,
        leadingIcon = Icons.Rounded.RocketLaunch,
        title = stringResource(com.android.launcher2.R.string.rocket_launcher_dream_name),
        desc = stringResource(com.android.launcher2.R.string.rocket_launcher_desc),
        onClick = {
            launchRocketLauncher(context)
        },
        trailingContent = { expanded ->
            val rotate by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                label = "Arrow"
            )
            IconButton(
                onClick = { expandedState.value = !expandedState.value },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotate)
                )
            }
        }
    ) {
        RadioOption(
            leadingIcon = imageVectorIconBlock(imageVector = Icons.Rounded.Android),
            title = stringResource(StringR.string.rocket_launcher_easter_egg_icons),
            shape = OptionShapes.firstShape(),
            value = RocketLauncherPrefUtil.VALUE_EASTER_EGG_ICONS,
            currentValueState = currentIconsValueState
        )
        RadioOption(
            leadingIcon = imageVectorIconBlock(imageVector = Icons.Rounded.Apps),
            title = stringResource(StringR.string.rocket_launcher_all_app_icons),
            value = RocketLauncherPrefUtil.VALUE_ALL_APP_ICONS,
            desc = stringResource(StringR.string.rocket_launcher_query_all_app_desc),
            currentValueState = currentIconsValueState
        )
        RadioOption(
            leadingIcon = imageVectorIconBlock(imageVector = Icons.AutoMirrored.Rounded.CallMerge),
            title = stringResource(StringR.string.rocket_launcher_all_icons),
            shape = OptionShapes.lastShape(),
            value = RocketLauncherPrefUtil.VALUE_ALL_ICONS,
            currentValueState = currentIconsValueState
        )
    }
}
