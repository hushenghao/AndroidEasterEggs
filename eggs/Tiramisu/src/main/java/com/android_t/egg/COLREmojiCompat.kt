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
import com.dede.basic.DefType
import com.dede.basic.getIdentifier
import com.dede.basic.requireDrawable
import com.dede.eggs.jvm_basic.EmojiUtils
import java.io.File
import java.util.WeakHashMap

/**
 * COLR Emoji Compat
 *
 * @author shhu
 * @since 2023/2/10
 */

internal const val TAG = "COLREmojiCompat"

/**
 * Find COLR font file from system fonts.
 */
fun findCOLRFontFile(): File? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        // https://developer.android.google.cn/about/versions/13/features#color-vector-fonts
        // API 33, added COLR support.
        return null
    }

    // Some OEMs have modified the Emoji implementation.
    // Such as: MEIZU

    // Find emoji font from all fonts.
    // Such as:
    // /system/fonts/NotoColorEmoji.ttf
    // /system/fonts/NotoColorEmojiFlags.ttf
    // /system/fonts/NotoColorEmojiLegacy.ttf
    val emojiFontRegex = Regex("^\\S*Emoji\\S*.[to]t[fc]$")
    for (font in SystemFonts.getAvailableFonts()) {
        val file = font.file ?: continue
        if (!emojiFontRegex.matches(file.name)) continue
        if (COLRv1.analyzeCOLR(file)) {// End when find any one that supports
            Log.i(TAG, "Find emoji font isSupportedCOLR: $file")
            return file
        }
    }

    // Find first und-Zsye font from `/system/etc/fonts.xml`.
    val undZsyeFontFile = UndZsyeFonts.findFirstUndZsyeFontFile()
    if (undZsyeFontFile != null) {
        if (COLRv1.analyzeCOLR(undZsyeFontFile)) {
            Log.i(TAG, "Find und-Zsye font isSupportedCOLR: $undZsyeFontFile")
            return undZsyeFontFile
        }
    }

    return null
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

    val drawableName = EmojiUtils.getEmojiUnicode(
        emoji,
        separator = "_",
        prefix = "t_emoji_u",
        temp = temp
    ).toString()
    val id: Int = this.getIdentifier(drawableName, DefType.DRAWABLE, this.packageName)
    if (id == 0) {
        throw IllegalStateException("Emoji xml not found, name: %s".format(drawableName))
    }
    return requireDrawable(id).apply {
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