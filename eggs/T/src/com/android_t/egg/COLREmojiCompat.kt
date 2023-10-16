@file:JvmName("COLREmojiCompat")

package com.android_t.egg

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.fonts.SystemFonts
import android.os.Build
import android.util.Log
import com.android_t.egg.PlatLogoActivity.Bubble
import com.dede.basic.*
import kotlinx.coroutines.*
import java.util.WeakHashMap

/**
 * COLR Emoji Compat
 *
 * @author shhu
 * @since 2023/2/10
 */

internal const val TAG = "COLREmojiCompat"

/**
 * Check if system emoji fonts support COLR
 */
fun isSupportedCOLR(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        // https://developer.android.google.cn/about/versions/13/features?hl=zh-cn#color-vector-fonts
        // API 33, added COLR support.
        return false
    }

    // Some OEMs have modified the Emoji implementation.
    // Such as: MEIZU

    // Find first und-Zsye font from `/system/etc/fonts.xml`.
    val undZsyeFontFile = UndZsyeFonts.findFirstUndZsyeFontFile()
    if (undZsyeFontFile != null) {
        val hasCOLR = COLRChecker.hasCOLR(undZsyeFontFile)
        Log.i(TAG, "isSupportedCOLR: $hasCOLR, file: $undZsyeFontFile")
        return hasCOLR
    }

    // Find emoji font from all fonts.
    // Such as:
    // /system/fonts/NotoColorEmoji.ttf
    // /system/fonts/NotoColorEmojiFlags.ttf
    // /system/fonts/NotoColorEmojiLegacy.ttf
    val emojiFontRegex = Regex("^\\S*Emoji\\S*.[to]tf$")
    try {
        val fonts = SystemFonts.getAvailableFonts()
        for (font in fonts) {
            val file = font.file ?: continue
            if (emojiFontRegex.matches(file.name)) {
                val hasCOLR = COLRChecker.hasCOLR(file)
                if (hasCOLR) {
                    Log.i(TAG, "Find isSupportedCOLR: $file")
                    return true
                }
            }
        }
    } catch (e: Exception) {
        Log.w(TAG, e)
    }
    return false
}

fun Canvas.drawCOLREmoji(bubble: Bubble, p: Float) {
    val drawable = bubble.drawable ?: return
    if (bubble.r <= 0f) return
    drawable.setBounds(
        (bubble.x - bubble.r * p).toInt(),
        (bubble.y - bubble.r * p).toInt(),
        (bubble.x + bubble.r * p).toInt(),
        (bubble.y + bubble.r * p).toInt()
    )
    drawable.draw(this)
}

private val cachedDrawable = WeakHashMap<CharSequence, Drawable>()

fun Context.identifierEmojiDrawable(
    emoji: CharSequence?,
    temp: MutableList<CharSequence>? = null,
): Drawable? {
    if (emoji.isNullOrEmpty()) return null
    val cache = cachedDrawable[emoji]
    if (cache != null) return cache

    val drawableName = getEmojiUnicode(
        emoji,
        separator = "_",
        prefix = "t_emoji_u",
        temp = temp
    ).toString()
    val id: Int = this.getIdentifier(drawableName, DefType.DRAWABLE, this.packageName)
    if (id == 0) {
        throw IllegalStateException("Emoji xml not found, name: %s".format(drawableName))
    }
    return createVectorDrawableCompat(id).apply {
        cachedDrawable[emoji] = this
    }
}

fun releaseIdentifiedCOLREmoji() {
    cachedDrawable.clear()
}

fun Array<Bubble>.identifierCOLREmoji(activity: Activity) {
    val list = ArrayList<CharSequence>()
    for (bubble in this) {
        val drawable = activity.identifierEmojiDrawable(bubble.text, list)
        if (drawable != null) {
            bubble.drawable = drawable
            bubble.text = null
        }
    }
}