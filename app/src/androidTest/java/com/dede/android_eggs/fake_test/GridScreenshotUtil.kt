package com.dede.android_eggs.fake_test

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.Size
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dede.android_eggs.fake_test.EasterEggsServer.Companion.registerHandler
import com.dede.android_eggs.ui.drawables.ScaleType
import com.dede.android_eggs.ui.drawables.ScaleTypeDrawable
import com.dede.basic.dpf
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.math.min

/**
 * Generate Grid Screenshot picture.
 *
 * @author shhu
 * @since 2023/6/19
 */
@org.junit.Ignore("Generate Grid Screenshot picture") // remove this line to run test
@RunWith(AndroidJUnit4::class)
class GridScreenshotUtil {

    companion object {
        // Pixel 6
        private val TARGET_SIZE = Size(1080, 2400)

        private val GROUPS = listOf<Group>(
            Split(510, 1),                // 1
            Triple(540, true, 4),  // 3
            Split(270, 4),                // 4
            Pentad(540, false),            // 5
            Triple(540, true, 4),   // 3
        )

        private const val ASSET_DIR = "screenshots"
    }

    interface Group {

        val height: Int

        fun convertBounds(width: Int, top: Int): List<Rect>
    }

    interface Reversal {
        val reverse: Boolean
    }

    interface Column {
        val column: Int
    }

    /**
     * --------------
     * |        | 2 |
     * |   1    |---|
     * |        | 3 |
     * --------------
     */
    private class Triple(
        override val height: Int,
        override val reverse: Boolean,
        override val column: Int = 3,
    ) : Group, Reversal, Column {
        override fun convertBounds(width: Int, top: Int): List<Rect> {
            val halfHeight = height / 2
            return if (reverse) {
                val split = width / column
                listOf(
                    Rect(split, top, width, top + height),
                    Rect(0, top, split, top + halfHeight),
                    Rect(0, top + halfHeight, split, top + height),
                )
            } else {
                val split = (width * (column - 1) / column)
                listOf(
                    Rect(0, top, split, top + height),
                    Rect(split, top, width, top + halfHeight),
                    Rect(split, top + halfHeight, width, top + height),
                )
            }
        }
    }

    /**
     * -------------
     * | 1 |...| n |
     * -------------
     */
    private class Split(override val height: Int, override val column: Int = 3) : Group, Column {
        override fun convertBounds(width: Int, top: Int): List<Rect> {
            val w = width / column
            return (0 until column).map {
                Rect(w * it, top, w * (it + 1), top + height)
            }
        }
    }

    /**
     * ------------------
     * |            | 2 |
     * |            |---|
     * |      1     | 3 |
     * |            |---|
     * |            | 4 |
     * ------------------
     */
    private class Tetrad(
        override val height: Int,
        override val reverse: Boolean,
        override val column: Int = 4,
    ) : Group, Reversal, Column {
        override fun convertBounds(width: Int, top: Int): List<Rect> {
            val splitH = height / 3
            return if (reverse) {
                val end = width / column
                listOf(
                    Rect(end, top, width, top + height),
                    Rect(0, top, end, top + splitH),
                    Rect(0, top + splitH, end, top + splitH * 2),
                    Rect(0, top + splitH * 2, end, top + height),
                )
            } else {
                val end = (width * (column - 1) / column)
                listOf(
                    Rect(0, top, end, top + height),
                    Rect(end, top, width, top + splitH),
                    Rect(end, top + splitH, width, top + splitH * 2),
                    Rect(end, top + splitH * 2, width, top + height),
                )
            }
        }
    }

    /**
     * ------------------
     * |        | 2 | 3 |
     * |   1    |---|---|
     * |        | 4 | 5 |
     * ------------------
     */
    private class Pentad(
        override val height: Int,
        override val reverse: Boolean,
    ) : Group, Reversal {
        override fun convertBounds(width: Int, top: Int): List<Rect> {
            val halfW = width / 2
            val quarter = width / 4
            val halfH = height / 2
            return if (reverse) {
                listOf(
                    Rect(halfW, top, width, top + height),
                    Rect(0, top, quarter, top + halfH),
                    Rect(quarter, top, halfW, top + halfH),
                    Rect(0, top + halfH, quarter, top + height),
                    Rect(quarter, top + halfH, halfW, top + height),
                )
            } else {
                listOf(
                    Rect(0, top, halfW, top + height),
                    Rect(halfW, top, halfW + quarter, top + halfH),
                    Rect(halfW + quarter, top, width, top + halfH),
                    Rect(halfW, top + halfH, halfW + quarter, top + height),
                    Rect(halfW + quarter, top + halfH, width, top + height),
                )
            }
        }

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 2.dpf
        style = Paint.Style.STROKE
    }

    private fun cropScreenshot(
        context: Context,
        screenshot: String,
        targetWidth: Int = -1,
        targetHeight: Int = -1,
        drawBorder: Boolean = false,
    ): Bitmap {
        val delegate = context.assets.open("$ASSET_DIR/$screenshot").use {
            BitmapDrawable(context.resources, it)
        }
        var width = targetWidth
        var height = targetHeight
        if (width <= 0 || height <= 0) {
            val size = min(delegate.bitmap.width, delegate.bitmap.height)
            width = size
            height = size
        }
        val drawable = ScaleTypeDrawable(delegate, ScaleType.CENTER_CROP).apply {
            setBounds(0, 0, width, height)
        }
        return createBitmap(width, height).applyCanvas {
            drawable.draw(this)
            if (drawBorder) {
                drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
        }
    }

    private fun createGridScreenshot(context: Context, screenshots: List<String>): Bitmap {
        return createBitmap(TARGET_SIZE.width, TARGET_SIZE.height).applyCanvas {
            var top = 0
            var index = 0
            out@ for (group in GROUPS) {
                val bounds = group.convertBounds(TARGET_SIZE.width, top)
                for (rect in bounds) {
                    val snapshot = screenshots.getOrNull(index++) ?: break@out
                    val cropBitmap =
                        cropScreenshot(context, snapshot, rect.width(), rect.height(), true)
                    drawBitmap(cropBitmap, rect.left.toFloat(), rect.top.toFloat(), paint)
                }
                top += group.height
            }
        }
    }

    @Test
    fun generate() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val screenshots =
            requireNotNull(context.assets.list(ASSET_DIR)).reversed() as MutableList<String>
        // 让g显示在最后一个
        val g = screenshots.removeAt(screenshots.size - 1)
        screenshots.add(screenshots.size - 2, g)
        EasterEggsServer.start(context) {
            for (screenshot in screenshots) {
                registerHandler("/$screenshot") {
                    cropScreenshot(context, screenshot).toResponse()
                }
            }
            registerHandler("/ic_grid_screenshot.jpeg") {
                createGridScreenshot(context, screenshots).toResponse()
            }
        }
    }

    private fun Bitmap.toResponse(): Response {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        recycle()
        val byteArray = stream.toByteArray()
        return NanoHTTPD.newFixedLengthResponse(
            Response.Status.OK, "image/jpeg",
            ByteArrayInputStream(byteArray),
            byteArray.size.toLong()
        )
    }
}