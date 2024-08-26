package com.dede.android_eggs.fake_test

import androidx.core.graphics.drawable.toBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dede.android_eggs.R
import com.dede.android_eggs.fake_test.utils.EasterEggsServer
import com.dede.android_eggs.fake_test.utils.ResponseUtils.toResponse
import com.dede.basic.createThemeWrapperContext
import com.dede.basic.requireDrawable
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generate ClockAnalog simple dial img
 */
@Ignore("Generate ClockAnalog simple dial img") // remove this line to run test
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
        val bitmap = drawable.toBitmap()

        //bitmap = bitmap.scale(IMAGE_SIZE, IMAGE_SIZE)
        // https://stackoverflow.com/questions/24745147/java-resize-image-without-losing-quality
        // replace to ffmpeg scale image
        // ffmpeg -i clock_analog_simple_dial.webp -s 380x380 -sws_flags lanczos lanczos.webp

        EasterEggsServer.disposable(context, "/clock_analog_simple_dial.webp") {
            bitmap.toResponse()
        }
    }
}