@file:JvmName("DrawableKt")
@file:JvmMultifileClass

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import android.util.Xml
import androidx.annotation.DrawableRes
import androidx.annotation.XmlRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

@Throws(Resources.NotFoundException::class)
fun Context.getSystemColor(resName: String): Int {
    val id = getIdentifier(resName, DefType.COLOR, "android")
    return ContextCompat.getColor(this, id)
}

enum class DefType {
    DRAWABLE,
    COLOR,
    RAW,
    XML,
    ;

    override fun toString(): String {
        return when (this) {
            DRAWABLE -> "drawable"
            COLOR -> "color"
            RAW -> "raw"
            XML -> "xml"
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
        val type = defType.toString()
        id = resources.getIdentifier(name, type, defPackage)
        identifierCache.put(key, id)
    }
    return id
}

fun Context.requireDrawable(@DrawableRes id: Int): Drawable {
    return requireNotNull(ContextCompat.getDrawable(this, id))
}

fun Context.createVectorDrawableCompat(@DrawableRes id: Int): VectorDrawableCompat {
    if (Build.VERSION.SDK_INT in Build.VERSION_CODES.N..Build.VERSION_CODES.N_MR1) {
        // Fix Android N VectorDrawable
        // https://issuetracker.google.com/issues/37138664
        return createVectorDrawableCompatFromXml(id)
    }
    return requireNotNull(VectorDrawableCompat.create(this.resources, id, this.theme))
}

/**
 * Force use support Library
 */
private fun Context.createVectorDrawableCompatFromXml(@XmlRes id: Int): VectorDrawableCompat {
    val parser = resources.getXml(id)
    val attrs = Xml.asAttributeSet(parser)
    var type: Int
    while (parser.next().also { type = it } != XmlPullParser.START_TAG &&
        type != XmlPullParser.END_DOCUMENT) {
        // Empty loop
    }
    if (type != XmlPullParser.START_TAG) {
        throw XmlPullParserException("No start tag found")
    }
    return VectorDrawableCompat.createFromXmlInner(resources, parser, attrs, theme)
}
