package com.android_l.egg.preview

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

interface RefreshTorso {
    fun refresh(v: View, w: Int, h: Int, x: Float, y: Float)
}

object RefreshTorsoImpls {

    @JvmStatic
    fun get(animate: Boolean): RefreshTorso {
        return if (animate) Animate else Default
    }

}

private object Animate : RefreshTorso {

    private const val DURATION = 200L

    override fun refresh(v: View, w: Int, h: Int, x: Float, y: Float) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        val sw = v.width
        val sh = v.height
        val sx = v.x
        val sy = v.y
        animator.addUpdateListener { animation ->
            val av = animation.animatedValue as Float
            v.x = sx + (x - sx) * av
            v.y = sy + (y - sy) * av
            val lp = v.layoutParams ?: ViewGroup.LayoutParams(0, 0)
            lp.width = (sw + (w - sw) * av).toInt()
            lp.height = (sh + (h - sh) * av).toInt()
            v.layoutParams = lp
        }
        animator.setDuration(DURATION)
        animator.start()
    }
}

private object Default : RefreshTorso {
    override fun refresh(v: View, w: Int, h: Int, x: Float, y: Float) {
        v.setLayoutParams(FrameLayout.LayoutParams(w, h))
        v.x = x;
        v.y = y;
    }
}

