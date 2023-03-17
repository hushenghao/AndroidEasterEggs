package com.dede.android_eggs.ui.blurhash

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.toDrawable
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.map.Mapper
import coil.request.Options
import coil.size.pxOrElse
import com.dede.android_eggs.ui.blurhash.BlurHashFinder.Companion.getSizeParameter
import com.dede.android_eggs.util.applyIf
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * blur-hash Uri.
 *
 * Normal:    <scheme>://<authority><path>?<query>
 * blur-hash: <blur-hash>://<a blur hash>/?<w=200&h=150>
 *  1. Added width and height parameters, ?w=200&h=150. Too large a resolution will affect performance;
 *  2. '/', Used to separate hash and parameter. If include parameters, must add a separator;
 *
 * For example:
 *  blur-hash://LOFqFcNxsQS6|,oIj@ax=cxFjufk/?w=200&h=150
 *  blur-hash://LVPO*{9docS$}Nn4R.oy$]${n$bI
 *
 * @see BlurHashFinder    parse blur-hash Uri
 * @see BlurHashFetcher   Coil load blur-hash Uri
 * @see BlurHashStringMapper    Coil load blur-hash String
 * @see BlurHashMapper    Coil load BlurHash obj
 */
const val BLUR_HASH_SCHEME = "blur-hash"
const val QUERY_KEY_W = "w"
const val QUERY_KEY_H = "h"

private const val SIZE_DEFAULT = 32
private const val SIZE_UNDEFINED = -1

class BlurHashDrawable(
    hash: String?,
    width: Int = SIZE_DEFAULT,  // Too large a resolution will affect performance
    height: Int = SIZE_DEFAULT, // Too large a resolution will affect performance
) : Drawable() {

    constructor(
        context: Context,
        @StringRes hashRes: Int,
        width: Int = SIZE_DEFAULT,
        height: Int = SIZE_DEFAULT,
    ) : this(context.getString(hashRes), width, height)

    private val blurHashFinder = BlurHashFinder(hash)

    private val width = blurHashFinder.getWidth(width)
    private val height = blurHashFinder.getHeight(height)
    private val bitmap: Bitmap? by lazy {
        BlurHashDecoder.decode(blurHashFinder.getHash(), this.width, this.height)
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun getIntrinsicHeight(): Int {
        return height
    }

    override fun getIntrinsicWidth(): Int {
        return width
    }

    override fun draw(canvas: Canvas) {
        if (width <= 0 || height <= 0 || bitmap == null) return
        canvas.drawBitmap(bitmap!!, 0f, 0f, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }
}

private class BlurHashFinder(hashStr: String?) {

    companion object {
        private val regex =
            Regex("^(blur-hash://)?([\\dA-Za-z#\$%*+,\\-.:;=?@\\[\\]^_{|}~]{6,})/?\\??\\S*$")

        fun Uri?.getSizeParameter(key: String, default: Int): Int {
            if (this == null) return default
            var size = getQueryParameter(key)?.toIntOrNull() ?: default
            if (size <= SIZE_UNDEFINED) {
                size = default
            }
            return size
        }
    }

    private val blurHashUri: Uri? = parseBlurHashUri(hashStr)

    private fun parseBlurHashUri(hashStr: String?): Uri? {
        if (hashStr == null) return null
        var hash = hashStr.findHash()
        if (hash != null) {
            return buildBlurHashUri(hash)
        }
        val decode = URLDecoder.decode(hashStr, "utf-8")
        hash = decode.findHash()
        if (hash != null) {
            return buildBlurHashUri(hash)
        }
        return null
    }

    private fun String.findHash(): String? {
        return regex.matchEntire(this)?.groups?.get(2)?.value
    }

    fun getUri(): Uri? {
        return blurHashUri
    }

    fun getWidth(default: Int): Int {
        return blurHashUri.getSizeParameter(QUERY_KEY_W, default)
    }

    fun getHeight(default: Int): Int {
        return blurHashUri.getSizeParameter(QUERY_KEY_H, default)
    }

    fun getHash(): String? {
        return blurHashUri?.authority
    }
}

fun buildBlurHashUri(
    hash: String,
    width: Int = SIZE_UNDEFINED,
    height: Int = SIZE_UNDEFINED,
): Uri {
    return Uri.Builder()
        .scheme(BLUR_HASH_SCHEME)
        .encodedAuthority(hash)
        .encodedPath("/")// Used to separate hash and parameter
        .applyIf(width > SIZE_UNDEFINED) {
            appendQueryParameter(QUERY_KEY_W, width.toString())
        }
        .applyIf(height > SIZE_UNDEFINED) {
            appendQueryParameter(QUERY_KEY_H, height.toString())
        }
        .build()
}

fun ImageLoader.Builder.blurHash() = components {
    add(BlurHashMapper())
    add(BlurHashStringMapper())
    add(BlurHashFetcher.Factory())
}

sealed class BlurHash(
    val hash: String,
    val width: Int = SIZE_UNDEFINED,
    val height: Int = SIZE_UNDEFINED,
)

private class BlurHashMapper : Mapper<BlurHash, Uri> {
    override fun map(data: BlurHash, options: Options): Uri {
        return buildBlurHashUri(data.hash, data.width, data.height)
    }
}

private class BlurHashStringMapper : Mapper<String, Uri> {
    override fun map(data: String, options: Options): Uri? {
        return BlurHashFinder(data).getUri()
    }
}

private class BlurHashFetcher(private val data: Uri, private val options: Options) : Fetcher {

    private val lock: Any get() = MemoryCacheCleaner.instance

    override suspend fun fetch(): FetchResult? {
        MemoryCacheCleaner.checkRegister(options.context)

        val width = data.getSizeParameter(
            QUERY_KEY_W,
            options.size.width.pxOrElse { SIZE_DEFAULT })
        val height = data.getSizeParameter(
            QUERY_KEY_H,
            options.size.height.pxOrElse { SIZE_DEFAULT })
        val bitmap = synchronized(lock) {
            BlurHashDecoder.decode(blurHash = data.authority, width = width, height = height)
        } ?: return null
        return DrawableResult(
            drawable = bitmap.toDrawable(options.context.resources),
            isSampled = false,
            dataSource = DataSource.MEMORY
        )
    }

    private class MemoryCacheCleaner : ComponentCallbacks2 {

        companion object {
            val instance = MemoryCacheCleaner()
            private var isRegistered = AtomicBoolean(false)

            fun checkRegister(context: Context) {
                if (!isRegistered.getAndSet(true)) {
                    context.applicationContext.registerComponentCallbacks(instance)
                }
            }
        }

        override fun onConfigurationChanged(newConfig: Configuration) {
        }

        override fun onLowMemory() {
            onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_COMPLETE)
        }

        override fun onTrimMemory(level: Int) {
            if (level > ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
                synchronized(this) {
                    BlurHashDecoder.clearCache()
                }
            }
        }
    }

    class Factory : Fetcher.Factory<Uri> {
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? {
            if (!isApplicable(data)) return null
            return BlurHashFetcher(data, options)
        }

        private fun isApplicable(data: Uri): Boolean {
            return data.scheme == BLUR_HASH_SCHEME && data.authority != null
        }
    }
}