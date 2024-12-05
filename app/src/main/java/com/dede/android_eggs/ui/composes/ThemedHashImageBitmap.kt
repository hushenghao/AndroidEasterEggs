package com.dede.android_eggs.ui.composes

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.applyCanvas
import com.dede.android_eggs.util.ThemeUtils
import com.wolt.blurhashkt.BlurHashDecoder


@Composable
fun rememberThemedHashImageBitmap(
    hash: String,
    width: Int = 54,
    height: Int = 32
): ImageBitmap {
    val context = LocalContext.current
    return remember(hash, ThemeUtils.isDarkMode(context.resources)) {
        var bitmap = checkNotNull(BlurHashDecoder.decode(hash, width, height)) {
            "BlurHash decode error! hash: ".format(hash)
        }
        if (ThemeUtils.isDarkMode(context.resources)) {
            val nightMode =
                Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val matrix = ColorMatrix()
            matrix.setScale(0.8f, 0.8f, 0.8f, 0.8f)
            paint.colorFilter = ColorMatrixColorFilter(matrix)
            nightMode.applyCanvas {
                drawBitmap(bitmap, 0f, 0f, paint)
                setBitmap(null)
            }
            bitmap.recycle()
            bitmap = nightMode
        }
        bitmap.asImageBitmap()
    }
}