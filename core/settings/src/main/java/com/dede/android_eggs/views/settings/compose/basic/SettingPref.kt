package com.dede.android_eggs.views.settings.compose.basic

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SettingPref(
    modifier: Modifier = Modifier,
    colors: CardColors? = null,
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    trailingContent: ImageVector,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SettingPref(
        modifier = modifier,
        colors = colors,
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            Icon(
                modifier = Modifier.padding(end = 12.dp),
                imageVector = trailingContent,
                contentDescription = title,
            )
        },
        onClick = onClick,
        shape = shape,
        content = content,
    )
}

@Composable
fun SettingPref(
    modifier: Modifier = Modifier,
    colors: CardColors? = null,
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SettingPref(
        modifier = modifier,
        colors = colors,
        leadingIcon = {
            SettingPrefIcon(
                icon = leadingIcon,
                contentDescription = title,
                modifier = Modifier.padding(start = 12.dp),
            )
        },
        title = title,
        desc = desc,
        trailingContent = trailingContent,
        onClick = onClick,
        shape = shape,
        content = content
    )
}

@Composable
fun SettingPref(
    modifier: Modifier = Modifier,
    colors: CardColors? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    title: String,
    desc: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SettingPref(
        modifier = modifier,
        colors = colors,
        leadingIcon = leadingIcon,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
        },
        desc = {
            if (desc != null) {
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        trailingContent = trailingContent,
        onClick = onClick,
        shape = shape,
        content = content
    )
}

@Composable
fun SettingPref(
    modifier: Modifier = Modifier,
    colors: CardColors? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    title: @Composable () -> Unit = {},
    desc: @Composable () -> Unit = {},
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val cardColors = colors ?: CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    )
    Card(
        shape = shape,
        colors = cardColors,
        modifier = Modifier.animateContentSize() then modifier,
    ) {
        Card(
            onClick = onClick,
            shape = shape,
            colors = cardColors,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.defaultMinSize(minHeight = 58.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    title()

                    desc()
                }
                if (trailingContent != null) {
                    trailingContent()
                }
            }
        }

        content()
    }
}
