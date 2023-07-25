@file:JvmName("UtilExt")

package com.dede.basic

import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * 计算Emoji的Unicode
 */
fun getEmojiUnicode(
    emoji: CharSequence,
    separator: CharSequence = "\\u",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    temp: MutableList<CharSequence>? = null,
): CharSequence {
    val list: MutableList<CharSequence> = if (temp != null) {
        temp.clear();temp
    } else ArrayList()
    var offset = 0
    while (offset < emoji.length) {
        val codePoint = Character.codePointAt(emoji, offset)
        offset += Character.charCount(codePoint)
        if (codePoint == 0xFE0F) {
            // the codepoint is a emoji style standardized variation selector
            continue
        }
        list.add("%04x".format(codePoint))
    }
    return list.joinToString(separator = separator, prefix = prefix, postfix = postfix)
}

/**
 * Unicode编码规则: Unicode码对每一个字符用4位16进制数表示。
 * 具体规则是：将一个字符(char)的高8位与低8位分别取出，转化为16进制数，
 * 如果转化的16进制数的长度不足2位，则在高位补0，然后将高、低8位转成的16进制字符串拼接起来并在前面补上"\\u" 即可。
 */
fun String?.toUnicode(prefix: String = "\\u", join: String = ""): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    val sb = StringBuilder()
    var s = true
    var hex: Int
    var hexStr: String
    for (c in this.toCharArray()) {
        if (s) {
            s = false
        } else {
            sb.append(join)
        }
        // 加上\\u前缀
        sb.append(prefix)
        // 取出高8位
        hex = c.code ushr 8
        hexStr = Integer.toHexString(hex)
        if (hexStr.length == 1) sb.append("0")
        sb.append(hexStr)

        // 取出低8位
        hex = c.code and 0xFF
        hexStr = Integer.toHexString(hex)
        if (hexStr.length == 1) sb.append("0")
        sb.append(hexStr)
    }
    return sb.toString()
}

val Number.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        globalContext.resources.displayMetrics
    ).roundToInt()

val Number.dpf: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        globalContext.resources.displayMetrics
    )