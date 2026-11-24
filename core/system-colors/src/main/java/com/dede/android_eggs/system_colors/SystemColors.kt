@file:JvmName("ResourcesUtils")

package com.dede.android_eggs.system_colors

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import com.dede.basic.DefType
import com.dede.basic.getIdentifier

/**
 * Resolve the dynamic system color resources (`system_accent1_400` etc.):
 *
 * 1. The runtime tonal palette ([SystemTonalColors]) when it overrides the framework
 *    colors: the wallpaper seed on API 27-30, and on every API the app color source
 *    while it is not the system one (custom seed or brand default), so the eggs match
 *    the theme the user picked.
 * 2. The static fallbacks (values, values-night) of the bundled palette on API < 31.
 * 3. The framework resources on API 31+, forwarded by the values-v31 aliases, so the
 *    egg colors follow the real system dynamic colors.
 *
 * Java callers use [ResourcesUtils.getSystemColor] via the file @JvmName.
 */
@Throws(Resources.NotFoundException::class)
fun Context.getSystemColor(resName: String): Int {
    SystemTonalColors.syncWithSettings(this)
    SystemTonalColors.getColor(resName)?.let { return it }
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

/**
 * Typed overload for code that references a palette entry directly, see [SystemColor].
 */
@Throws(Resources.NotFoundException::class)
fun Context.getSystemColor(color: SystemColor): Int {
    return getSystemColor(color.resName)
}
