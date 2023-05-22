package com.dede.android_eggs.main

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.PathParser
import androidx.lifecycle.LifecycleOwner
import com.dede.android_eggs.R
import com.dede.android_eggs.settings.EdgePref
import com.dede.android_eggs.util.LocalEvent
import com.dede.basic.dp
import com.google.android.material.color.MaterialColors
import kotlin.random.Random
import com.google.android.material.R as M3R

/**
 * Split Placeholder
 *
 * @author shhu
 * @since 2023/5/22
 */
class PlaceholderActivity : AppCompatActivity(R.layout.activity_placeholder) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EdgePref.applyEdge(this, window)
        LocalEvent.get(this as LifecycleOwner).register(EdgePref.ACTION_CHANGED) {
            recreate()
        }
        findViewById<ImageView>(R.id.iv_icon)
            .setImageBitmap(makePathBitmap())
    }

    private fun makePathBitmap(): Bitmap? {
        val array = resources.getStringArray(R.array.icon_shape_override_paths)
        // 排除前两个
        val index = Random.nextInt(array.size - 2) + 2
        val path = PathParser.createPathFromPathData(array[index])
        val size = 56.dp
        val matrix = Matrix()
        matrix.setScale(size / 100f, size / 100f)
        path.transform(matrix)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = MaterialColors.getColor(this, M3R.attr.colorAccent, Color.WHITE)
        canvas.drawPath(path, paint)
        canvas.setBitmap(null)
        return bitmap
    }
}