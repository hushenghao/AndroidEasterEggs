package com.dede.android_eggs.views.settings.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


@Composable
fun OptionsPref(
    leadingIcon: @Composable () -> Unit,
    title: String,
    options: @Composable ColumnScope.() -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "Arrow")
    Card(
        onClick = {
            expanded = !expanded
        },
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                leadingIcon()
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null,
                    modifier = Modifier.rotate(rotate)
                )
            }
            if (expanded) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    options()
                }
            }
        }
    }
}

@Composable
fun <T : Any> ValueOption(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit,
    title: String,
    desc: String? = null,
    trailingContent: @Composable () -> Unit,
    shape: Shape = MaterialTheme.shapes.small,
    onOptionClick: (value: T) -> Unit,
    value: T,
) {
    Option(
        leadingIcon = leadingIcon,
        title = title,
        modifier = modifier,
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
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit,
    title: String,
    desc: String? = null,
    trailingContent: @Composable () -> Unit,
    shape: Shape = MaterialTheme.shapes.small,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .then(modifier)
        ) {
            leadingIcon()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
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
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            trailingContent()
        }
    }
}