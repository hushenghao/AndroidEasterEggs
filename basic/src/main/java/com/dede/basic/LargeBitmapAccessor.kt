package com.dede.basic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.lang.ref.WeakReference
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Emoji Bitmap Drawable accessor
 *
 * @author shhu
 * @since 2022/9/7
 */
class LargeBitmapAccessor(private val context: Context) {

    private val cached = object : LruCache<Key, WeakReference<Bitmap>>(20 * 1024 * 1024) {
        override fun sizeOf(key: Key, value: WeakReference<Bitmap>): Int {
            return value.get()?.byteCount ?: 0
        }
    }
    private val options = BitmapFactory.Options()

    private data class Key(val id: Int, val width: Int, val height: Int) {
        companion object {
            fun obtain(id: Int): Key = Key(id, 0, 0)

            fun obtain(id: Int, width: Int, height: Int): Key = Key(id, width, height)
        }
    }

    fun getIdentifier(name: String): Int {
        return context.getIdentifier(name, DefType.DRAWABLE, context.packageName)
    }

    private fun decodeBitmap(id: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(context.resources, id, options)
        options.inSampleSize = calculateSampleSize(reqWidth, reqHeight, options)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(context.resources, id, options)!!
    }

    private fun calculateSampleSize(
        reqWidth: Int,
        reqHeight: Int,
        options: BitmapFactory.Options,
    ): Int {
        var sampleSize = 1
        if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
            val widthRatio = (options.outWidth.toFloat() / reqWidth).roundToInt()
            val heightRatio = (options.outHeight.toFloat() / reqHeight).roundToInt()
            sampleSize = min(widthRatio, heightRatio);
        }
        return sampleSize
    }

    fun requireBitmap(id: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        val key = Key.obtain(id, reqWidth, reqHeight)
        var cache = cached.get(key)?.get()
        if (cache == null) {
            cache = decodeBitmap(id, reqWidth, reqHeight)
            cached.put(key, WeakReference(cache))
        }
        return cache
    }
}