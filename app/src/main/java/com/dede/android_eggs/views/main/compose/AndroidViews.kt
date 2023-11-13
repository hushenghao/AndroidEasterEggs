package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dede.android_eggs.ui.views.SnapshotGroupView

@Composable
fun AndroidSnapshotView() {
    AndroidView(
        factory = { SnapshotGroupView(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}
