@file:OptIn(ExperimentalLayoutApi::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.egg_stats.EggLaunchSource
import com.dede.android_eggs.egg_stats.EggStatsSummary
import com.dede.android_eggs.icon_shape.IconShapePrefUtil
import com.dede.android_eggs.ui.composes.PHI
import com.dede.android_eggs.ui.composes.SnapshotView
import com.dede.android_eggs.ui.composes.icons.rounded.BookmarkAddOutline
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp
import com.dede.android_eggs.views.main.util.EggActionHelp
import com.dede.basic.provider.AndroidVersion
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import com.dede.basic.utils.AppLocaleDateFormatter
import java.util.Date
import com.dede.android_eggs.resources.R as StringsR

@Composable
@Preview
fun EasterEggHighestItem(
    modifier: Modifier = Modifier,
    base: BaseEasterEgg = EasterEggHelp.previewEasterEggs().first(),
    releaseDateLookup: (AndroidVersion) -> Date? = { null },
    statsMap: Map<String, EggStatsSummary> = emptyMap(),
    launchSource: EggLaunchSource = EggLaunchSource.LIST,
) {
    val context = LocalContext.current
    val easterEggState = rememberEasterEggState(base)
    val egg = easterEggState.getEasterEgg()
    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(2.dp)),
        shape = shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            EggActionHelp.launchEgg(context, egg, launchSource)
        }
    ) {
        val snapshot = remember(egg) {
            egg.provideSnapshotProvider()
        }
        SnapshotView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PHI),
            shape = RoundedCornerShape(0f),
            snapshot = snapshot
        )

        EasterEggItemContent(
            eggState = easterEggState,
            releaseDateLookup = releaseDateLookup,
            statsMap = statsMap,
        )
    }
}

@Composable
@Preview
fun EasterEggSimpleItem(
    modifier: Modifier = Modifier,
    base: BaseEasterEgg = EasterEggHelp.previewEasterEggs().first(),
    releaseDateLookup: (AndroidVersion) -> Date? = { null },
    statsMap: Map<String, EggStatsSummary> = emptyMap(),
    launchSource: EggLaunchSource = EggLaunchSource.LIST,
) {
    val context = LocalContext.current
    val easterEggState = rememberEasterEggState(base)
    val egg = easterEggState.getEasterEgg()
    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(2.dp)),
        shape = shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            EggActionHelp.launchEgg(context, egg, launchSource)
        },
    ) {
        EasterEggItemContent(
            eggState = easterEggState,
            releaseDateLookup = releaseDateLookup,
            statsMap = statsMap,
        )
    }
}

@Composable
private fun Chip(text: String) {
    Card(
        shape = CircleShape
    ) {
        Text(
            text = text,
            style = typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

private class EasterEggState(val base: BaseEasterEgg, index: Int) {
    private val indexState: MutableState<Int> = mutableIntStateOf(index)

    val isGroup: Boolean get() = base is EasterEggGroup

    fun getEasterEgg(): EasterEgg {
        return when (base) {
            is EasterEgg -> base
            is EasterEggGroup -> base.eggs[indexState.value]
            else -> throw IllegalArgumentException("Unknown EasterEgg type: ${base::class.java.name}")
        }
    }

    fun selectedEgg(newIndex: Int) {
        if (base is EasterEggGroup) {
            val index = newIndex.coerceIn(0, base.eggs.size - 1)
            base.selectedIndex = index
            indexState.value = index
        }
    }
}

@Composable
private fun rememberEasterEggState(base: BaseEasterEgg): EasterEggState {
    return remember(base) {
        EasterEggState(base, if (base is EasterEggGroup) base.selectedIndex else 0)
    }
}

@Composable
private fun EasterEggItemContent(
    modifier: Modifier = Modifier,
    eggState: EasterEggState,
    releaseDateLookup: (AndroidVersion) -> Date?,
    statsMap: Map<String, EggStatsSummary>,
) {
    val context = LocalContext.current
    val egg = eggState.getEasterEgg()
    val stats = statsMap[egg.id]

    val androidVersion = remember(egg.versions) {
        EasterEggHelp.VersionFormatter.create(egg.versions, egg.nicknameRes)
            .format(context)
    }
    val apiLevel = remember(egg.versions) {
        EasterEggHelp.ApiLevelFormatter.create(egg.versions).format(context)
    }
    val dateFormat = remember(egg, LocalConfiguration.current) {
        AppLocaleDateFormatter.getInstance("MMM yyyy")
    }
    val cardColor = colorScheme.surfaceColorAtElevation(2.dp)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            EasterEggLogo(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(cardColor, Color.Transparent),
                            ),
                        )
                    }
                    .alpha(0.4f)
                    .blur(14.dp, 14.dp),
                res = egg.iconRes,
                contentDescription = null,
                iconShape = RectangleShape,
            )
        }
        Column {
            Row(
                modifier = Modifier
                    .padding(start = 18.dp, top = 14.dp, end = 18.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f, true)
                ) {
                    Text(
                        text = stringResource(id = egg.nameRes),
                        style = typography.headlineSmall,
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clip(shapes.extraSmall)
                            .withEasterEggGroupSelector(eggState.base) {
                                eggState.selectedEgg(it)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = androidVersion,
                            style = typography.bodyMedium,
                            // Weighted children are measured last, so the group
                            // arrow icon keeps its space when the text wraps.
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (eggState.isGroup) {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(22.dp),
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = stringResource(StringsR.string.pref_title_language_more)
                            )
                        }
                    }
                }
                EasterEggLogo(egg = egg, sensor = true)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FlowRow(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 18.dp, end = 0.dp, top = 12.dp, bottom = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Chip(text = apiLevel)
                    val releaseDate = if (LocalInspectionMode.current) {
                        Date()
                    } else {
                        releaseDateLookup(egg.primaryVersion)
                    }
                    if (releaseDate != null) {
                        Chip(text = dateFormat.format(releaseDate))
                    }
                    val playCount = stats?.playCount ?: 0
                    if (playCount > 0) {
                        Chip(
                            text = stringResource(
                                StringsR.string.egg_stats_played_count,
                                playCount,
                            )
                        )
                    }
                }

                val isSupportShortcut = remember(egg) {
                    EasterEggShortcutsHelp.isSupportShortcut(egg)
                }
                if (isSupportShortcut) {
                    IconButton(
                        onClick = {
                            EasterEggShortcutsHelp.pinShortcut(context, egg)
                        },
                        shape = IconShapePrefUtil.getIconShape(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkAddOutline,
                            contentDescription = stringResource(id = StringsR.string.label_add_shortcut)
                        )
                    }
                }
            }
        }
    }
}
