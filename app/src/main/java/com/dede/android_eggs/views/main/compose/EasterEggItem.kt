@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.dede.android_eggs.views.main.compose

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EasterEggHelp
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.ui.views.ViscousFluidInterpolator
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt


@Composable
@Preview(showBackground = true)
fun EasterEggItem(
    base: BaseEasterEgg = EasterEggHelp.previewEasterEggs().first(),
    enableItemAnim: Boolean = false,
) {
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
            EasterEggItemContent(egg, base, supportShortcut, enableItemAnim) {
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

    val intercept = ViscousFluidInterpolator.getInstance()

    fun callbackSwipeProgress(offsetX: Float) {
        if (!supportShortcut) return
        val p = if (triggerOffsetX == 0f) 0f else
            min(abs(offsetX) / triggerOffsetX, 1f)
        onSwipe.invoke(p)
    }

    LaunchedEffect(released) {
        if (released) {
            animate(offsetX, 0f, animationSpec = tween(300)) { value, _ ->
                offsetX = value
                callbackSwipeProgress(value)
            }
        }
    }

    val view = LocalView.current
    Box(
        contentAlignment = Alignment.Center,
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
                        val p = if (triggerOffsetX == 0f) {
                            1f
                        } else {
                            intercept.getInterpolation(
                                1f - abs(offsetX) / (triggerOffsetX * 1.24f)
                            )
                        }
                        offsetX += delta * min(p, 1f)
                        if (supportShortcut) {
                            if (!needTrigger && (-offsetX) >= triggerOffsetX) {
                                needTrigger = true
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                            callbackSwipeProgress(offsetX)
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

@Composable
@Preview
fun EasterEggItemContent(
    egg: EasterEgg = EasterEggHelp.previewEasterEggs().first(),
    base: BaseEasterEgg = egg,
    supportShortcut: Boolean = true,
    enableItemAnim: Boolean = false,
    onSelected: ((index: Int) -> Unit)? = null,
) {
    val context = LocalContext.current
    val isGroup = base is EasterEggGroup
    val androidVersion = remember(egg) {
        EasterEggHelp.VersionFormatter.create(egg.apiLevel, egg.nicknameRes)
            .format(context)
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(2.dp)),
        shape = shapes.extraLarge,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(shapes.extraLarge)
                .combinedClickable(
                    onClick = {
                        EggActionHelp.launchEgg(context, egg)
                    },
                    onLongClick = {
                        if (supportShortcut) {
                            EggActionHelp.addShortcut(context, egg)
                        }
                    }
                )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp, bottom = 6.dp)
                            .weight(1f, true)
                    ) {
                        Text(
                            text = stringResource(egg.nameRes),
                            style = typography.headlineSmall,
                            modifier = if (enableItemAnim) Modifier.animateContentSize() else Modifier,
                        )
                    }
                    EasterEggLogo(egg, sensor = true)
                }
                Row(
                    modifier = Modifier
                        .clip(shapes.extraSmall)
                        .withEasterEggGroupSelector(base) {
                            onSelected?.invoke(it)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = androidVersion,
                        style = typography.bodyMedium,
                        modifier = if (enableItemAnim) Modifier.animateContentSize() else Modifier,
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
}

@Composable
@Preview(showBackground = true)
fun EasterEggItemFloor(
    egg: EasterEgg = EasterEggHelp.previewEasterEggs().first(),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = (swipeProgress * -14).dp)
            ) {
                Text(
                    text = stringResource(R.string.label_add_shortcut),
                    modifier = Modifier.padding(end = 4.dp),
                    maxLines = 2,
                    style = typography.bodyMedium
                )
                ShortcutIcon(swipeProgress >= 1f)
            }
        }
    }
}

@Composable
private fun ShortcutIcon(showShortcut: Boolean = false) {
    AnimatedContent(
        targetState = showShortcut,
        transitionSpec = {
            scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
        },
        label = "ShortcutIcon"
    ) {
        if (it) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Rounded.AppShortcut,
                contentDescription = stringResource(R.string.label_add_shortcut)
            )
        } else {
            val swipeIcon = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
                Icons.Rounded.SwipeRight
            } else {
                Icons.Rounded.SwipeLeft
            }
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = swipeIcon,
                contentDescription = stringResource(R.string.label_add_shortcut)
            )
        }
    }
}
