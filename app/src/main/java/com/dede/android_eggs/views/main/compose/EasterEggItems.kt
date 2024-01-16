@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android_u.egg.AndroidUEasterEgg
import com.dede.android_eggs.R
import com.dede.basic.provider.EasterEgg

@Composable
private fun Chip(text: String) {
    Card {
        Text(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            text = text,
            style = typography.labelMedium
        )
    }
}

@Composable
@Preview
fun EasterEggHighestItem() {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceColorAtElevation(2.dp)),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(colorScheme.surfaceVariant)
                .fillMaxWidth()
                .aspectRatio(1.6f)
        ) {
            AndroidView(
                factory = {
                    val easterEgg = AndroidUEasterEgg.provideEasterEgg() as EasterEgg
                    easterEgg.provideSnapshotProvider()!!.create(it)
                }, modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
            )
        }
        Text(
            text = "Android 14 Easter Egg",
            style = typography.headlineSmall,
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp),
        )
        Text(
            text = "Android 14 (Upside Down Cake) API 34",
            style = typography.bodyMedium,
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 16.dp)
                .padding(top = 6.dp),
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Chip(text = "Accessibility")
            Chip(text = "Camera and media")
            Chip(text = "Graphics")
            Chip(text = "Internationalization")
        }
    }
}