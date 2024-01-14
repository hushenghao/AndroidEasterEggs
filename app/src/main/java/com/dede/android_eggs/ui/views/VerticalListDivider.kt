package com.dede.android_eggs.ui.views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dede.android_eggs.util.EdgeUtils.onApplyWindowEdge
import java.lang.ref.WeakReference


fun RecyclerView.applyVerticalWindowInsetsPadding(
    divider: Int = 0,
    applyTop: Boolean = false,
    applyBottom: Boolean = false
) {
    val listDivider = VerticalListDivider(divider, 0, 0)
    this.onApplyWindowEdge {
        if (applyTop) {
            listDivider.setTopInset(it.top)
        }
        if (applyBottom) {
            listDivider.setBottomInset(it.bottom)
        }
    }
    this.addItemDecoration(listDivider)
}

class VerticalListDivider(
    private val divider: Int,
    private var topInset: Int,
    private var bottomInset: Int,
) : RecyclerView.ItemDecoration() {

    private var listRef: WeakReference<RecyclerView>? = null

    fun setTopInset(inset: Int) {
        if (inset == this.topInset) return
        this.topInset = inset
        listRef?.get()?.requestLayout()
    }

    fun setBottomInset(inset: Int) {
        if (inset == this.bottomInset) return
        this.bottomInset = inset
        listRef?.get()?.requestLayout()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        if (listRef == null) {
            listRef = WeakReference(parent)
        }
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.top = divider + topInset
        }
        outRect.bottom = divider

        val adapter = parent.adapter ?: return
        if (position == adapter.itemCount - 1) {
            outRect.bottom = divider + bottomInset
        }
    }

}