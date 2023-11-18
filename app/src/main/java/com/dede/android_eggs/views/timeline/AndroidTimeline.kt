@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class
)

package com.dede.android_eggs.views.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dede.android_eggs.main.entity.TimelineEvent
import com.dede.android_eggs.views.main.compose.Image

@Preview
@Composable
fun AndroidTimelineSheet(visible: MutableState<Boolean> = mutableStateOf(true)) {
    var show by visible
    if (!show) return

    val top = with(LocalDensity.current) {
        WindowInsets.systemBars.getTop(this).toDp()
    }

    ModalBottomSheet(
        windowInsets = MutableWindowInsets(),
        modifier = Modifier
            .offset(y = -top)
            .padding(top = top),
        onDismissRequest = {
            show = false
        },
    ) {
        AndroidTimeline()
    }
}

@Composable
@Preview(showBackground = true)
fun AndroidTimeline(items: List<TimelineEvent> = TimelineEvent.timelines) {
    val bottom = with(LocalDensity.current) {
        WindowInsets.systemBars.getBottom(this).toDp()
    }
    LazyColumn(
        contentPadding = PaddingValues(bottom = bottom),
    ) {
        itemsIndexed(items = items) { index, item ->
            AndroidTimelineItem(item, {
                return@AndroidTimelineItem index >= items.size - 1
            }) {
                if (index <= 0) return@AndroidTimelineItem true
                return@AndroidTimelineItem items[index - 1].year != it.year
            }
        }
    }
}

@Composable
fun AndroidTimelineItem(
    timeline: TimelineEvent,
    isLast: (current: TimelineEvent) -> Boolean,
    isNewYearGroup: (current: TimelineEvent) -> Boolean,
) {
    val isNewGroup = remember(timeline) { isNewYearGroup.invoke(timeline) }
    val isLast = remember(timeline) { isLast.invoke(timeline) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (logo, card, year, month, arrow, l1, l2) = createRefs()
        if (isNewGroup) {
            Text(
                text = timeline.localYear.toString(),
                style = typography.titleLarge,
                modifier = Modifier.constrainAs(year) {
                    end.linkTo(logo.start)
                    start.linkTo(parent.start)
                    top.linkTo(parent.top, margin = 10.dp)
                }
            )
        }
        Text(
            text = timeline.localMonth.toString(),
            style = typography.labelMedium,
            modifier = Modifier.constrainAs(month) {
                end.linkTo(logo.start, margin = 10.dp)
                linkTo(top = logo.top, bottom = logo.bottom)
            }
        )
        Box(
            modifier = Modifier
                .width(2.dp)
                .clip(RoundedCornerShape(bottomEndPercent = 50, bottomStartPercent = 50))
                .background(colorScheme.primary)
                .constrainAs(l1) {
                    height = Dimension.fillToConstraints
                    linkTo(start = logo.start, end = logo.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(logo.top, 4.dp)
                }
        ) {}
        Image(
            res = timeline.logoRes, contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(logo) {
                    linkTo(start = parent.start, end = parent.end, bias = 0.3f)
                    linkTo(top = card.top, bottom = card.bottom)
                }
        )
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .clip(RoundedCornerShape(topEndPercent = 50, topStartPercent = 50))
                    .background(colorScheme.primary)
                    .constrainAs(l2) {
                        height = Dimension.fillToConstraints
                        linkTo(start = logo.start, end = logo.end)
                        top.linkTo(logo.bottom, 4.dp)
                        bottom.linkTo(parent.bottom)
                    }
            ) {}
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = shapes.medium,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .constrainAs(card) {
                    width = Dimension.preferredWrapContent
                    linkTo(start = logo.end, end = parent.end, bias = 0f)
                    top.linkTo(year.bottom)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                }
        ) {
            Text(
                text = buildAnnotatedString {
                    val split = timeline.event.split("\n")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(split[0])
                    }
                    if (split.size > 1) {
                        append("\n")
                        append(split[1])
                    }
                },
                style = typography.labelMedium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
        // todo fix leak https://issuetracker.google.com/issues/311627066
        Icon(
            imageVector = Icons.Outlined.ArrowLeft,
            tint = colorScheme.secondaryContainer,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(arrow) {
                    linkTo(top = logo.top, bottom = logo.bottom)
                    start.linkTo(card.start, margin = (-3.5).dp)
                },
            contentDescription = null
        )
    }
}