package com.dede.android_eggs.views.settings.compose.basic

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
import com.dede.android_eggs.views.main.compose.Wavy
import com.dede.android_eggs.views.settings.compose.prefs.SettingPrefIcon


@Composable
fun SettingDivider() {
    Wavy(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .padding(vertical = 16.dp),
    )
}

@Composable
fun SettingPref(
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    trailingContent: ImageVector,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            Box(modifier = Modifier.padding(end = 12.dp)) {
                Icon(
                    imageVector = trailingContent,
                    contentDescription = null,
                )
            }
        },
        onClick = onClick,
        shape = shape,
        content = content,
    )
}

@Composable
fun SettingPref(
    modifier: Modifier = Modifier,
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
    leadingIcon: (@Composable () -> Unit)? = null,
    title: String,
    desc: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val cardColors = CardDefaults.cardColors(
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
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    if (desc != null) {
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp),
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (trailingContent != null) {
                    trailingContent()
                }
            }
        }

        content()
    }
}

