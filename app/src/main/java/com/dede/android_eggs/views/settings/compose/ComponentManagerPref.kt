package com.dede.android_eggs.views.settings.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EasterEggHelp.VersionFormatter
import com.dede.basic.provider.ComponentProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun ComponentManagerPref(viewModel: ComponentManagerViewModel = viewModel()) {
    val supportedComponentList = remember(viewModel.componentList) {
        viewModel.componentList.filter { it.isSupported() }
    }
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.AppRegistration,
        title = stringResource(id = R.string.label_component_manager),
    ) {
        val componentCount = supportedComponentList.size
        val context = LocalContext.current
        supportedComponentList.forEachIndexed { index, component ->
            val formatter = VersionFormatter.create(component.apiLevel, component.nicknameRes)
            SwitchOption(
                shape = OptionShapes.indexOfShape(index = index, optionsCount = componentCount),
                leadingIcon = {
                    Image(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(component.iconRes),
                        contentDescription = null,
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
class ComponentManagerViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var componentList: List<@JvmSuppressWildcards ComponentProvider.Component>
}