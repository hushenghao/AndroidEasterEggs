package com.dede.android_eggs.views.settings.compose.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.util.compose.bottom
import com.dede.android_eggs.util.compose.top

@Composable
internal fun ExpandOptionsPrefTrailing(
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "Arrow")
    Box(modifier = Modifier.padding(end = 12.dp)) {
        Icon(
            imageVector = Icons.Rounded.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier
                .rotate(rotate)
                .then(modifier)
        )
    }
}

@Composable
fun ExpandOptionsPref(
    expandedState: MutableState<Boolean>,
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    onClick: () -> Unit = {
        expandedState.value = !expandedState.value
    },
    trailingContent: @Composable (expended: Boolean) -> Unit = {
        ExpandOptionsPrefTrailing(it)
    },
    options: @Composable ColumnScope.() -> Unit
) {
    SettingPref(
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            trailingContent(expandedState.value)
        },
        onClick = onClick,
    ) {
        AnimatedVisibility(
            visible = expandedState.value,
            enter = slideInVertically() + fadeIn(),
            exit = shrinkVertically(
                animationSpec = spring(
                    stiffness = 1200f,
                    visibilityThreshold = IntSize.VisibilityThreshold
                )
            ) + fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 4.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = options
            )
        }
    }
}

@Composable
fun ExpandOptionsPref(
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    initializeExpanded: Boolean = false,
    options: @Composable ColumnScope.() -> Unit
) {
    ExpandOptionsPref(
        expandedState = rememberSaveable { mutableStateOf(initializeExpanded) },
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        options = options
    )
}

object OptionShapes {

    val defaultShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.small

    val borderShape: CornerBasedShape
        @Composable
        get() = MaterialTheme.shapes.medium

    @Composable
    fun indexOfShape(index: Int, optionsCount: Int): Shape {
        return if (optionsCount == 1) {
            borderShape
        } else if (index == 0 && optionsCount > 1) {
            firstShape()
        } else if (index == optionsCount - 1 && optionsCount > 1) {
            lastShape()
        } else {
            defaultShape
        }
    }

    @Composable
    fun lastShape(): Shape {
        return defaultShape.bottom(borderShape)
    }

    @Composable
    fun firstShape(): Shape {
        return defaultShape.top(borderShape)
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
    shape: Shape = OptionShapes.defaultShape,
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
fun <T : Any> RadioOption(
    leadingIcon: (@Composable () -> Unit)?,
    title: String,
    desc: String? = null,
    shape: Shape = OptionShapes.defaultShape,
    value: T,
    currentValueState: MutableState<T>,
    onOptionClick: (value: T) -> Unit = {
        currentValueState.value = value
    },
    trailingContent: @Composable () -> Unit = radioButtonBlock(currentValueState.value == value),
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
fun SwitchOption(
    leadingIcon: (@Composable () -> Unit)?,
    title: String,
    desc: String? = null,
    shape: Shape = OptionShapes.defaultShape,
    value: Boolean = false,
    onCheckedChange: (checked: Boolean) -> Unit
) {
    var isChecked by remember { mutableStateOf(value) }
    Option(
        shape = shape,
        leadingIcon = leadingIcon,
        title = title,
        desc = desc,
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    onCheckedChange(it)
                },
            )
        },
        onClick = {
            isChecked = !isChecked
            onCheckedChange(isChecked)
        }
    )
}

@Composable
fun Option(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)?,
    title: String,
    desc: String? = null,
    trailingContent: (@Composable () -> Unit)? = {
        Icon(imageVector = Icons.AutoMirrored.Rounded.NavigateNext, contentDescription = title)
    },
    shape: Shape = OptionShapes.defaultShape,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
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
}