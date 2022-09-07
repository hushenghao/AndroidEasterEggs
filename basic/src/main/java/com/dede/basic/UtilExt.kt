@file:JvmName("UtilExt")

package com.dede.basic

/**
 * emoji to unicode
 */
@JvmOverloads
fun String.unicode(join: String = "_"): String {
    val sb = StringBuilder()
    var s = true
    for (c in this.toCharArray()) {
        if (s) {
            s = false
        } else {
            sb.append(join)
        }
        sb.append("u").append(Integer.toHexString(c.code))
    }
    return sb.toString()
}