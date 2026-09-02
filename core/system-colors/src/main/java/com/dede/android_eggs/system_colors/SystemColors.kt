@file:JvmName("ResourcesUtils")

package com.dede.android_eggs.system_colors

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.core.content.ContextCompat
import com.dede.basic.DefType
import com.dede.basic.getIdentifier

/**
 * Resolve the dynamic system color resources (`system_accent1_400` etc.):
 *
 * 1. The runtime wallpaper tonal palette ([SystemTonalColors]) on API 27-30, where the
 *    framework does not provide dynamic colors, so the egg colors follow the current
 *    theme seed.
 * 2. The static fallbacks (values, values-night) of the bundled palette on API < 31.
 * 3. The framework resources on API 31+, forwarded by the values-v31 aliases, so the
 *    egg colors follow the real system dynamic colors.
 *
 * Java callers use [ResourcesUtils.getSystemColor] via the file @JvmName.
 */
@Throws(Resources.NotFoundException::class)
fun Context.getSystemColor(resName: String): Int {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        SystemTonalColors.getColor(resName)?.let { return it }
    }
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
