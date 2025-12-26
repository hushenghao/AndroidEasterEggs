package com.dede.android_eggs.ui.composes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.wolt.blurhashkt.BlurHashDecoder


@Composable
fun rememberBlurHashImageBitmap(
    hash: String,
    width: Int = 54,
    height: Int = 32
): ImageBitmap {
    return remember(hash, width, height) {
        checkNotNull(BlurHashDecoder.decode(hash, width, height)) {
            "BlurHash decode error! hash: ".format(hash)
        }.asImageBitmap()
    }
}
