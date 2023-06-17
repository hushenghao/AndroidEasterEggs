package com.dede.android_eggs.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.dede.android_eggs.R

class HorizontalSwipeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    interface OnSwipeListener {
        fun onSwipeCaptured(capturedChild: View) {}
        fun onSwipeReleased(releasedChild: View) {}
        fun onSwipePositionChanged(changedView: View, left: Int, dx: Int) {}
    }

    private val viewDragHelper: ViewDragHelper
    private var swipeView: View? = null
    var swipeListener: OnSwipeListener? = null

    init {
        viewDragHelper = ViewDragHelper.create(this, 1f, HorizontalSwipeCallback())
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

    private inner class HorizontalSwipeCallback : ViewDragHelper.Callback() {

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
            val params = releasedChild.layoutParams as MarginLayoutParams
            viewDragHelper.settleCapturedViewAt(params.leftMargin, params.topMargin)
            invalidate()
            swipeListener?.onSwipeReleased(releasedChild)
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return isSwipeView(child)
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return if (isSwipeView(child)) child.width else 0
        }
    }
}