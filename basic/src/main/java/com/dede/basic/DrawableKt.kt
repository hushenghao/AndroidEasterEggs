@file:JvmName("DrawableKt")
@file:JvmMultifileClass

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.ResourceManagerInternal
import androidx.core.content.ContextCompat
import com.dede.basic.utils.DynamicObjectUtils
import java.util.concurrent.atomic.AtomicBoolean

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

/**
 * Return a drawable object associated with a particular resource ID.
 *
 * <p>This method supports inflation of {@code <vector>}, {@code <animated-vector>} and
 * {@code <animated-selector>} resources on devices where platform support is not available.</p>
 *
 * Fixed issues:
 * * Android N VectorDrawable [#37138664](https://issuetracker.google.com/issues/37138664)
 */
fun Context.requireDrawable(@DrawableRes id: Int): Drawable {
    if (Build.VERSION.SDK_INT in Build.VERSION_CODES.N..Build.VERSION_CODES.N_MR1 && !installed.get()) {
        installApi24InflateDelegates()
    }
    val drawable = AppCompatResources.getDrawable(this, id)
    return requireNotNull(drawable)
}

private val installed = AtomicBoolean(false)

/**
 * Force use support Library
 *
 * @see ResourceManagerInternal.installDefaultInflateDelegates
 */
@Suppress("RestrictedApi", "SpellCheckingInspection")
@Synchronized
private fun installApi24InflateDelegates() {
    val manager = ResourceManagerInternal.get()
    val managerDynamicObject = DynamicObjectUtils.asDynamicObject(manager)

    fun addDelegate(tagName: String, delegateNamePrefix: String) {
        // androidx.appcompat.widget.ResourceManagerInternal$VdcInflateDelegate
        val className = StringBuilder("androidx.appcompat.widget.ResourceManagerInternal")
            .append("$")
            .append(delegateNamePrefix)
            .append("InflateDelegate")
            .toString()

        val inflateDelegate = DynamicObjectUtils
            .asDynamicObject(className)
            .newInstance()
            .getValue()
        if (inflateDelegate != null) {
            val interfaces = inflateDelegate.javaClass.interfaces
            if (interfaces.isNotEmpty()) {
                managerDynamicObject.invokeMethod(
                    "addDelegate",
                    arrayOf(String::class.java, interfaces[0]),
                    arrayOf(tagName, inflateDelegate)
                )
            }
        }
    }

    // vector, VdcInflateDelegate
    addDelegate("vector", "Vdc")
    // animated-vector, AvdcInflateDelegate
    addDelegate("animated-vector", "Avdc")
    // animated-selector, AsldcInflateDelegate
    addDelegate("animated-selector", "Asldc")

    installed.set(true)
}
