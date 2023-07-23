@file:JvmName("COLREmojiCompat")

package com.android_t.egg

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.android_t.egg.PlatLogoActivity.Bubble
import com.dede.basic.*
import kotlinx.coroutines.*
import kotlin.math.max

/**
 * Created by shhu on 2023/2/10 11:46.
 *
 * @author shhu
 * @since 2023/2/10
 */

fun Canvas.drawCOLREmoji(bubble: Bubble, p: Float) {
    val drawable = bubble.drawable ?: return
    drawable.setBounds(
        (bubble.x - bubble.r * p).toInt(),
        (bubble.y - bubble.r * p).toInt(),
        (bubble.x + bubble.r * p).toInt(),
        (bubble.y + bubble.r * p).toInt()
    )
    drawable.draw(this)
}

fun Context.identifierEmojiDrawable(emoji: String?, temp: MutableList<String>? = null): Drawable? {
    if (emoji.isNullOrEmpty()) return null
    val list: MutableList<String> = if (temp == null) {
        ArrayList()
    } else {
        temp.clear()
        temp
    }
    for (i in 0 until max(emoji.codePointCount(0, emoji.length - 1), 1)) {
        val code = emoji.codePointAt(i)
        if (code == 0xFE0F) continue
        list.add("%04x".format(code))
    }
    val drawableName = list.joinToString("_", prefix = "t_emoji_u")
    val id: Int = this.getIdentifier(drawableName, DefType.DRAWABLE, this.packageName)
    if (id == 0) {
        throw IllegalStateException("Emoji Drawable not found, name: %s".format(drawableName))
    }
    return this.requireDrawable(id)
}

fun Array<Bubble>.identifierCOLREmoji(activity: Activity) {
    val list = ArrayList<String>()
    for (bubble in this) {
        val drawable = activity.identifierEmojiDrawable(bubble.text, list)
        if (drawable != null) {
            bubble.drawable = drawable
            bubble.text = null
        }
    }
}