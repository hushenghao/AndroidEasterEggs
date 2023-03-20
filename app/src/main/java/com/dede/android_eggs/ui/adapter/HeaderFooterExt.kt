package com.dede.android_eggs.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

    private lateinit var headerView: ViewGroup
    private lateinit var footerView: ViewGroup

    private val headers = ArrayList<View>()
    private val footers = ArrayList<View>()

    private val hasHeader: Boolean
        get() = headers.size > 0 || (::headerView.isInitialized && headerView.childCount > 0)

    private val hasFooter: Boolean
        get() = footers.size > 0 || (::footerView.isInitialized && footerView.childCount > 0)

    fun addHeader(view: View) {
        if (::headerView.isInitialized) {
            val notify = !hasHeader
            headerView.addView(view)
            if (notify) vAdapter.notifyItemInserted(0)
        } else {
            headers.add(view)
        }
    }

    fun removeHeader(view: View) {
        if (::headerView.isInitialized) {
            headerView.removeView(view)
            if (!hasHeader) vAdapter.notifyItemRemoved(0)
        } else {
            headers.remove(view)
        }
    }

    fun addFooter(view: View) {
        if (::footerView.isInitialized) {
            val notify = !hasFooter
            footerView.addView(view)
            if (notify) vAdapter.notifyItemInserted(vAdapter.itemCount - 1)
        } else {
            footers.add(view)
        }
    }

    fun removeFooter(view: View) {
        if (::footerView.isInitialized) {
            footerView.removeView(view)
            if (!hasFooter) vAdapter.notifyItemRemoved(vAdapter.itemCount - 1)
        } else {
            footers.remove(view)
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
        when (holder.itemViewType) {
            TYPE_HEADER -> {
                val headerHolder = (holder as HeadFootHolder)
                for (header in headers) {
                    headerHolder.group.addView(header)
                }
                headers.clear()
                headerView = headerHolder.group
            }
            TYPE_FOOTER -> {
                val footerHolder = (holder as HeadFootHolder)
                for (footer in footers) {
                    footerHolder.group.addView(footer)
                }
                footers.clear()
                footerView = footerHolder.group
            }
            else -> return false
        }
        return true
    }

    fun createViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        return when (viewType) {
            TYPE_HEADER,
            TYPE_FOOTER -> {
                HeadFootHolder(createView(parent)).apply {
                    setIsRecyclable(false)
                }
            }
            else -> null
        }
    }

    private fun createView(parent: ViewGroup): ViewGroup {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_header_footer_group, parent, false) as LinearLayout
        view.orientation = getOrientation(parent)
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
        val group: ViewGroup = itemView.findViewById(R.id.ll_group)
    }
}

