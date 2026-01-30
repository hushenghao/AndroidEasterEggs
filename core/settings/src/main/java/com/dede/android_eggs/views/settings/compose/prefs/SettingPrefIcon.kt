@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SettingPrefIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    SettingPrefIcon(
        icon = icon,
        modifier = modifier,
        shape = IconShapePrefUtil.getIconShape(),
        contentDescription = contentDescription,
    )
}

object SettingPrefIconDefaults {
    @Composable
    fun defaultColors() = SettingPrefIconColors(
        containerColor = colorScheme.secondaryContainer,
        contentColor = colorScheme.onSecondaryContainer,
    )
}

@Immutable
class SettingPrefIconColors(
    val containerColor: Color,
    val contentColor: Color,
)

@Composable
fun SettingPrefIcon(
    icon: ImageVector,
    shape: Shape,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    colors: SettingPrefIconColors = SettingPrefIconDefaults.defaultColors(),
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
    ) {
        Box(
            modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )
        }
    }
}
