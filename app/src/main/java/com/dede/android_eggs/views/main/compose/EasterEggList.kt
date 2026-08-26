package com.dede.android_eggs.views.main.compose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.composable.WindowWidthPane
import com.dede.android_eggs.composable.currentWindowWidthPane
import com.dede.android_eggs.util.compose.plus
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup


private const val HIGHEST_COUNT = 1

internal fun BaseEasterEgg.lazyItemKey(): String {
    return when (this) {
        is EasterEgg -> "egg:${fullApiLevelRange.first}-${fullApiLevelRange.last}"
        is EasterEggGroup -> "group:${fullApiLevelRange.first}-${fullApiLevelRange.last}"
        else -> throw IllegalArgumentException("Unsupported EasterEgg type: ${this::class.java}")
    }
}

/**
 * The egg list entry: the searched results, or the browsing list grouped by the highest
 * egg, wavy dividers and the footer. The browsing list is a two-column staggered grid on
 * medium and expanded window widths, single column with the 560.dp centered limit on
 * compact widths.
 */
@Composable
@Preview(showBackground = true)
fun EasterEggList(
    modifier: Modifier = Modifier,
    easterEggs: List<BaseEasterEgg> = EasterEggHelp.previewEasterEggs(),
    searchText: String = "",
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val listContentPadding = contentPadding +
            PaddingValues(vertical = 10.dp, horizontal = 12.dp)
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Crossfade(
            targetState = searchText.isNotBlank(),
            label = "EasterEggList",
        ) { isSearchMode ->
            if (isSearchMode) {
                EasterEggSearchList(
                    easterEggs = easterEggs,
                    searchText = searchText,
                    contentPadding = listContentPadding,
                )
            } else {
                if (currentWindowWidthPane() != WindowWidthPane.COMPACT) {
                    EasterEggGrid(
                        easterEggs = easterEggs,
                        contentPadding = listContentPadding,
                    )
                } else {
                    EasterEggColumnList(
                        easterEggs = easterEggs,
                        contentPadding = listContentPadding,
                    )
                }
            }
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
        modifier = Modifier.sizeIn(maxWidth = 560.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val highestList = easterEggs.subList(0, HIGHEST_COUNT)
        val normalList = easterEggs.subList(HIGHEST_COUNT, easterEggs.size)
        items(
            items = highestList,
            key = BaseEasterEgg::lazyItemKey,
        ) {
            EasterEggHighestItem(it)
        }
        item("wavy1") {
            Wavy(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(vertical = 26.dp),
            )
        }
        items(
            items = normalList,
            key = BaseEasterEgg::lazyItemKey,
        ) {
            EasterEggSimpleItem(base = it)
        }
        item("wavy2") {
            Wavy(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(vertical = 26.dp),
            )
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
        modifier = Modifier.fillMaxSize(),
    ) {
        val highestList = easterEggs.subList(0, HIGHEST_COUNT)
        val normalList = easterEggs.subList(HIGHEST_COUNT, easterEggs.size)
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
            Wavy(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(vertical = 26.dp),
            )
        }
        item("footer", span = StaggeredGridItemSpan.FullLine) {
            ProjectDescription()
        }
    }
}
