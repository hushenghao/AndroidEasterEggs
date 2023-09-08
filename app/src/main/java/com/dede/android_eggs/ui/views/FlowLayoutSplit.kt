package com.dede.android_eggs.ui.views

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.get
import com.google.android.material.internal.FlowLayout

class FlowLayoutSplit(context: Context) : LinearLayout(context) {

    companion object {

        fun forEachUnwrapChild(flowLayout: FlowLayout, block: (child: View) -> Unit) {
            for (i in 0..<flowLayout.childCount) {
                var view = flowLayout[i]
                if (view is FlowLayoutSplit) {
                    view = view.getWrapView() ?: view
                }
                block.invoke(view)
            }
        }

        fun join(flowLayout: FlowLayout, provider: FlowSplitViewProvider) {
            val childCount = flowLayout.childCount
            if (childCount < 2) return
            val context = flowLayout.context
            for (i in 0..<childCount - 1) {// skip last view
                val view = flowLayout[i]
                val wrapView = FlowLayoutSplit(context)
                wrapView.addWrapView(view)// auto remove from flowlayout
                wrapView.addView(provider.createSplitView(context, wrapView))
                flowLayout.addView(wrapView, i)
            }
        }
    }

    interface FlowSplitViewProvider {

        fun createSplitView(context: Context, parent: ViewGroup): View
    }

    private var wrapView: View? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    fun getWrapView(): View? {
        return wrapView
    }

    fun addWrapView(view: View) {
        wrapView = view
        (view.parent as? ViewGroup)?.removeView(view)
        addView(view)
    }
}