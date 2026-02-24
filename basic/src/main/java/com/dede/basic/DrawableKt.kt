@file:JvmName("DrawableKt")
@file:JvmMultifileClass

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import android.util.TypedValue
import android.util.Xml
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.ResourceManagerInternal
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.dede.basic.utils.DynamicObjectUtils
import org.xmlpull.v1.XmlPullParser
import java.util.concurrent.atomic.AtomicBoolean

@Throws(Resources.NotFoundException::class)
fun Context.getSystemColor(resName: String): Int {
    val id = getIdentifier(resName, DefType.COLOR, "android")
    return ContextCompat.getColor(this, id)
}

enum class DefType {
    DRAWABLE,
    MIPMAP,
    COLOR,
    RAW,
    XML,
    STRING,
    ;

    override fun toString(): String {
        return when (this) {
            DRAWABLE -> "drawable"
            MIPMAP -> "mipmap"
            COLOR -> "color"
            RAW -> "raw"
            XML -> "xml"
            STRING -> "string"
        }
    }
}

private val identifierCache = LruCache<String, Int>(50)

private fun makeKey(name: String, defType: DefType, packageName: String): String {
    return "$packageName:$defType/$name"
}

private fun Context.getPackageResources(pkg: String?): Resources? {
    return when (pkg) {
        null, packageName -> resources
        "android" -> Resources.getSystem()
        else -> {
            val pm = packageManager
            var flags = PackageManager.GET_SHARED_LIBRARY_FILES
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                flags = flags or PackageManager.MATCH_UNINSTALLED_PACKAGES
            }
            try {
                val applicationInfo = pm.getApplicationInfo(pkg, flags)
                pm.getResourcesForApplication(applicationInfo)
            } catch (_: Exception) {
                null
            }
        }
    }
}

@JvmOverloads
@SuppressLint("DiscouragedApi")
fun Context.getIdentifier(name: String, defType: DefType, pkg: String = packageName): Int {
    val key = makeKey(name, defType, pkg)
    var id = identifierCache.get(key)
    if (id == null) {
        val appResources: Resources? = getPackageResources(pkg)
        val type = defType.toString()
        id = appResources?.getIdentifier(name, type, pkg) ?: 0//Resources.ID_NULL
        identifierCache.put(key, id)
    }
    return id
}

fun Context.getPackageDrawable(id: Int, pkg: String? = null): Drawable? {
    if (pkg == null || pkg == packageName || pkg == "android") {
        return getDrawableCompat(id)
    }

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

/**
 * Check is SupportAdaptiveIconDrawable
 */
fun Context.isAdaptiveIconDrawable(@DrawableRes id: Int): Boolean {
    val outValue = sharedTypedValue
    val resources = this.resources
    resources.getValue(id, outValue, true)
    val path: CharSequence? = outValue.string
    if (path?.endsWith(".xml") != true) {
        return false
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
        return false
    }
    if (parser.name == "adaptive-icon") {
        return true
    }

    var supportAdaptiveIcon = false
    withStyledAttributes(attrs, intArrayOf(R.attr.supportAdaptiveIcon)) {
        supportAdaptiveIcon = getBoolean(0, supportAdaptiveIcon)
    }
    return supportAdaptiveIcon
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
