package com.dede.android_eggs.fake_test

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.Size
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dede.android_eggs.ui.drawables.ScaleType
import com.dede.android_eggs.ui.drawables.ScaleTypeDrawable
import com.dede.basic.dpf
import fi.iki.elonen.NanoHTTPD
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Generate Grid Screenshot picture.
 *
 * @author shhu
 * @since 2023/6/19
 */
@Ignore("Generate Grid Screenshot picture") // remove this line to run test
@RunWith(AndroidJUnit4::class)
class GridScreenshotUtil {

    companion object {
        // Pixel 6
        private val TARGET_SIZE = Size(1080, 2400)
        private val GRIDS = listOf(
            Grid(1, 600),
            Grid(3, 360),
            Grid(3, 360),
            Grid(3, 360),
            Grid(3, 360),
            Grid(2, 360),
        )
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 2.dpf
        style = Paint.Style.STROKE
    }

    private data class Grid(val row: Int, val height: Int)

    private fun getGridBitmap(
        context: Context,
        screenshot: String,
        targetWidth: Int,
        targetHeight: Int,
    ): Bitmap {
        val delegate = context.assets.open("screenshots/$screenshot").use {
            BitmapDrawable(context.resources, it)
        }
        val drawable = ScaleTypeDrawable(delegate, ScaleType.CENTER_CROP).apply {
            setBounds(0, 0, targetWidth, targetHeight)
        }
        return createBitmap(targetWidth, targetHeight).applyCanvas {
            drawable.draw(this)
            drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), paint)
        }
    }

    @Test
    fun generate() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = createBitmap(TARGET_SIZE.width, TARGET_SIZE.height)
        val screenshots = context.assets.list("screenshots")!!.toList().reversed()
        bitmap.applyCanvas {
            var snapshotIndex = 0
            var top = 0
            out@ for (grid in GRIDS) {
                val width = TARGET_SIZE.width / grid.row
                val height = grid.height
                for (i in 0 until grid.row) {
                    val snapshot = screenshots.getOrNull(snapshotIndex++) ?: break@out
                    val snapshotBitmap = getGridBitmap(context, snapshot, width, height)
                    drawBitmap(snapshotBitmap, i * width.toFloat(), top.toFloat(), paint)
                }
                top += height
            }
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        bitmap.recycle()
        val byteArray = stream.toByteArray()

        EasterEggsServer.disposable(context, "/ic_grid_screenshot.jpeg", 60 * 1000L) {
            NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK, "image/jpeg",
                ByteArrayInputStream(byteArray),
                byteArray.size.toLong()
            )
        }
    }
}