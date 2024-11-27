package com.dede.android_eggs.ui.composes

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun rememberLoopPagerState(
    initialPage: Int = 0,
    @FloatRange(from = -0.5, to = 0.5) initialPageOffsetFraction: Float = 0f,
    pageCount: () -> Int
): LoopPagerState {
    val pageCountDelegate = {
        var count = pageCount()
        if (count > 0) {
            count += 2
        }
        count
    }
    return rememberSaveable(saver = LoopPagerState.Saver) {
        LoopPagerState(
            initialPage + 1,
            initialPageOffsetFraction,
            pageCountDelegate
        )
    }.apply {
        pageCountState.value = pageCountDelegate
    }
}

class LoopPagerState internal constructor(
    currentPage: Int,
    currentPageOffsetFraction: Float,
    updatedPageCount: () -> Int
) : PagerState(currentPage, currentPageOffsetFraction) {

    var pageCountState = mutableStateOf(updatedPageCount)

    override val pageCount: Int get() = pageCountState.value.invoke()

    val realPageCount: Int get() = if (pageCount == 0) 0 else pageCount - 2

    val fixPageCount: Int get() = fixPageIndex(currentPage)

    internal fun fixPageIndex(index: Int): Int {
        // src: [0, 1, 2, 3, 4]
        // dst: [4, 0, 1, 2, 3, 4, 0]
        val pageCount = pageCount
        return when (index) {
            0 -> pageCount - 1 - 2
            pageCount - 1 -> 0
            else -> index - 1
        }
    }

    companion object {
        /**
         * To keep current page and current page offset saved
         */
        val Saver: Saver<LoopPagerState, *> = listSaver(
            save = {
                listOf(
                    it.currentPage,
                    (it.currentPageOffsetFraction).coerceIn(-0.5f, 0.5f),
                    it.pageCount
                )
            },
            restore = {
                LoopPagerState(
                    currentPage = it[0] as Int,
                    currentPageOffsetFraction = it[1] as Float,
                    updatedPageCount = { it[2] as Int }
                )
            }
        )
    }
}

@Composable
fun LoopHorizontalPager(
    state: LoopPagerState,
    autoLoop: Boolean = true,
    interval: Long = 2000L,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSize: PageSize = PageSize.Fill,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pageSpacing: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: TargetedFlingBehavior = PagerDefaults.flingBehavior(state = state),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null,
    pageNestedScrollConnection: NestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
        state, Orientation.Horizontal
    ),
    snapPosition: SnapPosition = SnapPosition.Start,
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {

    if (state.pageCount > 0) {
        val isDragged by state.interactionSource.collectIsDraggedAsState()

        LaunchedEffect(state.settledPage, isDragged, autoLoop) {
            val currentPage = state.currentPage
            if (currentPage == state.pageCount - 1) {// on end
                state.requestScrollToPage(1)
            } else if (currentPage == 0) {// on start
                state.requestScrollToPage(state.pageCount - 2)
            }

            if (!isDragged && autoLoop) {
                delay(interval)
                state.animateScrollToPage((state.currentPage + 1) % state.pageCount)
            }
        }
    }

    HorizontalPager(
        state = state,
        modifier = modifier,
        contentPadding = contentPadding,
        pageSize = pageSize,
        beyondViewportPageCount = beyondViewportPageCount,
        pageSpacing = pageSpacing,
        verticalAlignment = verticalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        key = if (key == null) null else { index ->
            key(state.fixPageIndex(index))
        },
        pageNestedScrollConnection = pageNestedScrollConnection,
        snapPosition = snapPosition,
    ) {
        pageContent(state.fixPageIndex(it))
    }
}

@Composable
fun LoopPagerIndicator(
    state: LoopPagerState,
    modifier: Modifier = Modifier,
    indicatorSpace: Dp = 6.dp,
    indicatorModifier: Modifier = Modifier
        .size(6.dp)
        .clip(RoundedCornerShape(50f)),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(indicatorSpace),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 0..<state.realPageCount) {
            Box(
                modifier = indicatorModifier
                    .then(
                        Modifier.background(
                            if (i == state.fixPageCount) colorScheme.primary else colorScheme.primaryContainer
                        )
                    )

            )
        }
    }
}
