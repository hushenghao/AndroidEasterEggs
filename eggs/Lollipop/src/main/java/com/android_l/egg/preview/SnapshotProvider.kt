package com.android_l.egg.preview

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import com.dede.basic.provider.SnapshotProvider

class SnapshotProvider : SnapshotProvider() {

    override fun create(context: Context): View {
        return Torso(context)
    }

    private class Torso(context: Context) : FrameLayout(context), Runnable, View.OnClickListener {

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
                val w = (Math.random() * parentw).toInt()
                val h = (Math.random() * parenth).toInt()
                v.layoutParams = LayoutParams(w, h)

                v.x = Math.random().toFloat() * (parentw - w)
                v.y = Math.random().toFloat() * (parenth - h)
            }
        }
    }
}
