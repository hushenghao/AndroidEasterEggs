@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.applyCanvas
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EasterEggHelp
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.util.ThemeUtils
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.wolt.blurhashkt.BlurHashDecoder
import kotlin.random.Random


private fun randomHash(context: Context): String {
    val strings = context.resources.getStringArray(R.array.hash_gallery)
    val index = Random.nextInt(strings.size)
    return strings[index]
}

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
        EasterEggHelp.DateFormatter.getInstance("MMM yyyy")
    }

    val snapshot = remember(egg) {
        egg.provideSnapshotProvider()
    }
    val hashBitmap = remember(egg, ThemeUtils.isSystemNightMode(context)) {
        var bitmap = BlurHashDecoder.decode(randomHash(context), 54, 32)// 5:3
        if (bitmap != null && ThemeUtils.isSystemNightMode(context)) {
            val nightMode =
                Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val matrix = ColorMatrix()
            matrix.setScale(0.8f, 0.8f, 0.8f, 0.8f)
            paint.colorFilter = ColorMatrixColorFilter(matrix)
            nightMode.applyCanvas {
                drawBitmap(bitmap!!, 0f, 0f, paint)
                setBitmap(null)
            }
            bitmap.recycle()
            bitmap = nightMode
        }
        bitmap
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .clipToBounds()
        ) {
            if (snapshot == null || !snapshot.includeBackground) {
                if (hashBitmap != null) {
                    Image(
                        bitmap = hashBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
            if (snapshot != null) {
                AndroidView(
                    factory = {
                        snapshot.create(it)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (snapshot.includeBackground) 0.dp else 12.dp)
                )
            }
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
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 14.dp, end = 18.dp)
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
                .padding(horizontal = 16.dp)
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