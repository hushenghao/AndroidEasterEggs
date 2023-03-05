@file:JvmName("COLRBitmapCompat")

package com.android_t.egg

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.ImageRequest
import com.android_t.egg.PlatLogoActivity.Bubble
import com.dede.basic.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

fun Array<Bubble>.convertCOLRBitmap(activity: Activity, result: () -> Unit) {
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
    GlobalScope.launch {
        for ((i, bubble) in this@convertCOLRBitmap.withIndex()) {
            val id: Int = activity.getIdentifier(
                String.format(
                    "t_emoji_%s",
                    bubble.text.toUnicode("u", "_")
                ),
                DefType.DRAWABLE,
                activity.packageName
            )
            val size = ((sizeMap[bubble.text] ?: 0f) * 2).roundToInt()
            if (bubble.r > 0f) {
                val request = ImageRequest.Builder(activity)
                    .data(id)
                    .size(size)
                    .allowConversionToBitmap(true)
                    .build()
                val drawable = activity.imageLoader.execute(request).drawable
                if (drawable is BitmapDrawable) {
                    bubble.bitmap = drawable.bitmap
                    bubble.text = null
                }
            }
            if (i >= this@convertCOLRBitmap.size - 1) {
                withContext(Dispatchers.Main) {
                    result.invoke()
                }
            }
        }
    }
}