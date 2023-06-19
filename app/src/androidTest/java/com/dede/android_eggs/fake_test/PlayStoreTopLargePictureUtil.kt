package com.dede.android_eggs.fake_test

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dede.android_eggs.R
import com.dede.basic.requireDrawable
import fi.iki.elonen.NanoHTTPD
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

/**
 * Generate Play Store top large picture
 *
 * @author shhu
 * @since 2022/9/9
 */
@Ignore("Generate Play Store top large picture") // remove this line to run test
@RunWith(AndroidJUnit4::class)
class PlayStoreTopLargePictureUtil {

    companion object {
        private const val PICTURE_WIDTH = 1024
        private const val PICTURE_HEIGHT = 500

        private const val ICON_SIZE = 500
        private val ICON_RES = R.mipmap.ic_launcher_round
        private val BG_COLOR_RES = com.dede.basic.R.color.system_accent3_800
    }

    @Test
    fun generate() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap =
            Bitmap.createBitmap(PICTURE_WIDTH, PICTURE_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(ContextCompat.getColor(context, BG_COLOR_RES))
        val drawable = context.requireDrawable(ICON_RES)
        drawable.setBounds(
            (PICTURE_WIDTH / 2f - ICON_SIZE / 2f).roundToInt(),
            (PICTURE_HEIGHT / 2f - ICON_SIZE / 2f).roundToInt(),
            (PICTURE_WIDTH / 2f + ICON_SIZE / 2f).roundToInt(),
            (PICTURE_HEIGHT / 2f + ICON_SIZE / 2f).roundToInt(),
        )
        drawable.draw(canvas)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        canvas.setBitmap(null)
        bitmap.recycle()
        val byteArray = stream.toByteArray()

        EasterEggsServer.disposable(context, "/play_store_top_large_picture.jpeg") {
            NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK, "image/jpeg",
                ByteArrayInputStream(byteArray),
                byteArray.size.toLong()
            )
        }
    }
}