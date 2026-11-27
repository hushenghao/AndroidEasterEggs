package com.dede.android_eggs.views.main.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.dede.android_eggs.system_colors.R as SystemColorsR
import com.dede.basic.requireDrawable
import kotlin.math.min
import kotlin.math.roundToInt

// 72px with a 3-pass radius-4 box blur (sigma ≈ 4.47px, ~6.2% of height) matches
// a 14dp Gaussian blur on a ~134dp card while keeping each cached bitmap ~20 KB.
private const val BACKDROP_BITMAP_SIZE = 72
private const val BLUR_RADIUS = 4
private const val BLUR_PASSES = 3
private const val ADAPTIVE_ICON_INSET_FACTOR = 0.25f
private const val BACKDROP_MAX_ALPHA = 0.4f
private const val MAX_CACHE_ENTRIES = 32

@Composable
internal fun EasterEggLogoBackdrop(
    @DrawableRes res: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val systemAccentSample = ContextCompat.getColor(
        context,
        SystemColorsR.color.system_accent3_500,
    )
    val bitmap = remember(res, configuration.uiMode, isRtl, systemAccentSample) {
        EasterEggBackdropBitmapCache.getOrCreate(
            context = context,
            key = BackdropCacheKey(
                res = res,
                uiMode = configuration.uiMode,
                isRtl = isRtl,
                systemAccentSample = systemAccentSample,
            ),
        )
    }
    Image(
        bitmap = bitmap,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier,
    )
}

internal data class BackdropCacheKey(
    @DrawableRes val res: Int,
    val uiMode: Int,
    val isRtl: Boolean,
    val systemAccentSample: Int,
)

internal object EasterEggBackdropBitmapCache {

    private val cache = LruCache<BackdropCacheKey, ImageBitmap>(MAX_CACHE_ENTRIES)

    fun getOrCreate(context: Context, key: BackdropCacheKey): ImageBitmap {
        val cached = cache.get(key)
        if (cached != null) {
            return cached
        }
        val created = createBlurredBackdropBitmap(
            context = context,
            res = key.res,
            isRtl = key.isRtl,
        )
        cache.put(key, created)
        return created
    }

    fun clear() {
        cache.evictAll()
    }
}

/**
 * Rasterizes the egg icon into a small square [Bitmap], blurs it in premultiplied
 * ARGB space, and bakes in the horizontal fade (`0f` on the inner card edge to
 * [BACKDROP_MAX_ALPHA] on the outer edge) so cards draw a plain static bitmap
 * without offscreen `RenderEffect` or alpha layers.
 */
private fun createBlurredBackdropBitmap(
    context: Context,
    @DrawableRes res: Int,
    isRtl: Boolean,
): ImageBitmap {
    val size = BACKDROP_BITMAP_SIZE
    val bitmap = createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawEggIconToSquareCanvas(context, res, size, canvas)

    val pixelCount = size * size
    val pixels = IntArray(pixelCount)
    bitmap.getPixels(pixels, 0, size, 0, 0, size, size)

    val aChannel = IntArray(pixelCount)
    val rChannel = IntArray(pixelCount)
    val gChannel = IntArray(pixelCount)
    val bChannel = IntArray(pixelCount)
    for (i in 0 until pixelCount) {
        val color = pixels[i]
        val a = color ushr 24
        val r = (color ushr 16) and 0xFF
        val g = (color ushr 8) and 0xFF
        val b = color and 0xFF
        aChannel[i] = a
        rChannel[i] = (r * a + 127) / 255
        gChannel[i] = (g * a + 127) / 255
        bChannel[i] = (b * a + 127) / 255
    }

    val tmpA = IntArray(pixelCount)
    val tmpR = IntArray(pixelCount)
    val tmpG = IntArray(pixelCount)
    val tmpB = IntArray(pixelCount)
    for (pass in 0 until BLUR_PASSES) {
        boxBlurHorizontal(
            srcA = aChannel, srcR = rChannel, srcG = gChannel, srcB = bChannel,
            dstA = tmpA, dstR = tmpR, dstG = tmpG, dstB = tmpB,
            size = size, radius = BLUR_RADIUS,
        )
        boxBlurVertical(
            srcA = tmpA, srcR = tmpR, srcG = tmpG, srcB = tmpB,
            dstA = aChannel, dstR = rChannel, dstG = gChannel, dstB = bChannel,
            size = size, radius = BLUR_RADIUS,
        )
    }

    val maxX = (size - 1).toFloat()
    for (y in 0 until size) {
        val rowOffset = y * size
        for (x in 0 until size) {
            val idx = rowOffset + x
            val premultipliedAlpha = aChannel[idx]
            if (premultipliedAlpha <= 0) {
                pixels[idx] = 0
                continue
            }
            val horizontalProgress = if (isRtl) {
                1f - x / maxX
            } else {
                x / maxX
            }
            val outAlpha = (premultipliedAlpha * BACKDROP_MAX_ALPHA * horizontalProgress)
                .roundToInt()
                .coerceIn(0, 255)
            if (outAlpha == 0) {
                pixels[idx] = 0
                continue
            }
            val halfAlpha = premultipliedAlpha / 2
            val outRed = ((rChannel[idx] * 255 + halfAlpha) / premultipliedAlpha).coerceIn(0, 255)
            val outGreen = ((gChannel[idx] * 255 + halfAlpha) / premultipliedAlpha).coerceIn(0, 255)
            val outBlue = ((bChannel[idx] * 255 + halfAlpha) / premultipliedAlpha).coerceIn(0, 255)
            pixels[idx] = (outAlpha shl 24) or (outRed shl 16) or (outGreen shl 8) or outBlue
        }
    }

    bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
    return bitmap.asImageBitmap()
}

private fun drawEggIconToSquareCanvas(
    context: Context,
    @DrawableRes res: Int,
    size: Int,
    canvas: Canvas,
) {
    val drawable = context.requireDrawable(res)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable) {
        // Match AlterableAdaptiveIcon(clipShape = RectangleShape): expand layers by 25% on each
        // side so the adaptive icon fills the full square without the system icon mask.
        val inset = (size * ADAPTIVE_ICON_INSET_FACTOR).roundToInt()
        val left = -inset
        val top = -inset
        val right = size + inset
        val bottom = size + inset
        val background = drawable.background?.mutate()
        if (background != null) {
            background.setBounds(left, top, right, bottom)
            background.draw(canvas)
        }
        val foreground = drawable.foreground?.mutate()
        if (foreground != null) {
            foreground.setBounds(left, top, right, bottom)
            foreground.draw(canvas)
        }
    } else {
        val target = drawable.mutate()
        val intrinsicWidth = target.intrinsicWidth
        val intrinsicHeight = target.intrinsicHeight
        if (intrinsicWidth > 0 && intrinsicHeight > 0 && intrinsicWidth != intrinsicHeight) {
            val scale = min(size.toFloat() / intrinsicWidth, size.toFloat() / intrinsicHeight)
            val drawWidth = (intrinsicWidth * scale).roundToInt()
            val drawHeight = (intrinsicHeight * scale).roundToInt()
            val left = (size - drawWidth) / 2
            val top = (size - drawHeight) / 2
            target.setBounds(left, top, left + drawWidth, top + drawHeight)
        } else {
            target.setBounds(0, 0, size, size)
        }
        target.draw(canvas)
    }
}

private fun boxBlurHorizontal(
    srcA: IntArray, srcR: IntArray, srcG: IntArray, srcB: IntArray,
    dstA: IntArray, dstR: IntArray, dstG: IntArray, dstB: IntArray,
    size: Int,
    radius: Int,
) {
    val diameter = radius * 2 + 1
    for (y in 0 until size) {
        val rowOffset = y * size
        var sumA = 0
        var sumR = 0
        var sumG = 0
        var sumB = 0
        for (k in -radius..radius) {
            val sampleX = k.coerceIn(0, size - 1)
            val idx = rowOffset + sampleX
            sumA += srcA[idx]
            sumR += srcR[idx]
            sumG += srcG[idx]
            sumB += srcB[idx]
        }
        for (x in 0 until size) {
            val outIdx = rowOffset + x
            dstA[outIdx] = (sumA + radius) / diameter
            dstR[outIdx] = (sumR + radius) / diameter
            dstG[outIdx] = (sumG + radius) / diameter
            dstB[outIdx] = (sumB + radius) / diameter

            val removeIdx = rowOffset + (x - radius).coerceAtLeast(0)
            val addIdx = rowOffset + (x + radius + 1).coerceAtMost(size - 1)
            sumA += srcA[addIdx] - srcA[removeIdx]
            sumR += srcR[addIdx] - srcR[removeIdx]
            sumG += srcG[addIdx] - srcG[removeIdx]
            sumB += srcB[addIdx] - srcB[removeIdx]
        }
    }
}

private fun boxBlurVertical(
    srcA: IntArray, srcR: IntArray, srcG: IntArray, srcB: IntArray,
    dstA: IntArray, dstR: IntArray, dstG: IntArray, dstB: IntArray,
    size: Int,
    radius: Int,
) {
    val diameter = radius * 2 + 1
    for (x in 0 until size) {
        var sumA = 0
        var sumR = 0
        var sumG = 0
        var sumB = 0
        for (k in -radius..radius) {
            val sampleY = k.coerceIn(0, size - 1)
            val idx = sampleY * size + x
            sumA += srcA[idx]
            sumR += srcR[idx]
            sumG += srcG[idx]
            sumB += srcB[idx]
        }
        for (y in 0 until size) {
            val outIdx = y * size + x
            dstA[outIdx] = (sumA + radius) / diameter
            dstR[outIdx] = (sumR + radius) / diameter
            dstG[outIdx] = (sumG + radius) / diameter
            dstB[outIdx] = (sumB + radius) / diameter

            val removeIdx = (y - radius).coerceAtLeast(0) * size + x
            val addIdx = (y + radius + 1).coerceAtMost(size - 1) * size + x
            sumA += srcA[addIdx] - srcA[removeIdx]
            sumR += srcR[addIdx] - srcR[removeIdx]
            sumG += srcG[addIdx] - srcG[removeIdx]
            sumB += srcB[addIdx] - srcB[removeIdx]
        }
    }
}
