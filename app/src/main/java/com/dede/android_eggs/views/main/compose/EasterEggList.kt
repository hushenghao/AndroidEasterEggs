package com.dede.android_eggs.views.main.compose

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.composable.WindowWidthPane
import com.dede.android_eggs.composable.currentWindowWidthPane
import com.dede.android_eggs.inject.EasterEggModules
import com.dede.android_eggs.util.compose.plus
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup


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
        is EasterEgg -> "egg:${fullApiLevelRange.first}-${fullApiLevelRange.last}"
        is EasterEggGroup -> "group:${fullApiLevelRange.first}-${fullApiLevelRange.last}"
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
            PaddingValues(vertical = 10.dp, horizontal = 12.dp)
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
                )
            }
            EasterEggListType.GRID -> {
                EasterEggGrid(
                    easterEggs = easterEggs,
                    contentPadding = listContentPadding,
                )
            }
            EasterEggListType.SEARCH_RESULT -> {
                EasterEggSearchList(
                    easterEggs = searchResults,
                    contentPadding = listContentPadding,
                )
            }
            EasterEggListType.SEARCH_EMPTY -> SearchEmpty(contentPadding)
        }
    }
}

/** Single column list for compact widths, centered with the 560.dp limit. */
@Composable
private fun EasterEggColumnList(
    easterEggs: List<BaseEasterEgg>,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.animateContentSize(),
    ) {
        val (highestList, normalList) = easterEggs.highest(HIGHEST_COUNT)
        items(
            items = highestList,
            key = BaseEasterEgg::lazyItemKey,
        ) {
            EasterEggHighestItem(it)
        }
        item("wavy1") {
            ItemWavy()
        }
        items(
            items = normalList,
            key = BaseEasterEgg::lazyItemKey,
        ) {
            EasterEggSimpleItem(base = it)
        }
        item("wavy2") {
            ItemWavy()
        }
        item("footer") {
            ProjectDescription()
        }
    }
}

@Composable
private fun EasterEggGrid(
    easterEggs: List<BaseEasterEgg>,
    contentPadding: PaddingValues,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp,
        modifier = Modifier.fillMaxSize().animateContentSize(),
    ) {
        val (highestList, normalList) = easterEggs.highest(HIGHEST_COUNT)
        items(
            items = highestList,
            key = BaseEasterEgg::lazyItemKey,
        ) {
            EasterEggHighestItem(it)
        }
        items(
            items = normalList,
            key = BaseEasterEgg::lazyItemKey,
        ) {
            EasterEggSimpleItem(base = it)
        }
        item("wavy2", span = StaggeredGridItemSpan.FullLine) {
            ItemWavy()
        }
        item("footer", span = StaggeredGridItemSpan.FullLine) {
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
