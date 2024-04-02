package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.graphics.Path
import androidx.collection.LruCache
import androidx.core.graphics.PathParser

object PathInflater {

    private val cache = LruCache<String, Path>(10)

    private val blankPath = Path()

    fun inflate(pathStr: String): Path {
        var path = cache[pathStr]
        if (path == null) {
            path = inflateInternal(pathStr)
            cache.put(pathStr, path)
        }
        return Path(path)
    }

    @SuppressLint("RestrictedApi")
    private fun inflateInternal(pathStr: String): Path {
        return PathParser.createPathFromPathData(pathStr) ?: blankPath
    }
}