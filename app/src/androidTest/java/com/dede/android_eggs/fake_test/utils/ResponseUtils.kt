package com.dede.android_eggs.fake_test.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import com.dede.basic.requireDrawable
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object ResponseUtils {

    fun Bitmap.toResponse(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP_LOSSLESS,
        quality: Int = 100
    ): Response {
        val stream = ByteArrayOutputStream().apply {
            compress(format, quality, this)
            recycle()
        }
        val byteArray = stream.toByteArray()
        val mimeType = when (format) {
            Bitmap.CompressFormat.JPEG -> "image/jpeg"
            Bitmap.CompressFormat.PNG -> "image/png"
            Bitmap.CompressFormat.WEBP_LOSSY,
            Bitmap.CompressFormat.WEBP_LOSSLESS,
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP -> "image/webp"
            else -> throw IllegalArgumentException("bitmap compress format: $format")
        }
        return NanoHTTPD.newFixedLengthResponse(
            Response.Status.OK, mimeType,
            ByteArrayInputStream(byteArray),
            byteArray.size.toLong()
        )
    }

    fun createDrawableResponse(
        context: Context,
        @DrawableRes resId: Int,
        width: Int,
        height: Int,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP_LOSSLESS,
        quality: Int = 100
    ): Response {
        val drawable = context.requireDrawable(resId)
        val bitmap = drawable.toBitmap(width, height)
        return bitmap.toResponse(format, quality)
    }
}