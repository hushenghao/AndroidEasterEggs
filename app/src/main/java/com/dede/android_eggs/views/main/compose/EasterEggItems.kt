@file:OptIn(ExperimentalLayoutApi::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.ui.composes.PHI
import com.dede.android_eggs.ui.composes.SnapshotView
import com.dede.android_eggs.ui.composes.icons.rounded.BookmarkAddOutline
import com.dede.android_eggs.views.main.util.AndroidReleaseDateMatcher
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EasterEggShortcutsHelp
import com.dede.android_eggs.views.main.util.EggActionHelp
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import com.dede.basic.utils.AppLocaleDateFormatter
import java.util.Date
import com.dede.android_eggs.resources.R as StringsR

@Composable
@Preview
fun EasterEggHighestItem(
    base: BaseEasterEgg = EasterEggHelp.previewEasterEggs().first()
) {
    val context = LocalContext.current
    val easterEggState = rememberEasterEggState(base)
    val egg = easterEggState.getEasterEgg()
    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(2.dp)),
        shape = shapes.extraLarge,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        onClick = {
            EggActionHelp.launchEgg(context, egg)
        }
    ) {
        val snapshot = remember(egg) {
            egg.provideSnapshotProvider()
        }
        SnapshotView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PHI),
            snapshot = snapshot
        )

        EasterEggItemContent(eggState = easterEggState)
    }
}

@Composable
@Preview
fun EasterEggSimpleItem(
    base: BaseEasterEgg = EasterEggHelp.previewEasterEggs().first(),
) {
    val context = LocalContext.current
    val easterEggState = rememberEasterEggState(base)
    val egg = easterEggState.getEasterEgg()
    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(2.dp)),
        shape = shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        onClick = {
            EggActionHelp.launchEgg(context, egg)
        },
    ) {
        EasterEggItemContent(eggState = easterEggState)
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

@Immutable
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
) {
    val context = LocalContext.current
    val egg = eggState.getEasterEgg()

    val androidVersion = remember(egg.fullApiLevelRange) {
        EasterEggHelp.VersionFormatter.create(egg.fullApiLevelRange, egg.nicknameRes)
            .format(context)
    }
    val apiLevel = remember(egg.fullApiLevelRange) {
        EasterEggHelp.ApiLevelFormatter.create(egg.fullApiLevelRange).format(context)
    }
    val dateFormat = remember(egg, LocalConfiguration.current) {
        AppLocaleDateFormatter.getInstance("MMM yyyy")
    }
    Column(modifier = modifier) {
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
                    .padding(horizontal = 18.dp)
                    .padding(top = 12.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Chip(text = apiLevel)
                val releaseDate = if (LocalInspectionMode.current) {
                    Date()
                } else {
                    AndroidReleaseDateMatcher.findReleaseDateByFullApiLevel(egg.fullApiLevel)
                }
                if (releaseDate != null) {
                    Chip(text = dateFormat.format(releaseDate))
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
