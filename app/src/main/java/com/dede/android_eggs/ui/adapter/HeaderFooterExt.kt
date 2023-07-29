package com.dede.android_eggs.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dede.android_eggs.R
import com.google.android.material.carousel.CarouselLayoutManager


const val TYPE_HEADER = Int.MIN_VALUE
const val TYPE_FOOTER = Int.MAX_VALUE


fun VAdapter.addFooter(view: View) {
    headerFooterExt.addFooter(view)
}

fun VAdapter.removeFooter(view: View) {
    headerFooterExt.removeFooter(view)
}

fun VAdapter.addHeader(view: View) {
    headerFooterExt.addHeader(view)
}

fun VAdapter.removeHeader(view: View) {
    headerFooterExt.removeHeader(view)
}

class HeaderFooterExt(private val vAdapter: VAdapter) {

    private lateinit var headerView: LinearLayout
    private lateinit var footerView: LinearLayout

    private val hasHeader: Boolean
        get() = ::headerView.isInitialized && headerView.childCount > 0

    private val hasFooter: Boolean
        get() = ::footerView.isInitialized && footerView.childCount > 0

    fun addHeader(view: View) {
        if (!::headerView.isInitialized) {
            headerView = LinearLayout(view.context)
        }
        val notify = !hasHeader
        val parent = view.parent
        if (parent is ViewGroup) {
            parent.removeView(view)
        }
        val params = view.layoutParams
        if (params != null) {
            view.setTag(R.id.adapter_hf_lp, params)
        }
        headerView.addView(view)
        if (notify) vAdapter.notifyItemInserted(0)
    }

    fun removeHeader(view: View) {
        if (hasHeader) {
            headerView.removeView(view)
            view.setTag(R.id.adapter_hf_lp, null)
            if (!hasHeader) vAdapter.notifyItemRemoved(0)
        }
    }

    fun addFooter(view: View) {
        if (!::footerView.isInitialized) {
            footerView = LinearLayout(view.context)
        }
        val notify = !hasFooter
        val parent = view.parent
        if (parent is ViewGroup) {
            parent.removeView(view)
        }
        val params = view.layoutParams
        if (params != null) {
            view.setTag(R.id.adapter_hf_lp, params)
        }
        footerView.addView(view)
        if (notify) vAdapter.notifyItemInserted(vAdapter.itemCount - 1)
    }

    fun removeFooter(view: View) {
        if (hasFooter) {
            footerView.removeView(view)
            view.setTag(R.id.adapter_hf_lp, null)
            if (!hasFooter) vAdapter.notifyItemRemoved(vAdapter.itemCount - 1)
        }
    }

    fun getItemCount(list: List<*>): Int {
        var offset = 0
        if (hasHeader) offset++
        if (hasFooter) offset++
        return list.size + offset
    }

    fun calculatePosition(position: Int): Int {
        return if (hasHeader) position - 1 else position
    }

    fun getViewType(list: List<*>, position: Int): Int {
        if (hasHeader && position == 0) {
            return TYPE_HEADER
        }
        if (hasFooter && position == vAdapter.itemCount - 1) {
            return TYPE_FOOTER
        }
        val p = calculatePosition(position)
        val data = list[p]
        return if (data is VType) data.viewType else 0
    }

    fun bindViewHolder(holder: ViewHolder): Boolean {
        return when (holder.itemViewType) {
            TYPE_HEADER,
            TYPE_FOOTER,
            -> true

            else -> false
        }
    }

    fun createViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        return when (viewType) {
            TYPE_HEADER -> HeadFootHolder(setupView(headerView, parent))
            TYPE_FOOTER -> HeadFootHolder(setupView(footerView, parent))
            else -> null
        }
    }

    private fun setupView(view: LinearLayout, parent: ViewGroup): ViewGroup {
        val orientation = getOrientation(parent)
        view.layoutParams = if (orientation == LinearLayout.VERTICAL) {
            RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        } else {
            RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        }
        view.orientation = orientation
        for (child in view.children) {
            val savedParams = child.getTag(R.id.adapter_hf_lp)
            if (savedParams != null) {
                continue
            }
            val params = if (orientation == LinearLayout.VERTICAL) {
                LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            } else {
                LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            }
            child.layoutParams = params
        }
        return view
    }

    private fun getOrientation(parent: ViewGroup): Int {
        when (val manager = (parent as? RecyclerView)?.layoutManager) {
            is LinearLayoutManager -> manager.orientation
            is StaggeredGridLayoutManager -> manager.orientation
            is CarouselLayoutManager -> LinearLayout.HORIZONTAL
        }
        return LinearLayout.VERTICAL
    }

    private class HeadFootHolder(view: View) : ViewHolder(view) {
        init {
            setIsRecyclable(false)
        }
    }
}

