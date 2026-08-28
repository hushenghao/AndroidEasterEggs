package com.dede.android_eggs.views.main.compose

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.composable.WindowWidthPane
import com.dede.android_eggs.composable.currentWindowWidthPane
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEgg.VERSION_CODES_FULL.toFullApiLevel
import com.dede.basic.provider.toApiLevelRange

/**
 * The searched egg list, a two-column grid on medium and expanded window widths,
 * single column on compact widths. The caller filters [easterEggs] and switches
 * to [SearchEmpty] separately when nothing matches.
 */
@Composable
@Preview(showBackground = true)
fun EasterEggSearchList(
    modifier: Modifier = Modifier,
    easterEggs: List<EasterEgg> = EasterEggHelp.previewEasterEggs(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val isCompact = currentWindowWidthPane() == WindowWidthPane.COMPACT
    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isCompact) 1 else 2),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .then(modifier)
            .animateContentSize()
            .fillMaxSize(),
    ) {
        items(
            items = easterEggs,
            key = BaseEasterEgg::lazyItemKey,
        ) {
            EasterEggSimpleItem(it)
        }
    }
}

@Composable
internal fun SearchEmpty(contentPadding: PaddingValues) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        DrawableImage(
            res = R.drawable.img_samples,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.6f)
        )
    }
}

internal fun filterEasterEggs(
    context: Context,
    pureEasterEggs: List<EasterEgg>,
    searchText: String,
): List<EasterEgg> {

    fun EasterEgg.matchStringResNames(searchText: String): Boolean {
        return context.getString(nameRes).contains(searchText, true) ||
                context.getString(nicknameRes).contains(searchText, true)
    }

    fun EasterEgg.matchApiLevel(searchText: String): Boolean {
        val isApiLevel = Regex("^\\d{1,2}$").matches(searchText)
        if (isApiLevel) {
            val apiLevel = searchText.toIntOrNull() ?: return false
            return fullApiLevelRange.contains(apiLevel.toFullApiLevel())
        }
        return false
    }

    fun EasterEgg.matchAndroidVersion(searchText: String): Boolean {
        val versionNameResult = Regex("[\\d.]{1,3}").find(searchText) ?: return false
        val versionNameValue = versionNameResult.value
        val versionNames = buildSet {
            add(EasterEggHelp.getVersionNameByFullApiLevel(fullApiLevelRange.first))
            add(EasterEggHelp.getVersionNameByFullApiLevel(fullApiLevelRange.last))
            for (apiLevel in fullApiLevelRange.toApiLevelRange()) {
                add(EasterEggHelp.getVersionNameByApiLevel(apiLevel))
            }
        }
        for (versionName in versionNames) {
            if (versionName.contains(versionNameValue, true)) {
                return true
            }
        }
        return false
    }

    val trim = searchText.trim()
    return pureEasterEggs.filter {
        it.matchStringResNames(trim) ||
                it.matchApiLevel(trim) ||
                it.matchAndroidVersion(trim)
    }
}
