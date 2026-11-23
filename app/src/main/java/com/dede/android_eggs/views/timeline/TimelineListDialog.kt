@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.timeline

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.dede.android_eggs.R
import com.dede.android_eggs.composable.ScrollableModalBottomSheet
import com.dede.android_eggs.icon_shape.IconShapePrefUtil
import com.dede.android_eggs.views.main.compose.EasterEggLogo
import com.dede.android_eggs.views.main.util.AndroidLogoMatcher
import com.dede.android_eggs.views.timeline.TimelineEventHelp.eventAnnotatedString
import com.dede.android_eggs.views.timeline.TimelineEventHelp.isNewGroup
import com.dede.android_eggs.views.timeline.TimelineEventHelp.localMonth
import com.dede.android_eggs.views.timeline.TimelineEventHelp.localYear
import com.dede.basic.provider.AndroidVersion
import com.dede.basic.provider.TimelineEvent
import com.dede.basic.requireDrawable
import com.dede.basic.withBackground
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

private const val TIMELINE_HORIZONTAL_BIAS = 0.3f

/** Horizontal center of the timeline rail in this box, at [TIMELINE_HORIZONTAL_BIAS] of the width. */
private val BoxWithConstraintsScope.timelineCenterX: Dp
    get() = maxWidth * TIMELINE_HORIZONTAL_BIAS

/** Places this element so that its horizontal center sits at [centerX]. */
private fun Modifier.centerAt(centerX: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(centerX.roundToPx() - placeable.width / 2, 0)
    }
}

/** Draws the 2dp vertical rail line, horizontally centered at [TIMELINE_HORIZONTAL_BIAS] of the width. */
private fun Modifier.drawTimelineRail(color: Color): Modifier = drawBehind {
    val lineWidth = 2.dp.toPx()
    drawRect(
        color = color,
        topLeft = Offset(size.width * TIMELINE_HORIZONTAL_BIAS - lineWidth / 2, 0f),
        size = Size(lineWidth, size.height),
    )
}

@Composable
fun TimelineListDialog(
    visible: Boolean = true,
    viewModel: TimelineViewModel = hiltViewModel(),
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    onDismiss: () -> Unit = {},
) {
    if (!visible) {
        return
    }
    val paddingValues = WindowInsets.safeDrawing.asPaddingValues()

    val lazyListState = rememberLazyListState()
    ScrollableModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = scrimColor,
        scrollState = lazyListState,
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val centerX = timelineCenterX
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
            ) {
                item {
                    TimelineHeader(centerX)
                }
                items(
                    items = viewModel.timelines,
                    key = { event ->
                        "${event.version.id}:${event.year}-${event.month}-${event.event}"
                    },
                    contentType = { "event" },
                ) {
                    TimelineItem(
                        event = it,
                        logoRes = viewModel.findAndroidLogoByVersion(it.version),
                        isNewGroup = it.isNewGroup(viewModel.timelines),
                        centerX = centerX,
                    )
                }
                item {
                    TimelineFooter(centerX)
                }
            }
        }
    }
}

@Composable
private fun TimelineFooter(centerX: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .centerAt(centerX)
                .size(2.dp)
                .background(colorScheme.secondary)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .centerAt(centerX)
                .size(8.dp)
                .background(colorScheme.secondary, CircleShape)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TimelineHeader(centerX: Dp = 120.dp) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .centerAt(centerX)
                .width(2.dp)
                .height(2.dp)
                .background(colorScheme.secondary, RoundedCornerShape(50, 50, 0, 0))
        )
        Icon(
            imageVector = Icons.Outlined.RocketLaunch,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .centerAt(centerX)
                .size(36.dp)
                .padding(6.dp)
                .rotate(-45f),
            tint = colorScheme.secondary
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TimelineItem(
    event: TimelineEvent = TimelineEvent(2025, Calendar.SEPTEMBER, AndroidVersion.CINNAMON_BUN, "Demo event name"),
    @DrawableRes logoRes: Int = R.mipmap.ic_launcher_17,
    isNewGroup: Boolean = true,
    centerX: Dp = 120.dp,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawTimelineRail(colorScheme.secondary)
    ) {
        val logoSize = 44
        val gap = 12.dp

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .width(centerX - logoSize.dp / 2 - gap)
                    .padding(top = 16.dp)
                    .height(logoSize.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (isNewGroup) {
                    Text(
                        text = event.localYear,
                        style = typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            Spacer(Modifier.width(gap))

            var imageModifier = Modifier
                .size(logoSize.dp)
            val withBackground = context.withBackground(logoRes)
            if (!withBackground) {
                imageModifier = Modifier
                    .background(colorScheme.secondaryContainer, IconShapePrefUtil.getIconShape())
                    .then(imageModifier)
                    .padding((logoSize * .15f).dp)
            }
            EasterEggLogo(
                res = logoRes,
                contentDescription = null,
                modifier = Modifier.padding(top = 16.dp).then(imageModifier),
            )

            Spacer(Modifier.width(gap))

            Column(Modifier.padding(top = 16.dp, end = 16.dp)) {
                Text(
                    text = event.localMonth,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                if (event.androidLogo != -1) {
                    val drawable = remember(LocalResources.current) {
                        context.requireDrawable(event.androidLogo)
                    }
                    Image(
                        painter = rememberDrawablePainter(drawable),
                        modifier = Modifier
                            .height(20.dp)
                            .padding(bottom = 3.dp),
                        contentDescription = null,
                    )
                }
                Text(
                    text = event.eventAnnotatedString,
                    style = typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
        }
    }
}

@HiltViewModel
class TimelineViewModel @Inject constructor(
    val timelines: List<@JvmSuppressWildcards TimelineEvent>,
    private val androidLogoMatcher: AndroidLogoMatcher,
) : ViewModel() {
    fun findAndroidLogoByVersion(version: AndroidVersion): Int {
        return androidLogoMatcher.findAndroidLogoByVersion(version)
    }
}
