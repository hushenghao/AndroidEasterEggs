package com.dede.basic

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.LruCache

/**
 * Emoji Bitmap Drawable accessor
 *
 * @author shhu
 * @since 2022/9/7
 */
class LargeDrawableAccessor(private val context: Context) {

    private val cachedDrawable = LruCache<Int, Drawable>(50)

    fun getIdentifier(name: String): Int {
        return context.getIdentifier(name, DefType.DRAWABLE, context.packageName)
    }

    fun requireDrawable(id: Int): Drawable {
        var cache = cachedDrawable.get(id)
        if (cache == null) {
            cache = context.requireDrawable(id)
            cachedDrawable.put(id, cache)
        }
        return cache
    }
}