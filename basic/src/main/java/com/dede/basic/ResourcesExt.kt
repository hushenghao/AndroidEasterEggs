@file:JvmName("ResourcesUtils")

package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.util.LruCache
import androidx.core.content.ContextCompat

@Throws(Resources.NotFoundException::class)
fun Context.getSystemColor(resName: String): Int {
    // Resolve the local resource first, so the static fallbacks (values, values-night)
    // apply on API < 31 and the values-v31 aliases forward to the system dynamic colors.
    val id = getIdentifier(resName, DefType.COLOR)
    if (id != 0) {
        return ContextCompat.getColor(this, id)
    }
    val sysId = getIdentifier(resName, DefType.COLOR, "android")
    if (sysId != 0) {
        return ContextCompat.getColor(this, sysId)
    }
    throw Resources.NotFoundException(resName)
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

internal fun Context.getPackageResources(pkg: String?): Resources? {
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
