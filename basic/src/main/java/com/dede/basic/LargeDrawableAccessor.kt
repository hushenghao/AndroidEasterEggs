package com.dede.basic

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.LruCache
import java.lang.ref.WeakReference

/**
 * Emoji Bitmap Drawable accessor
 *
 * @author shhu
 * @since 2022/9/7
 */
class LargeDrawableAccessor(private val context: Context) {

    private val cachedDrawable = LruCache<Int, WeakReference<Drawable>>(50)

    fun getIdentifier(name: String): Int {
        return context.getIdentifier(name, DefType.DRAWABLE, context.packageName)
    }

    fun requireDrawable(id: Int): Drawable {
        var cache = cachedDrawable.get(id).get()
        if (cache == null) {
            cache = context.requireDrawable(id)
            cachedDrawable.put(id, WeakReference(cache))
        }
        return cache
    }
}