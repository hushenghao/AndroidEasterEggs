package com.dede.android_eggs.ui.composes

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dede.android_eggs.R
import com.dede.android_eggs.util.ThemeUtils
import com.dede.basic.provider.SnapshotProvider
import kotlin.random.Random


private fun randomHash(context: Context): String {
    val strings = context.resources.getStringArray(R.array.hash_gallery)
    val index = Random.nextInt(strings.size)
    return strings[index]
}

const val PHI = 1.618034f

@Preview
@Composable
fun SnapshotView(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    snapshot: SnapshotProvider? = null,
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .clip(shape),
        contentAlignment = Alignment.Center,
    ) {
        if (snapshot == null || !snapshot.includeBackground) {
            val context = LocalContext.current
            val hash = remember(snapshot) { randomHash(context) }
            Image(
                bitmap = rememberBlurHashImageBitmap(hash),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                colorFilter = if (ThemeUtils.isDarkMode(context))
                    ColorFilter.lighting(Color.LightGray, Color.Transparent) else null,
                contentDescription = null,
            )
        }
        if (snapshot != null) {
            AndroidView(
                factory = {
                    snapshot.create(it)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (snapshot.includeBackground || !snapshot.insertPadding) 0.dp else 12.dp)
            )
        }
    }
}
