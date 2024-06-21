@file:OptIn(ExperimentalLayoutApi::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EggActionHelp
import com.dede.android_eggs.ui.composes.SnapshotView
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg

@Composable
@Preview
fun EasterEggHighestItem(
    base: BaseEasterEgg = EasterEggHelp.previewEasterEggs().first()
) {
    val context = LocalContext.current
    val egg = base as EasterEgg
    val androidVersion = remember(egg) {
        EasterEggHelp.VersionFormatter.create(egg.apiLevel, egg.nicknameRes)
            .format(context)
    }
    val apiLevel = remember(egg) {
        EasterEggHelp.ApiLevelFormatter.create(egg.apiLevel).format(context)
    }
    val dateFormat = remember(egg, context.resources.configuration) {
        EasterEggHelp.DateFormatter.getInstance("MM yyyy")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(2.dp)),
        shape = shapes.extraLarge,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(shapes.extraLarge)
            .clickable {
                EggActionHelp.launchEgg(context, egg)
            }
    ) {
        Box {
            val isSupportShortcut = remember(egg) {
                EggActionHelp.isSupportShortcut(egg)
            }
            val snapshot = remember(egg) {
                egg.provideSnapshotProvider()
            }

            SnapshotView(snapshot)

            if (isSupportShortcut) {
                IconButton(
                    onClick = {
                        EggActionHelp.addShortcut(context, egg)
                    },
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BookmarkBorder,
                        tint = colorScheme.onPrimary,
                        contentDescription = stringResource(id = R.string.label_add_shortcut)
                    )
                }
            }
        }
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
                Text(
                    text = androidVersion,
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            EasterEggLogo(egg = egg, sensor = true)
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 12.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Chip(text = apiLevel)
            Chip(text = dateFormat.format(egg.getReleaseDate()))
        }
    }
}

@Composable
private fun Chip(text: String) {
    Card(
        shape = shapes.large
    ) {
        Text(
            text = text,
            style = typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}