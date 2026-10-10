@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.carousel.CarouselDefaults.multiBrowseFlingBehavior
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigationevent.NavigationEvent
import com.dede.android_eggs.composable.appbar.HazeScaffoldDefaults.hazeOverlayBackdrop
import com.dede.android_eggs.icon_shape.IconShapePrefUtil
import com.dede.android_eggs.navigation.LocalOverlayManager
import com.dede.android_eggs.navigation.OverlayRoute
import com.dede.android_eggs.settings_ui.basic.SettingPref
import com.dede.android_eggs.ui.composes.PHI
import com.dede.android_eggs.ui.composes.PredictiveBackProgressHandler.predictiveBackShrink
import com.dede.android_eggs.ui.composes.SnapshotView
import com.dede.android_eggs.ui.composes.predictiveBackProgressEventState
import com.dede.android_eggs.views.main.util.EasterEggHelp.ApiLevelFormatter
import com.dede.android_eggs.views.main.util.EasterEggHelp.VersionFormatter
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dede.android_eggs.resources.R as StringR

@Composable
fun SnapshotPref() {
    val overlayManager = LocalOverlayManager.current
    SettingPref(
        title = stringResource(StringR.string.label_snapshot_preview),
        leadingIcon = Icons.Rounded.ViewCarousel,
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            overlayManager.show(OverlayRoute.SnapshotPreview)
        }
    )
}

private val SnapshotOverlayMaxWidth = 560.dp

private const val SNAPSHOT_OVERLAY_ENTER_DURATION = 220
private const val SNAPSHOT_OVERLAY_EXIT_DURATION = 180

/**
 * Full screen snapshot preview over a blurred copy of the screen.
 *
 * An overlay of the app window itself, not a dialog window: the blur samples the window wide Haze
 * source in the same draw pass it is recorded in, so the backdrop is complete from its first
 * frame, and the fade in and out are ours to animate.
 */
@Composable
fun SnapshotOverlay(onDismiss: () -> Unit) {
    // Starts hidden so that the enter transition plays on the first composition. Those are also
    // the states that tell when the exit animation is over, see below.
    val visibility = remember { MutableTransitionState(false).apply { targetState = true } }
    val dismiss = { visibility.targetState = false }

    // The predictive back gesture dissolves the blur as it progresses, and dismissing it once the
    // gesture is let go fades the rest out. A cancelled gesture walks the progress back to 0. The
    // swipe edge side fades out along the progress while the opposite side stays fully blurred,
    // evenly for a non-edge back.
    val backEvent by predictiveBackProgressEventState(
        enabled = visibility.targetState,
        onBackCompleted = dismiss
    )
    val backProgress = backEvent.progress.coerceIn(0f, 1f)
    val opacity = 1f - backProgress
    val (startIntensity, endIntensity) = when (backEvent.swipeEdge) {
        NavigationEvent.EDGE_LEFT -> opacity to 1f
        NavigationEvent.EDGE_RIGHT -> 1f to opacity
        else -> opacity to opacity
    }
    val shrinkOrigin = when (backEvent.swipeEdge) {
        // LayoutDirection.Ltr
        NavigationEvent.EDGE_LEFT -> Alignment.CenterEnd
        NavigationEvent.EDGE_RIGHT -> Alignment.CenterStart
        else -> Alignment.Center
    }

    AnimatedVisibility(
        label = "SnapshotOverlay",
        visibleState = visibility,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(tween(SNAPSHOT_OVERLAY_ENTER_DURATION, easing = LinearOutSlowInEasing)),
        exit = fadeOut(tween(SNAPSHOT_OVERLAY_EXIT_DURATION, easing = FastOutLinearInEasing)),
    ) {
        val overlayTitle = stringResource(StringR.string.label_snapshot_preview)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { paneTitle = overlayTitle },
            contentAlignment = Alignment.Center,
        ) {
            // Full screen and the topmost hit target, so it both carries the blur and keeps the
            // screen behind it from being touched. Like the sheet scrim it sits below the
            // content, which keeps taps on the content from dismissing.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeOverlayBackdrop(
                        style = HazeMaterials.thin().then {
                            progressive(
                                HazeProgressive.horizontalGradient(
                                    easing = LinearEasing,
                                    startIntensity = startIntensity,
                                    endIntensity = endIntensity,
                                )
                            )
                        }
                    )
                    .pointerInput(Unit) {
                        detectTapGestures { dismiss() }
                    },
            )
            SnapshotCarousel(
                modifier = Modifier
                    .graphicsLayer {
                        predictiveBackShrink(
                            progress = backProgress,
                            shrinkFactor = 0.1f,
                            shrinkOrigin = shrinkOrigin,
                        )
                    }
                    .safeDrawingPadding()
                    .widthIn(max = SnapshotOverlayMaxWidth)
                    .padding(horizontal = 24.dp),
            )
        }
    }

    // Clearing the route right away would cut the fade out off, so wait for it to be over.
    LaunchedEffect(visibility.targetState, visibility.isIdle) {
        if (!visibility.targetState && visibility.isIdle) {
            onDismiss()
        }
    }
}

@HiltViewModel
class SnapshotViewModel @Inject constructor(
    val easterEggs: List<@JvmSuppressWildcards EasterEgg>
) : ViewModel()

@Preview
@Composable
fun SnapshotCarousel(
    modifier: Modifier = Modifier,
    viewModel: SnapshotViewModel = hiltViewModel(),
    showEasterEggName: Boolean = true,
    carouselFeedback: Boolean = true,
) {
    val pairList = remember(viewModel) {
        buildList {
            for (egg in viewModel.easterEggs) {
                val snapshot = egg.provideSnapshotProvider() ?: continue
                add(snapshot to egg)
            }
        }
    }
    Column(modifier = modifier) {
        val carouselState = rememberCarouselState { pairList.size }
        if (carouselFeedback) {
            val hapticFeedback = LocalHapticFeedback.current
            LaunchedEffect(carouselState) {
                snapshotFlow { carouselState.currentItem }
                    .distinctUntilChangedIgnoreInitializeValue(0)
                    .collect {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    }
            }
        }

        HorizontalCenteredHeroCarousel(
            state = carouselState,
            flingBehavior = multiBrowseFlingBehavior(carouselState),
            itemSpacing = 6.dp,
            minSmallItemWidth = 34.dp,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PHI),
        ) { i ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .maskClip(MaterialTheme.shapes.extraLarge)
            ) {
                SnapshotView(
                    modifier = Modifier.fillMaxSize(),
                    snapshot = pairList[i].first,
                )
                if (showEasterEggName) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        val tooltipState = rememberTooltipState()
                        val scope = rememberCoroutineScope()
                        TooltipBox(
                            state = tooltipState,
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above,
                                spacingBetweenTooltipAndAnchor = 12.dp,
                            ),
                            tooltip = {
                                PlainTooltip {
                                    val egg = pairList[i].second
                                    val eggName = stringResource(egg.nameRes)
                                    val context = LocalContext.current
                                    val tooltip = remember(egg.fullApiLevelRange, eggName) {
                                        val versionFormatter =
                                            VersionFormatter.create(egg.fullApiLevelRange)
                                        val apiLevelFormatter =
                                            ApiLevelFormatter.create(egg.fullApiLevelRange)
                                        """
                                            ${versionFormatter.format(context)}
                                            ${apiLevelFormatter.format(context)}
                                            $eggName
                                        """.trimIndent()
                                    }
                                    Text(text = tooltip)
                                }
                            },
                        ) {
                            FilledTonalIconButton(
                                modifier = Modifier.size(36.dp),
                                shape = IconShapePrefUtil.getIconShape(),
                                onClick = {
                                    scope.launch {
                                        tooltipState.show()
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun <T> Flow<T>.distinctUntilChangedIgnoreInitializeValue(initializeValue: T): Flow<T> {
    var saveValue: T? = initializeValue
    return distinctUntilChanged()
        .filter {
            if (saveValue != null && it == saveValue) {
                saveValue = null
                return@filter false
            }
            true
        }
}
