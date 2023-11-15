@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.dede.android_eggs.views.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.drawable.toBitmap
import com.dede.android_eggs.main.entity.TimelineEvent
import com.dede.basic.requireDrawable

@Preview
@Composable
fun AndroidTimelineSheet(visible: MutableState<Boolean>) {
    var show by visible
    if (!show) return
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        windowInsets = BottomSheetDefaults.windowInsets.exclude(WindowInsets.systemBars),
        sheetState = sheetState,
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
    LazyColumn {
        itemsIndexed(items = items) { index, item ->
            AndroidTimelineItem(item) {
                if (index <= 0) return@AndroidTimelineItem true
                return@AndroidTimelineItem items[index - 1].year != it.year
            }
        }
    }
}

@Composable
fun AndroidTimelineItem(
    timeline: TimelineEvent,
    isNewYearGroup: (current: TimelineEvent) -> Boolean,
) {
    val context = LocalContext.current
    val bitmap = remember(context.theme) {
        context.requireDrawable(timeline.logoRes)
            .toBitmap().asImageBitmap()
    }
    val isNewGroup = remember(timeline) { isNewYearGroup.invoke(timeline) }

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
            bitmap = bitmap, contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(logo) {
                    linkTo(start = parent.start, end = parent.end, bias = 0.3f)
                    linkTo(top = card.top, bottom = card.bottom)
                }
        )
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
        Card(
            backgroundColor = colorScheme.secondaryContainer,
            shape = shapes.medium,
            modifier = Modifier
                .constrainAs(card) {
                    width = Dimension.fillToConstraints
                    end.linkTo(parent.end, margin = 10.dp)
                    start.linkTo(logo.end, margin = 10.dp)
                    top.linkTo(year.bottom)
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                }
        ) {
            Text(
                text = timeline.eventSpan.toString(),
                style = typography.labelMedium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
        Icon(
            imageVector = Icons.Outlined.ArrowLeft,
            tint = colorScheme.secondaryContainer,
            modifier = Modifier
                .size(24.dp)
                .constrainAs(arrow) {
                    linkTo(top = logo.top, bottom = logo.bottom)
                    start.linkTo(card.start, margin = (-14).dp)
                },
            contentDescription = null
        )
    }
}