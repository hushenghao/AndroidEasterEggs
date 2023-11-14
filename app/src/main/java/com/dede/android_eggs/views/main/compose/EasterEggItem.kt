package com.dede.android_eggs.views.main.compose

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppShortcut
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.SwipeLeft
import androidx.compose.material.icons.rounded.SwipeRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EasterEggHelp
import com.dede.android_eggs.main.EggActionHelp
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt


@Composable
fun EasterEggItem(base: BaseEasterEgg) {
    val context = LocalContext.current

    var groupIndex by remember { mutableIntStateOf(0) }
    val egg = when (base) {
        is EasterEgg -> base
        is EasterEggGroup -> base.eggs[groupIndex]
        else -> throw UnsupportedOperationException("Unsupported type: ${base.javaClass}")
    }
    val supportShortcut = remember(egg) { EggActionHelp.isSupportShortcut(egg) }
    var swipeProgress by remember { mutableFloatStateOf(0f) }

    EasterEggItemSwipe(
        floor = {
            EasterEggItemFloor(egg, supportShortcut, swipeProgress)
        },
        content = {
            EasterEggItemContent(egg, base) {
                groupIndex = it
            }
        },
        supportShortcut = supportShortcut,
        onSwipe = {
            swipeProgress = it
        },
        addShortcut = {
            EggActionHelp.addShortcut(context, egg)
        },
    )
}

@Composable
fun EasterEggItemSwipe(
    floor: @Composable () -> Unit,
    content: @Composable () -> Unit,
    supportShortcut: Boolean,
    addShortcut: () -> Unit,
    onSwipe: (p: Float) -> Unit,
) {
    var released by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var triggerOffsetX by remember { mutableFloatStateOf(0f) }
    var needTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(released) {
        if (released) {
            val releaseAnim = Animatable(offsetX)
            releaseAnim.animateTo(0f, animationSpec = tween(200)) {
                offsetX = value
            }
        }
    }

    val view = LocalView.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(top = 12.dp)
    ) {
        floor()
        Box(
            modifier = Modifier
                .onSizeChanged { triggerOffsetX = it.width / 5f * 2 }
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX += delta
                        if (supportShortcut) {
                            if (!needTrigger && (-offsetX) >= triggerOffsetX) {
                                needTrigger = true
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                            val p = if (triggerOffsetX == 0f)
                                0f
                            else
                                min(abs(offsetX) / triggerOffsetX, 1f)
                            onSwipe.invoke(p)
                        }
                    },
                    onDragStarted = {
                        released = false
                    },
                    onDragStopped = {
                        if (supportShortcut && needTrigger && abs(offsetX) >= triggerOffsetX) {
                            addShortcut.invoke()
                        }
                        released = true
                        needTrigger = false
                    }
                ),
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasterEggItemContent(egg: EasterEgg, base: BaseEasterEgg, onSelected: (index: Int) -> Unit) {
    val context = LocalContext.current
    val isGroup = base is EasterEggGroup
    val androidVersion = remember(egg) {
        EasterEggHelp.VersionFormatter.create(egg.apiLevel, egg.nicknameRes)
            .format(context)
    }
    Card(
        onClick = {
            EggActionHelp.launchEgg(context, egg)
        },
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 22.dp, bottom = 18.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(egg.nameRes),
                    style = typography.headlineSmall,
                    modifier = Modifier
                        .padding(end = 10.dp, bottom = 6.dp)
                        .weight(1f, true)
                )
                EasterEggLogo(egg)
            }
            Row(
                modifier = Modifier.withEasterEggGroupSelector(base, onSelected),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = androidVersion,
                    style = typography.bodyMedium
                )
                if (isGroup) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(22.dp),
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.pref_title_language_more)
                    )
                }
            }
        }
    }
}

@Composable
fun EasterEggItemFloor(
    egg: EasterEgg,
    enableShortcut: Boolean = true,
    swipeProgress: Float = 0f,
) {
    val content = LocalContext.current
    val androidVersion = remember(egg) {
        EasterEggHelp.VersionFormatter.create(egg.apiLevel)
            .format(content)
    }
    val apiVersion = remember(egg) {
        EasterEggHelp.ApiLevelFormatter.create(egg.apiLevel)
            .format(content)
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 28.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EasterEggLogo(egg = egg, 36.dp)
            Text(
                text = buildAnnotatedString {
                    append(androidVersion)
                    append("\n")
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(apiVersion)
                    }
                },
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 2,
                style = typography.bodyMedium
            )
        }
        if (enableShortcut) {
            val icon = if (swipeProgress >= 1f) {
                Icons.Rounded.AppShortcut
            } else if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                Icons.Rounded.SwipeRight
            } else {
                Icons.Rounded.SwipeLeft
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = (swipeProgress * -12).dp)
            ) {
                Text(
                    text = stringResource(R.string.label_add_shortcut),
                    modifier = Modifier.padding(end = 4.dp),
                    maxLines = 3,
                    style = typography.bodyMedium
                )
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = icon,
                    contentDescription = stringResource(R.string.label_add_shortcut)
                )
            }
        }
    }
}
