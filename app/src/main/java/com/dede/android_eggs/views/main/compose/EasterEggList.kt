package com.dede.android_eggs.views.main.compose

import android.content.Context
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.inject.EasterEggModules
import com.dede.android_eggs.util.compose.plus
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEgg.VERSION_CODES_FULL.toFullApiLevel
import com.dede.basic.provider.EasterEggGroup
import com.dede.basic.provider.toApiLevelRange


private const val HIGHEST_COUNT = 1

private fun BaseEasterEgg.lazyItemKey(): String {
    return when (this) {
        is EasterEggGroup -> "group:${fullApiLevelRange.first}-${fullApiLevelRange.last}"
        is EasterEgg -> "egg:${fullApiLevelRange.first}-${fullApiLevelRange.last}"
        else -> throw IllegalArgumentException("Unsupported EasterEgg type: ${this::class.java}")
    }
}

@Composable
@Preview(showBackground = true)
fun EasterEggList(
    modifier: Modifier = Modifier,
    easterEggs: List<BaseEasterEgg> = EasterEggHelp.previewEasterEggs(),
    searchText: String? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val context = LocalContext.current
    val pureEasterEggs = remember(easterEggs) {
        EasterEggModules.providePureEasterEggList(easterEggs)
    }
    val searchMode = !searchText.isNullOrBlank()
    val currentList = remember(searchText, searchMode, easterEggs, pureEasterEggs) {
        if (searchMode) {
            filterEasterEggs(context, pureEasterEggs, searchText)
        } else {
            easterEggs
        }
    }
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Crossfade(
            targetState = currentList.isEmpty(),
            modifier = Modifier.sizeIn(maxWidth = 560.dp),
            label = "EasterEggList",
        ) { isEmpty ->
            if (isEmpty) {
                SearchEmpty(contentPadding)
            } else {
                LazyColumn(
                    contentPadding = contentPadding + PaddingValues(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (searchMode) {
                        items(
                            items = currentList,
                            key = BaseEasterEgg::lazyItemKey,
                        ) {
                            EasterEggSimpleItem(it)
                        }
                    } else {
                        val highestList = currentList.subList(0, HIGHEST_COUNT)
                        val normalList = currentList.subList(HIGHEST_COUNT, currentList.size)
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
            }
        }
    }
}

@Composable
private fun SearchEmpty(contentPadding: PaddingValues) {
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

private fun filterEasterEggs(
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
