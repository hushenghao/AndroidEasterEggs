@file:JvmName("COLREmojiCompat")

package com.android_t.egg

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
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