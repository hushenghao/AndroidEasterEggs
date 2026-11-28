package com.dede.android_eggs.views.main.compose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.composable.WindowWidthPane
import com.dede.android_eggs.composable.currentWindowWidthPane
import com.dede.android_eggs.egg_stats.EggStatsSummary
import com.dede.android_eggs.inject.EasterEggModules
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.util.compose.plus
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.basic.provider.AndroidVersion
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import java.util.Date


private const val HIGHEST_COUNT = 1

/**
 * The content type of the egg list, drives the Crossfade between the browsing
 * layouts and the search result/empty states.
 */
private enum class EasterEggListType {
    /** Single-column browsing list on compact windows. */
    COLUMN_LIST,

    /** Two-column browsing grid on medium and expanded windows. */
    GRID,

    /** Non-empty search results. */
    SEARCH_RESULT,

    /** Nothing matches the search text. */
    SEARCH_EMPTY,
}

private fun <T> List<T>.highest(count: Int): Pair<List<T>, List<T>> {
    if (size <= count) return this to emptyList()

    val highestList = subList(0, count)
    val normalList = subList(count, size)
    return highestList to normalList
}

internal fun BaseEasterEgg.lazyItemKey(): String {
    return when (this) {
        is EasterEgg -> "egg:$id"
        is EasterEggGroup -> "group:$id"
        else -> throw IllegalArgumentException("Unsupported EasterEgg type: ${this::class.java}")
    }
}

/**
 * The egg list entry: the searched results, or the browsing list grouped by the highest
 * egg, wavy dividers and the footer. The browsing list is a two-column grid on
 * medium and expanded window widths, single column on compact widths. A single
 * Crossfade switches between the browsing layouts and the search result/empty states.
 */
@Composable
@Preview(showBackground = true)
fun EasterEggList(
    modifier: Modifier = Modifier,
    easterEggs: List<BaseEasterEgg> = EasterEggHelp.previewEasterEggs(),
    searchText: String = "",
    contentPadding: PaddingValues = PaddingValues(0.dp),
    releaseDateLookup: (AndroidVersion) -> Date? = { null },
    highestCount: Int = HIGHEST_COUNT,
    searchBarVisible: Boolean = false,
    statsMap: Map<String, EggStatsSummary> = emptyMap(),
) {
    val context = LocalContext.current
    val pureEasterEggs = remember(easterEggs) {
        EasterEggModules.providePureEasterEggList(easterEggs)
    }
    val searchResults = remember(searchText, pureEasterEggs) {
        filterEasterEggs(context, pureEasterEggs, searchText)
    }
    val type = when {
        searchText.isNotBlank() -> {
            if (searchResults.isEmpty()) EasterEggListType.SEARCH_EMPTY else EasterEggListType.SEARCH_RESULT
        }
        currentWindowWidthPane() != WindowWidthPane.COMPACT -> EasterEggListType.GRID
        else -> EasterEggListType.COLUMN_LIST
    }

    val listContentPadding = contentPadding +
            PaddingValues(
                start = 12.dp,
                top = 10.dp,
                end = 12.dp,
                bottom = 10.dp +
                        (if (searchBarVisible) BottomSearchBarClearance else 0.dp) +
                        rememberExtraImeBottomPadding(),
            )

    // `HIGHEST_EGG_COUNT_ALL` highlights every egg; the other values are literal card counts.
    val effectiveHighestCount = if (highestCount == AppSettings.HIGHEST_EGG_COUNT_ALL) {
        easterEggs.size
    } else {
        highestCount
    }
    Crossfade(
        modifier = Modifier
            .then(modifier)
            .fillMaxSize(),
        targetState = type,
        label = "EasterEggList",
    ) { currentType ->
        when (currentType) {
            EasterEggListType.COLUMN_LIST -> {
                EasterEggColumnList(
                    easterEggs = easterEggs,
                    contentPadding = listContentPadding,
                    releaseDateLookup = releaseDateLookup,
                    statsMap = statsMap,
                    highestCount = effectiveHighestCount,
                )
            }
            EasterEggListType.GRID -> {
                EasterEggGrid(
                    easterEggs = easterEggs,
                    contentPadding = listContentPadding,
                    releaseDateLookup = releaseDateLookup,
                    statsMap = statsMap,
                    highestCount = effectiveHighestCount,
                )
            }
            EasterEggListType.SEARCH_RESULT -> {
                EasterEggSearchList(
                    easterEggs = searchResults,
                    contentPadding = listContentPadding,
                    releaseDateLookup = releaseDateLookup,
                    statsMap = statsMap,
                )
            }
            EasterEggListType.SEARCH_EMPTY -> SearchEmpty(listContentPadding)
        }
    }
}

@Composable
private fun rememberExtraImeBottomPadding(): Dp {
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val navBottom = WindowInsets.navigationBars.getBottom(density)
    return with(density) { (imeBottom - navBottom).coerceAtLeast(0).toDp() }
}

/** Single column list for compact widths, centered with the 560.dp limit. */
@Composable
private fun EasterEggColumnList(
    easterEggs: List<BaseEasterEgg>,
    contentPadding: PaddingValues,
    releaseDateLookup: (AndroidVersion) -> Date?,
    statsMap: Map<String, EggStatsSummary>,
    highestCount: Int,
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        val (highestList, normalList) = easterEggs.highest(highestCount)
        items(
            items = highestList,
            key = BaseEasterEgg::lazyItemKey,
            contentType = { "highest" },
        ) {
            EasterEggHighestItem(
                base = it,
                modifier = Modifier.animateItem(),
                releaseDateLookup = releaseDateLookup,
                statsMap = statsMap,
            )
        }
        // Separate the highlighted and the normal sections only when both exist.
        if (highestList.isNotEmpty() && normalList.isNotEmpty()) {
            item("wavy1", contentType = "wavy") {
                ItemWavy()
            }
        }
        items(
            items = normalList,
            key = BaseEasterEgg::lazyItemKey,
            contentType = { "simple" },
        ) {
            EasterEggSimpleItem(
                base = it,
                modifier = Modifier.animateItem(),
                releaseDateLookup = releaseDateLookup,
                statsMap = statsMap,
            )
        }
        item("wavy2", contentType = "wavy") {
            ItemWavy()
        }
        item("footer", contentType = "footer") {
            ProjectDescription()
        }
    }
}

@Composable
private fun EasterEggGrid(
    easterEggs: List<BaseEasterEgg>,
    contentPadding: PaddingValues,
    releaseDateLookup: (AndroidVersion) -> Date?,
    statsMap: Map<String, EggStatsSummary>,
    highestCount: Int,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp,
        modifier = Modifier.fillMaxSize(),
    ) {
        val (highestList, normalList) = easterEggs.highest(highestCount)
        items(
            items = highestList,
            key = BaseEasterEgg::lazyItemKey,
            contentType = { "highest" },
        ) {
            EasterEggHighestItem(
                base = it,
                modifier = Modifier.animateItem(),
                releaseDateLookup = releaseDateLookup,
                statsMap = statsMap,
            )
        }
        items(
            items = normalList,
            key = BaseEasterEgg::lazyItemKey,
            contentType = { "simple" },
        ) {
            EasterEggSimpleItem(
                base = it,
                modifier = Modifier.animateItem(),
                releaseDateLookup = releaseDateLookup,
                statsMap = statsMap,
            )
        }
        item("wavy2", span = StaggeredGridItemSpan.FullLine, contentType = "wavy") {
            ItemWavy()
        }
        item("footer", span = StaggeredGridItemSpan.FullLine, contentType = "footer") {
            ProjectDescription()
        }
    }
}

@Composable
private fun ItemWavy() {
    Wavy(
        modifier = Modifier
            .sizeIn(maxWidth = 420.dp)
            .fillMaxWidth(0.4f)
            .padding(vertical = 26.dp),
    )
}
