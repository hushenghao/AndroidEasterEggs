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

private val WHITESPACE_REGEX = Regex("\\s+")

/** The first digits-and-dots run of a search token, e.g. "4.4" from "4.4W". */
private val VERSION_VALUE_REGEX = Regex("[\\d.]{1,3}")

/**
 * One whitespace-separated word of the search text, with the numeric forms
 * resolved once so that matching an egg is only plain string comparisons.
 */
private class SearchToken(val text: String) {

    /** The api level when this token is a one or two digit number, otherwise null. */
    val apiLevel: Int? = if (text.length <= 2 && text.all { it.isDigit() }) {
        text.toIntOrNull()
    } else {
        null
    }

    val versionValue: String? = VERSION_VALUE_REGEX.find(text)?.value
}

/**
 * Every whitespace-separated token must match the egg: the token contained in
 * the name or the nickname, or resolved to the egg's api level or version name.
 */
internal fun filterEasterEggs(
    context: Context,
    pureEasterEggs: List<EasterEgg>,
    searchText: String,
): List<EasterEgg> {

    fun EasterEgg.versionNames(): Set<String> {
        val range = fullApiLevelRange
        return buildSet {
            add(EasterEggHelp.getVersionNameByFullApiLevel(range.first))
            add(EasterEggHelp.getVersionNameByFullApiLevel(range.last))
            for (apiLevel in range.toApiLevelRange()) {
                add(EasterEggHelp.getVersionNameByApiLevel(apiLevel))
            }
        }
    }

    fun EasterEgg.matches(token: SearchToken): Boolean {
        if (context.getString(nameRes).contains(token.text, true) ||
            context.getString(nicknameRes).contains(token.text, true)
        ) {
            return true
        }
        val apiLevelRange = fullApiLevelRange.toApiLevelRange()
        val apiLevel = token.apiLevel
        if (apiLevel != null && apiLevelRange.contains(apiLevel)) {
            return true
        }
        val versionValue = token.versionValue ?: return false
        return versionNames().any { it.contains(versionValue, true) }
    }

    val tokens = searchText.trim()
        .split(WHITESPACE_REGEX)
        .filter { it.isNotEmpty() }
        .map(::SearchToken)
    return pureEasterEggs.filter { egg ->
        tokens.all { token -> egg.matches(token) }
    }
}
