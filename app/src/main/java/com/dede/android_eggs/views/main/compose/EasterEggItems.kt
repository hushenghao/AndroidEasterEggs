@file:OptIn(ExperimentalLayoutApi::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.SwipeLeft
import androidx.compose.material.icons.rounded.SwipeRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.android_eggs.ui.composes.PHI
import com.dede.android_eggs.ui.composes.SnapshotView
import com.dede.android_eggs.ui.views.ViscousFluidInterpolator
import com.dede.android_eggs.views.main.util.AndroidReleaseDateMatcher
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp
import com.dede.android_eggs.views.main.util.EggActionHelp
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefBoolState
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import com.dede.basic.utils.AppLocaleDateFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.math.abs
import kotlin.math.min
import com.dede.android_eggs.resources.R as StringsR

@Composable
@Preview
fun EasterEggHighestItem(
    base: BaseEasterEgg = EasterEggHelp.previewEasterEggs().first()
) {
    val context = LocalContext.current
    val egg = base as EasterEgg
    val androidVersion = remember(egg) {
        EasterEggHelp.VersionFormatter.create(egg.fullApiLevelRange, egg.nicknameRes)
            .format(context)
    }
    val apiLevel = remember(egg) {
        EasterEggHelp.ApiLevelFormatter.create(egg.apiLevelRange).format(context)
    }
    val dateFormat = remember(egg, LocalConfiguration.current) {
        AppLocaleDateFormatter.getInstance("MMM yyyy")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(2.dp)),
        shape = shapes.extraLarge,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(shapes.extraLarge)
            .clickable {
                EggActionHelp.launchEgg(context, egg)
            }
    ) {
        Box {
            val isSupportShortcut = remember(egg) {
                EasterEggShortcutsHelp.isSupportShortcut(egg)
            }
            val snapshot = remember(egg) {
                egg.provideSnapshotProvider()
            }

            SnapshotView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(PHI),
                snapshot = snapshot
            )

            if (isSupportShortcut) {
                FilledIconButton(
                    onClick = {
                        EasterEggShortcutsHelp.pinShortcut(context, egg)
                    },
                    shape = IconShapePrefUtil.getIconShape(),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BookmarkAdd,
                        tint = colorScheme.onPrimary,
                        contentDescription = stringResource(id = StringsR.string.label_add_shortcut)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 18.dp, top = 14.dp, end = 18.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f, true)
            ) {
                Text(
                    text = stringResource(id = egg.nameRes),
                    style = typography.headlineSmall,
                )
                Text(
                    text = androidVersion,
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            EasterEggLogo(egg = egg, sensor = true)
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 12.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Chip(text = apiLevel)
            Chip(
                text = dateFormat.format(
                    if (LocalInspectionMode.current)
                        Date()
                    else
                        AndroidReleaseDateMatcher.findReleaseDateByApiLevel(egg.apiLevel)
                )
            )
        }
    }
}

@Composable
private fun Chip(text: String) {
    Card(
        shape = CircleShape
    ) {
        Text(
            text = text,
            style = typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Immutable
private class EasterEggState(val base: BaseEasterEgg, index: Int) {
    private val indexState: MutableState<Int> = mutableIntStateOf(index)

    fun getEasterEgg(): EasterEgg {
        return when (base) {
            is EasterEgg -> base
            is EasterEggGroup -> base.eggs[indexState.value]
            else -> throw IllegalArgumentException("Unknown EasterEgg type: ${base::class.java.name}")
        }
    }

    fun selectedEgg(newIndex: Int) {
        if (base is EasterEggGroup) {
            val index = newIndex.coerceIn(0, base.eggs.size - 1)
            base.selectedIndex = index
            indexState.value = index
        }
    }
}

@Composable
private fun rememberEasterEggState(base: BaseEasterEgg): EasterEggState {
    return remember(base) {
        EasterEggState(base, if (base is EasterEggGroup) base.selectedIndex else 0)
    }
}

@Composable
private fun EasterEggItemSwipeGuide(isEnabled: Boolean, state: EasterEggItemSwipeState) {
    if (!isEnabled) {
        return
    }
    var needGuidePref by rememberPrefBoolState(SettingPrefUtil.KEY_EGG_ITEM_NEED_GUIDE_SWIPE, true)
    if (!needGuidePref) {
        return
    }
    val navigator = LocalNavigator.current
    LaunchedEffect(navigator.currentRoute) {
        if (navigator.currentRoute != EasterEggsDestination.EasterEggs) {
            return@LaunchedEffect
        }
        val maxOffset = state.triggerOffsetXState.floatValue * -0.9f
        state.animateToOffset(
            maxOffset,
            tween(800, delayMillis = 300)
        )
        delay(1000)
        state.animateToRelease()
        needGuidePref = false
    }
}

@Composable
@Preview(showBackground = true)
fun EasterEggItem(
    base: BaseEasterEgg = EasterEggHelp.previewEasterEggs().first(),
    enableItemAnim: Boolean = false,
    index: Int = -1,
) {
    val context = LocalContext.current

    val easterEggState = rememberEasterEggState(base)
    val egg = easterEggState.getEasterEgg()
    val supportShortcut = remember(egg) { EasterEggShortcutsHelp.isSupportShortcut(egg) }

    val state = rememberEasterEggItemSwipeState()
    EasterEggItemSwipeGuide(isEnabled = index == 0, state = state)
    EasterEggItemSwipe(
        state = state,
        floor = {
            EasterEggItemFloor(egg, supportShortcut, state.swipeProgress)
        },
        content = {
            EasterEggItemContent(egg, base, enableItemAnim) { index ->
                easterEggState.selectedEgg(index)
            }
        },
        supportShortcut = supportShortcut,
        addShortcut = {
            EasterEggShortcutsHelp.pinShortcut(context, egg)
        },
    )
}

@Composable
private fun rememberEasterEggItemSwipeState(): EasterEggItemSwipeState {
    return remember() { EasterEggItemSwipeState() }
}

@OptIn(ExperimentalAtomicApi::class)
@Stable
private class EasterEggItemSwipeState {
    val offsetXState = mutableFloatStateOf(0f)
    val triggerOffsetXState = mutableFloatStateOf(0f)

    val swipeProgress: Float
        get() {
            val offsetX = offsetXState.floatValue
            val triggerOffsetX = triggerOffsetXState.floatValue
            return if (triggerOffsetX == 0f) 0f else min(abs(offsetX) / triggerOffsetX, 1f)
        }

    suspend fun animateToOffset(
        targetOffset: Float,
        animationSpec: AnimationSpec<Float> = tween(300),
    ) {
        animate(offsetXState.floatValue, targetOffset, animationSpec = animationSpec) { value, _ ->
            offsetXState.floatValue = value
        }
    }

    suspend fun animateToRelease() {
        animateToOffset(0f)
    }
}

@Composable
private fun EasterEggItemSwipe(
    state: EasterEggItemSwipeState = rememberEasterEggItemSwipeState(),
    floor: @Composable () -> Unit,
    content: @Composable () -> Unit,
    supportShortcut: Boolean,
    addShortcut: () -> Unit,
) {
    val currentAddShortcut by rememberUpdatedState(newValue = addShortcut)

    var offsetX by state.offsetXState
    var triggerOffsetX by state.triggerOffsetXState

    val scope = rememberCoroutineScope()
    Box(
        contentAlignment = Alignment.Center,
    ) {
        if (offsetX != 0f) {
            floor()
        }

        var needTrigger by remember { mutableStateOf(false) }
        val intercept = remember { ViscousFluidInterpolator.getInstance() }

        val hapticFeedback = LocalHapticFeedback.current
        val draggableState = rememberDraggableState { delta ->
            val p = if (triggerOffsetX == 0f) {
                1f
            } else {
                intercept.getInterpolation(1f - abs(offsetX) / (triggerOffsetX * 1.24f))
            }
            offsetX += delta * min(p, 1f)

            if (supportShortcut) {
                if (!needTrigger && (-offsetX) >= triggerOffsetX) {
                    needTrigger = true
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                }
            }
        }
        Box(
            modifier = Modifier
                .onSizeChanged { triggerOffsetX = it.width / 5f * 2 }
                .offset { IntOffset(offsetX.fastRoundToInt(), 0) }
                .draggable(
                    reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
                    orientation = Orientation.Horizontal,
                    state = draggableState,
                    onDragStopped = {
                        if (supportShortcut && needTrigger && abs(offsetX) >= triggerOffsetX) {
                            currentAddShortcut()
                        }
                        scope.launch {
                            state.animateToRelease()
                        }
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
    enableItemAnim: Boolean = false,
    onSelected: ((index: Int) -> Unit)? = null,
) {
    val context = LocalContext.current
    val isGroup = base is EasterEggGroup
    val androidVersion = remember(egg.fullApiLevelRange) {
        EasterEggHelp.VersionFormatter.create(egg.fullApiLevelRange, egg.nicknameRes)
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
                .clickable {
                    EggActionHelp.launchEgg(context, egg)
                }
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
                            contentDescription = stringResource(StringsR.string.pref_title_language_more)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun EasterEggItemFloor(
    egg: EasterEgg = EasterEggHelp.previewEasterEggs().first(),
    enableShortcut: Boolean = true,
    swipeProgress: Float = 0f,
) {
    val content = LocalContext.current
    val androidVersion = remember(egg.fullApiLevelRange, content) {
        EasterEggHelp.VersionFormatter.create(egg.fullApiLevelRange)
            .format(content)
    }
    val apiVersion = remember(egg.apiLevelRange, content) {
        EasterEggHelp.ApiLevelFormatter.create(egg.apiLevelRange)
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
                    text = stringResource(StringsR.string.label_add_shortcut),
                    modifier = Modifier.padding(end = 4.dp),
                    maxLines = 2,
                    style = typography.labelLarge
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
                imageVector = Icons.Rounded.BookmarkAdd,
                contentDescription = stringResource(StringsR.string.label_add_shortcut)
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
                contentDescription = stringResource(StringsR.string.label_add_shortcut)
            )
        }
    }
}
