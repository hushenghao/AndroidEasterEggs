@file:JvmName("DrawableKt")

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.core.content.ContextCompat

@SuppressLint("DiscouragedApi")
@Throws(Resources.NotFoundException::class)
fun Context.getSystemColor(resName: String): Int {
    val id = getIdentifier(resName, DefType.COLOR, "android")
    return ContextCompat.getColor(this, id)
}

enum class DefType {
    DRAWABLE,
    COLOR
    ;

    override fun toString(): String {
        return when (this) {
            DRAWABLE -> "drawable"
            COLOR -> "color"
        }
    }
}

private val identifierCache = LruCache<String, Int>(50)

private fun makeKey(name: String, defType: DefType, packageName: String): String {
    return "$packageName:$defType/$name"
}

@JvmOverloads
@SuppressLint("DiscouragedApi")
fun Context.getIdentifier(name: String, defType: DefType, defPackage: String = packageName): Int {
    val key = makeKey(name, defType, defPackage)
    var id = identifierCache.get(key)
    if (id == null) {
        val type = when (defType) {
            DefType.DRAWABLE -> "drawable"
            DefType.COLOR -> "color"
        }
        id = resources.getIdentifier(name, type, defPackage)
        identifierCache.put(key, id)
    }
    return id
}

fun Context.requireDrawable(id: Int): Drawable {
    return requireNotNull(ContextCompat.getDrawable(this, id))
}
