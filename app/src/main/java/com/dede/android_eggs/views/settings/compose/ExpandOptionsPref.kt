package com.dede.android_eggs.views.settings.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


@Composable
fun ExpandOptionsPref(
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    initializeExpanded: Boolean = false,
    options: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(initializeExpanded) }
    val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "Arrow")
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            Box(modifier = Modifier.padding(end = 12.dp)) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotate)
                )
            }
        },
        onClick = {
            expanded = !expanded
        },
    ) {
        if (expanded) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                options()
            }
        }
    }
}


@Composable
fun imageVectorIconBlock(
    imageVector: ImageVector,
    contentDescription: String? = null
): @Composable () -> Unit {
    return {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}

@Composable
fun radioButtonBlock(selected: Boolean): @Composable () -> Unit {
    return {
        RadioButton(selected = selected, onClick = null)
    }
}

@Composable
fun <T : Any> ValueOption(
    leadingIcon: (@Composable () -> Unit)?,
    title: String,
    desc: String? = null,
    trailingContent: (@Composable () -> Unit)?,
    shape: Shape = MaterialTheme.shapes.small,
    onOptionClick: (value: T) -> Unit,
    value: T,
) {
    Option(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = trailingContent,
        shape = shape,
        onClick = {
            onOptionClick(value)
        }
    )
}

@Composable
fun Option(
    leadingIcon: (@Composable () -> Unit)?,
    title: String,
    desc: String? = null,
    trailingContent: (@Composable () -> Unit)? = {
        Icon(imageVector = Icons.AutoMirrored.Rounded.NavigateNext, contentDescription = title)
    },
    shape: Shape = MaterialTheme.shapes.small,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        shape = shape,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 12.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
        ) {
            if (leadingIcon != null) {
                Box(modifier = Modifier.widthIn(0.dp, 30.dp)) {
                    leadingIcon()
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
                    .animateContentSize()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                )
                if (desc != null) {
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (trailingContent != null) {
                Box(modifier = Modifier.widthIn(0.dp, 30.dp)) {
                    trailingContent()
                }
            }
        }
    }
}