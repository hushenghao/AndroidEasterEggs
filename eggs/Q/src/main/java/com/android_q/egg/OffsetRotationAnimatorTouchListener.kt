package com.android_q.egg

import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.View
import androidx.core.view.HapticFeedbackConstantsCompat

/**
 * Fix the issue of rotation animation not working.
 */
internal class OffsetRotationAnimatorTouchListener(
    private val offset: Float = 0f,
    private val testOverlap: Runnable
) : View.OnTouchListener {

    private var mOffsetX: Float = 0f
    private var mOffsetY: Float = 0f
    private var mClickTime: Long = 0
    private var mRotAnim: ObjectAnimator? = null

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                v.animate().scaleX(1.1f).scaleY(1.1f)
                v.parent.bringChildToFront(v)
                mOffsetX = event.rawX - v.x
                mOffsetY = event.rawY - v.y
                val now = System.currentTimeMillis()
                if (now - mClickTime < 350) {
                    mRotAnim?.cancel()
                    mRotAnim = ObjectAnimator.ofFloat(
                        v, View.ROTATION,
                        v.rotation, v.rotation + 3600 + offset
                    )
                    mRotAnim!!.setDuration(10000)
                    mRotAnim!!.start()
                    mClickTime = 0
                } else {
                    mClickTime = now
                }
            }
            MotionEvent.ACTION_MOVE -> {
                v.x = event.rawX - mOffsetX
                v.y = event.rawY - mOffsetY
                v.performHapticFeedback(HapticFeedbackConstantsCompat.TEXT_HANDLE_MOVE)
            }
            MotionEvent.ACTION_UP -> {
                v.performClick()
                v.animate().scaleX(1f).scaleY(1f)
                if (mRotAnim != null) mRotAnim!!.cancel()
                testOverlap.run()
            }
            MotionEvent.ACTION_CANCEL -> {
                v.animate().scaleX(1f).scaleY(1f)
                if (mRotAnim != null) mRotAnim!!.cancel()
                testOverlap.run()
            }
        }
        return true
    }
}
