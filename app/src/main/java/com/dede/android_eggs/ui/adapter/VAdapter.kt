package com.dede.android_eggs.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Constructor
import java.util.*

typealias BindViewHolder<T> = (holder: VHolder<T>, t: T) -> Unit
typealias CreateViewHolder = (parent: ViewGroup, viewType: Int) -> RecyclerView.ViewHolder

class VAdapter(
    list: List<Any> = emptyList(),
    setup: VAdapter.() -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: List<Any>
    private val viewTypeMapping = SparseArray<Mapping>()

    val headerFooterExt = HeaderFooterExt(this)
    var onBindViewHolder: BindViewHolder<Any>? = null
    var onCreateViewHolder: CreateViewHolder? = null

    private class Mapping(
        val layoutRes: Int,
        val vhClass: Class<out VHolder<out Any>>,
    )

    init {
        this.list = list
        setup.invoke(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Any>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun append(list: List<Any>) {
        val newList = ArrayList<Any>()
        val oldSize = this.list.size
        newList.addAll(this.list)
        newList.addAll(list)
        this.list = newList
        notifyItemRangeInserted(oldSize, list.size)
    }

    fun addViewType(
        @LayoutRes layoutRes: Int,
        viewType: Int,
        vhClass: Class<out VHolder<out Any>>,
    ) {
        if (viewType == TYPE_FOOTER || viewType == TYPE_HEADER) {
            throw IllegalArgumentException("viewType: (%d) is internal used!".format(viewType))
        }
        if (viewTypeMapping[viewType] != null) {
            throw IllegalArgumentException("viewType: (%d) is added!".format(viewType))
        }
        val mapping = Mapping(layoutRes, vhClass)
        viewTypeMapping.put(viewType, mapping)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder = headerFooterExt.createViewHolder(parent, viewType)
        if (viewHolder != null) {
            return viewHolder
        }
        viewHolder = onCreateViewHolder?.invoke(parent, viewType)
        if (viewHolder != null) {
            return viewHolder
        }
        val mapping = viewTypeMapping[viewType]
        return VHolder.createViewHolder(parent, mapping.vhClass, mapping.layoutRes)
    }

    override fun getItemCount(): Int {
        return headerFooterExt.getItemCount(list)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (headerFooterExt.bindViewHolder(holder)) return

        val p = headerFooterExt.calculatePosition(position)
        val vType = list[p]

        @Suppress("UNCHECKED_CAST") val vHolder = holder as VHolder<Any>
        vHolder.onBindViewHolder(vType)
        onBindViewHolder?.invoke(vHolder, vType)
    }

    override fun getItemViewType(position: Int): Int {
        return headerFooterExt.getViewType(list, position)
    }
}

interface VType {
    val viewType: Int
}

abstract class VHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    companion object {

        private fun ViewGroup.inflater(layoutRes: Int): View {
            val layoutInflater = LayoutInflater.from(context)
            return layoutInflater.inflate(layoutRes, this, false)
        }

        private val constructorCache =
            WeakHashMap<Class<out VHolder<*>>, Constructor<out VHolder<*>>>()

        fun createViewHolder(
            parent: ViewGroup,
            clazz: Class<out VHolder<*>>,
            layoutRes: Int,
        ): VHolder<*> {
            var constructor = constructorCache[clazz]
            if (constructor == null) {
                constructor = clazz.getConstructor(View::class.java)
                constructor!!.isAccessible = true
                constructorCache[clazz] = constructor
            }
            return constructor.newInstance(parent.inflater(layoutRes))
        }
    }

    private val viewRef = SparseArray<View>()

    val context: Context get() = itemView.context

    fun <T : View> findViewById(id: Int): T {
        @Suppress("UNCHECKED_CAST") var view = viewRef.get(id) as? T
        if (view != null) return view
        view = itemView.findViewById(id)
        viewRef.put(id, view)
        return view
    }

    open fun onBindViewHolder(t: T) {
    }
}