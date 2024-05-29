package com.dede.android_eggs.fake_test

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dede.android_eggs.R
import com.dede.basic.createThemeWrapperContext
import com.dede.basic.requireDrawable
import fi.iki.elonen.NanoHTTPD
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Generate ClockAnalog simple dial img
 */
@Ignore("Generate Play Store top large picture") // remove this line to run test
@RunWith(AndroidJUnit4::class)
class ClockAnalogSimpleDialUtil {

    companion object {
        private const val IMAGE_SIZE = 380
    }

    @Test
    fun generate() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val wrapperContext = context.createThemeWrapperContext()
        val drawable = wrapperContext.requireDrawable(R.drawable.clock_analog_simple_dial)
        var bitmap = drawable.toBitmap()
        bitmap = bitmap.scale(IMAGE_SIZE, IMAGE_SIZE)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 75, stream)
        bitmap.recycle()
        val byteArray = stream.toByteArray()

        EasterEggsServer.disposable(context, "/clock_analog_simple_dial.webp") {
            NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK, "image/webp",
                ByteArrayInputStream(byteArray),
                byteArray.size.toLong()
            )
        }
    }
}