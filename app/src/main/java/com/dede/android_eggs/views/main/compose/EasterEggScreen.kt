package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EasterEggHelp
import com.dede.basic.provider.BaseEasterEgg

@Composable
@Preview
fun EasterEggScreen(
    easterEggs: List<BaseEasterEgg> = EasterEggHelp.previewEasterEggs(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val inspectionMode = LocalInspectionMode.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier.sizeIn(maxWidth = 560.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!inspectionMode) {
                item("snapshot") {
                    AndroidSnapshotView()
                }
            }
            item("wavy1") {
                Wavy(res = R.drawable.ic_wavy_line)
            }
            items(items = easterEggs) {
                EasterEggItem(it)
            }
            item("wavy2") {
                Wavy(res = R.drawable.ic_wavy_line)
            }
            item("footer") {
                ProjectDescription()
            }
        }
    }
}