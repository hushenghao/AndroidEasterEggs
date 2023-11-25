package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dede.android_eggs.ui.views.SnapshotGroupView

@Composable
@Preview
fun AndroidSnapshotView() {
    val inspectionMode = LocalInspectionMode.current
    if (inspectionMode) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp)
        ) {
            Card(
                shape = shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {}
        }
    } else {
        // has inject
        AndroidView(
            factory = { SnapshotGroupView(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}
