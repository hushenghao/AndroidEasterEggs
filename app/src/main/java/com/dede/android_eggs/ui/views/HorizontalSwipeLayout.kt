package com.dede.android_eggs.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.dede.android_eggs.R
import kotlin.math.max
import kotlin.math.min

class HorizontalSwipeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    interface OnSwipeListener {
        fun onSwipeCaptured(capturedChild: View) {}
        fun onSwipeReleased(releasedChild: View) {}
        fun onSwipePositionChanged(changedView: View, left: Int, dx: Int) {}
    }

    private val swipeCallback: HorizontalSwipeCallback =
        HorizontalSwipeCallback(Gravity.FILL_HORIZONTAL)
    private val viewDragHelper: ViewDragHelper = ViewDragHelper.create(this, 1f, swipeCallback)

    private var swipeView: View? = null
    var swipeListener: OnSwipeListener? = null
    var swipeGravity: Int = 0
        set(value) {
            swipeCallback.gravity =
                GravityCompat.getAbsoluteGravity(value, ViewCompat.getLayoutDirection(this))
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        swipeView = findViewWithTag(resources.getString(R.string.tag_swipe_view))
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        viewDragHelper.processTouchEvent(event)
        return true
    }

    private fun isSwipeView(child: View): Boolean {
        return child === swipeView
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private inner class HorizontalSwipeCallback(var gravity: Int) : ViewDragHelper.Callback() {

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            swipeListener?.onSwipeCaptured(capturedChild)
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int,
        ) {
            invalidate()
            swipeListener?.onSwipePositionChanged(changedView, left, dx)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            viewDragHelper.settleCapturedViewAt(getStartLeft(releasedChild), releasedChild.top)
            invalidate()
            swipeListener?.onSwipeReleased(releasedChild)
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return isSwipeView(child)
        }

        @SuppressLint("RtlHardcoded")
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            val absGravity = GravityCompat.getAbsoluteGravity(
                if (dx > 0) Gravity.RIGHT else Gravity.LEFT,
                ViewCompat.getLayoutDirection(child)
            )
            val startLeft = getStartLeft(child)
            val endLeft = getEndLeft(child)
            if (absGravity == Gravity.LEFT) {
                if (isTargetGravity(Gravity.LEFT)) {
                    return max(-endLeft, left)
                } else if (left >= startLeft) {
                    return max(left, startLeft)// slide right after , reverse slide
                }
            } else if (absGravity == Gravity.RIGHT) {
                if (isTargetGravity(Gravity.RIGHT)) {
                    return min(left, endLeft)
                } else if (left <= startLeft) {
                    return min(left, startLeft)// slide left after , reverse slide
                }
            }
            return child.left
        }

        private fun isTargetGravity(abs: Int): Boolean {
            return (gravity and abs) == abs
        }

        private fun getEndLeft(child: View): Int {
            return child.width * 3 / 5
        }

        private fun getStartLeft(child: View): Int {
            val params = child.layoutParams as MarginLayoutParams
            return width - child.width - params.leftMargin
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return if (isSwipeView(child)) child.width else 0
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return child.top
        }
    }
}