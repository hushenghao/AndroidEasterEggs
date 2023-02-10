@file:JvmName("COLRBitmapCompat")

package com.android_t.egg

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.android_t.egg.PlatLogoActivity.Bubble
import com.dede.basic.LargeBitmapAccessor
import com.dede.basic.toUnicode
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Created by shhu on 2023/2/10 11:46.
 *
 * @author shhu
 * @since 2023/2/10
 */

private val rectF: RectF = RectF()

fun Canvas.drawCOLRBitmap(bubble: Bubble, p: Float, paint: Paint) {
    rectF.set(
        bubble.x - bubble.r * p,
        bubble.y - bubble.r * p,
        bubble.x + bubble.r * p,
        bubble.y + bubble.r * p
    )
    this.drawBitmap(bubble.bitmap, null, rectF, paint)
}

fun Array<Bubble>.convertCOLRBitmap(drawableAccessor: LargeBitmapAccessor) {
    val sizeMap = HashMap<String, Float>()
    var r: Float?
    for (bubble in this) {
        r = sizeMap[bubble.text]
        if (r == null) {
            r = bubble.r
            sizeMap[bubble.text] = r
        } else {
            sizeMap[bubble.text] = max(r, bubble.r)
        }
    }
    for (bubble in this) {
        val id: Int = drawableAccessor.getIdentifier(
            String.format(
                "t_emoji_%s",
                bubble.text.toUnicode("u", "_")
            )
        )
        val size = ((sizeMap[bubble.text] ?: 0f) * 2).roundToInt()
        bubble.bitmap = drawableAccessor.requireBitmap(id, size, size)
        bubble.text = null
    }
}