package com.dede.android_eggs.views.settings.compose.prefs

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.android.launcher2.RocketLauncher
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.main.compose.EasterEggLogo
import com.dede.android_eggs.views.main.util.EasterEggHelp.VersionFormatter
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.SwitchOption
import com.dede.basic.provider.ComponentProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.dede.android_eggs.resources.R as StringsR

@Composable
fun ComponentManagerPref(viewModel: ComponentManagerViewModel = hiltViewModel()) {
    val componentList = if (LocalInspectionMode.current) {
        emptyList()
    } else {
        viewModel.componentList
    }
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.AppRegistration,
        title = stringResource(id = StringsR.string.label_component_manager),
    ) {
        val context = LocalContext.current
        Option(
            shape = OptionShapes.borderShape,
            leadingIcon = {
                EasterEggLogo(
                    res = com.android.launcher2.R.mipmap.ic_rocket_launcher,
                    modifier = Modifier.size(30.dp),
                    contentDescription = stringResource(id = com.android.launcher2.R.string.dream_name),
                )
            },
            title = stringResource(com.android.launcher2.R.string.dream_name),
            desc = stringResource(com.android.launcher2.R.string.rocket_launcher_desc),
            onClick = {
                context.startActivity(Intent(context, RocketLauncher::class.java))
                LocalEvent.poster().post(SettingPrefUtil.ACTION_CLOSE_SETTING)
            }
        )

        Spacer(modifier = Modifier.height(1.dp))

        val componentCount = componentList.size
        componentList.forEachIndexed { index, component ->
            val formatter = VersionFormatter.create(component.apiLevelRange, component.nicknameRes)
            SwitchOption(
                shape = OptionShapes.indexOfShape(index = index, optionsCount = componentCount),
                leadingIcon = {
                    EasterEggLogo(
                        res = component.iconRes,
                        modifier = Modifier.size(30.dp),
                        contentDescription = stringResource(id = component.nameRes),
                    )
                },
                title = stringResource(id = component.nameRes),
                desc = formatter.format(context),
                value = component.isEnabled(context),
                onCheckedChange = {
                    component.setEnabled(context, it)
                },
            )
        }
        for (component in viewModel.componentList) {
            if (!component.isSupported()) continue

        }
    }
}

@HiltViewModel
class ComponentManagerViewModel @Inject constructor(
    val componentList: List<@JvmSuppressWildcards ComponentProvider.Component>
) : ViewModel()
