package com.android_l.egg.preview

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import com.dede.basic.dp
import com.dede.basic.provider.SnapshotProvider
import kotlin.math.max

class SnapshotProvider : SnapshotProvider() {

    override fun create(context: Context): View {
        return Torso(context)
    }

    private class Torso(context: Context) : FrameLayout(context), Runnable, View.OnClickListener {

        private val minTorsoSize = 10.dp

        init {
            for (i in 0..1) {
                val v = View(context)
                v.setBackgroundColor(if (i % 2 == 0) Color.BLUE else Color.RED)
                addView(v)
            }
            post(this)
            setOnClickListener(this)
        }

        override fun onClick(v: View) {
            post(this)
        }

        override fun run() {
            val parentw = measuredWidth.toFloat()
            val parenth = measuredHeight.toFloat()
            for (i in 0..<childCount) {
                val v = getChildAt(i)
                val w = max((Math.random() * parentw).toInt(), minTorsoSize)
                val h = max((Math.random() * parenth).toInt(), minTorsoSize)
                v.layoutParams = LayoutParams(w, h)

                v.x = Math.random().toFloat() * (parentw - w)
                v.y = Math.random().toFloat() * (parenth - h)
            }
        }
    }
}
