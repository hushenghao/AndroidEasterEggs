package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.dede.android_eggs.views.main.compose.EasterEggLogo
import com.dede.android_eggs.views.main.util.EasterEggHelp.VersionFormatter
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
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

        val supportedList = componentList.filter { it.isSupported() }
        val componentCount = supportedList.size
        supportedList.forEachIndexed { index, component ->
            var checked by remember { mutableStateOf(component.isEnabled(context)) }
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
                checked = checked,
                onCheckedChange = {
                    checked = it
                    component.setEnabled(context, it)
                },
            )
        }
    }
}

@HiltViewModel
class ComponentManagerViewModel @Inject constructor(
    val componentList: List<@JvmSuppressWildcards ComponentProvider.Component>
) : ViewModel()
