package com.dede.android_eggs.util

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.graphics.drawable.toDrawable
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.map.Mapper
import coil.request.Options
import coil.size.pxOrElse
import com.wolt.blurhashkt.BlurHashDecoder
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicBoolean

// blur-hash://LOFqFcNxsQS6|,oIj@ax=cxFjufk/?w=200&h=150
const val BLUR_HASH_SCHEME = "blur-hash"
const val QUERY_KEY_W = "w"
const val QUERY_KEY_H = "h"

private const val DEFAULT_WIDTH = 200
private const val DEFAULT_HEIGHT = 150
private const val SIZE_UNDEFINED = -1

class BlurHashDrawable(
    hash: String?,
    width: Int = DEFAULT_WIDTH,
    height: Int = DEFAULT_HEIGHT
) : Drawable() {

    private val blurHashFinder = BlurHashFinder(hash)

    private val width = blurHashFinder.getWidth(DEFAULT_WIDTH)
    private val height = blurHashFinder.getHeight(DEFAULT_HEIGHT)

    private val bitmap: Bitmap? by lazy {
        BlurHashDecoder.decode(blurHashFinder.getHash(), width, height)
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

fun ImageLoader.Builder.blurHash() = components {
    add(BlurHashMapper())
    add(BlurHashStringMapper())
    add(BlurHashFetcher.Factory())
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

sealed class BlurHash(
    val hash: String,
    val width: Int = SIZE_UNDEFINED,
    val height: Int = SIZE_UNDEFINED,
)

private class BlurHashMapper : Mapper<BlurHash, Uri> {
    override fun map(data: BlurHash, options: Options): Uri {
        return buildBlurHashUri(data.hash)
    }
}

private class BlurHashFinder(hashStr: String?) {

    companion object {
        private val regex =
            Regex("^(blur-hash://)?([\\dA-Za-z#\$%*+,\\-.:;=?@\\[\\]^_{|}~]{6,})/?\\??\\S*$")

        fun Uri.getSizeParameter(key: String, default: Int): Int {
            var size = getQueryParameter(key)?.toIntOrNull() ?: default
            if (size <= SIZE_UNDEFINED) {
                size = default
            }
            return size
        }
    }

    private var blurHashUri: Uri? = null

    init {
        if (hashStr != null) {
            var hash = hashStr.findHash()
            if (hash != null) {
                blurHashUri = buildBlurHashUri(hash)
            } else {
                val decode = URLDecoder.decode(hashStr, "utf-8")
                hash = decode.findHash()
                if (hash != null) {
                    blurHashUri = buildBlurHashUri(hash)
                }
            }
        }
    }

    private fun String.findHash(): String? {
        return regex.matchEntire(this)?.groups?.get(2)?.value
    }

    fun getUri(): Uri? {
        return blurHashUri
    }

    fun getWidth(default: Int): Int {
        return blurHashUri?.getSizeParameter(QUERY_KEY_W, default) ?: default
    }

    fun getHeight(default: Int): Int {
        return blurHashUri?.getSizeParameter(QUERY_KEY_H, default) ?: default
    }

    fun getHash(): String? {
        return blurHashUri?.authority
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

        val width = data.getSizeParameter(QUERY_KEY_W,
            options.size.width.pxOrElse { DEFAULT_WIDTH })
        val height = data.getSizeParameter(QUERY_KEY_H,
            options.size.height.pxOrElse { DEFAULT_HEIGHT })
        val bitmap = synchronized(lock) {
            BlurHashDecoder.decode(blurHash = data.authority, width = width, height = height)
        } ?: return null
        return DrawableResult(
            drawable = bitmap.toDrawable(options.context.resources),
            isSampled = false,
            dataSource = DataSource.MEMORY
        )
    }

    private fun Uri.getSizeParameter(key: String, default: Int): Int {
        var size = getQueryParameter(key)?.toIntOrNull() ?: default
        if (size <= SIZE_UNDEFINED) {
            size = default
        }
        return size
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