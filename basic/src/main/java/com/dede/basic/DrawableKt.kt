@file:JvmName("DrawableKt")
@file:JvmMultifileClass

package com.dede.basic

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.util.Xml
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.ResourceManagerInternal
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.dede.basic.utils.DynamicObjectUtils
import org.xmlpull.v1.XmlPullParser
import java.util.concurrent.atomic.AtomicBoolean

fun Context.getPackageDrawable(id: Int, pkg: String? = null): Drawable? {
    val resource: Resources = getPackageResources(pkg) ?: return null
    val theme: Resources.Theme? = try {
        val packageContext = createPackageContext(
            pkg, Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
        )
        packageContext.theme
    } catch (_: Exception) {
        null
    }
    return try {
        ResourcesCompat.getDrawable(resource, id, theme)
    } catch (_: Resources.NotFoundException) {
        null
    }
}

private val sharedTypedValue = TypedValue()

internal fun Resources.withXmlAttributes(
    id: Int,
    block: (parser: XmlPullParser, attrs: AttributeSet) -> Unit
) {
    val outValue = sharedTypedValue
    val resources = this
    resources.getValue(id, outValue, true)
    val path: CharSequence? = outValue.string
    if (path?.endsWith(".xml") != true) {
        return
    }

    @Suppress("ResourceType")
    val parser = resources.getXml(id)
    val attrs = Xml.asAttributeSet(parser)
    var type = parser.next()
    while (type != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {
        // Empty loop
        type = parser.next()
    }
    if (type != XmlPullParser.START_TAG) {
        return
    }

    block(parser, attrs)
}

fun Context.withBackground(@DrawableRes id: Int): Boolean {
    var withBackground = false
    resources.withXmlAttributes(id) { parser, attrs ->
        if (parser.name == "adaptive-icon") {
            withBackground = true
            return@withXmlAttributes
        }

        withStyledAttributes(attrs, intArrayOf(R.attr.withBackground)) {
            withBackground = getBoolean(0, withBackground)
        }
    }
    return withBackground
}

/**
 * Check is AdaptiveIconDrawable
 */
fun Context.isAdaptiveIconDrawable(@DrawableRes id: Int): Boolean {
    var isAdaptiveIcon = false
    resources.withXmlAttributes(id) { parser, _ ->
        if (parser.name == "adaptive-icon") {
            isAdaptiveIcon = true
        }
    }
    return isAdaptiveIcon
}

fun Context.requireDrawable(@DrawableRes id: Int): Drawable {
    return requireNotNull(getDrawableCompat(id))
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
private fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? {
    if (Build.VERSION.SDK_INT in Build.VERSION_CODES.N..Build.VERSION_CODES.N_MR1 && !installed.get()) {
        installApi24InflateDelegates()
    }
    return AppCompatResources.getDrawable(this, id)
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
                    arrayOf(
                        String::class.java,
                        interfaces[0]
                    ),// androidx.appcompat.widget.ResourceManagerInternal$InflateDelegate
                    arrayOf(tagName, inflateDelegate)
                )
            }
        }
    }

    // vector, VdcInflateDelegate
    addDelegate("vector", "Vdc")
    // animated-vector, AvdcInflateDelegate
    addDelegate("animated-vector", "Avdc")// ??? android N AnimatedVectorDrawableCompat can't play
    // animated-selector, AsldcInflateDelegate
    addDelegate("animated-selector", "Asldc")

    installed.set(true)
}
