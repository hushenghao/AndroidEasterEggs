package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dede.android_eggs.R
import com.dede.android_eggs.views.main.compose.DrawableImage
import com.dede.android_eggs.views.main.util.EasterEggHelp.VersionFormatter
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.SwitchOption
import com.dede.basic.provider.ComponentProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun ComponentManagerPref(viewModel: ComponentManagerViewModel = viewModel()) {
    val componentList = if (LocalInspectionMode.current) {
        emptyList()
    } else {
        viewModel.componentList
    }
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.AppRegistration,
        title = stringResource(id = R.string.label_component_manager),
    ) {
        val componentCount = componentList.size
        val context = LocalContext.current
        componentList.forEachIndexed { index, component ->
            val formatter = VersionFormatter.create(component.apiLevel, component.nicknameRes)
            SwitchOption(
                shape = OptionShapes.indexOfShape(index = index, optionsCount = componentCount),
                leadingIcon = {
                    DrawableImage(
                        res = component.iconRes,
                        modifier = Modifier.size(28.dp),
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
