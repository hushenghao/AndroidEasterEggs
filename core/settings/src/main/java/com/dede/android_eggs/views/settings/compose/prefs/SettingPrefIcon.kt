package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil.toShapePlus

@Composable
fun SettingPrefIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val iconShapeIndex by SettingPrefUtil.iconShapeValueState
    var newPolygon: RoundedPolygon? by remember { mutableStateOf(null) }
    LaunchedEffect(iconShapeIndex) {
        newPolygon = IconShapePrefUtil.getIconShapeRoundedPolygon(iconShapeIndex)
    }
    SettingPrefIcon(
        icon = icon,
        modifier = modifier,
        shape = newPolygon.toShapePlus(),
        contentDescription = contentDescription,
    )
}

@Composable
fun SettingPrefIcon(
    icon: ImageVector,
    shape: Shape,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.padding(6.dp)
        )
    }
}
