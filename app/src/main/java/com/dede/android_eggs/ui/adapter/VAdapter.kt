package com.dede.android_eggs.ui.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Constructor
import java.util.*
import kotlin.reflect.KClass

class VAdapter(
    list: List<VType> = emptyList(),
    setup: VAdapter.() -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list: List<VType>
    private val viewTypeMapping = SparseArray<Mapping>()
    val headerFooterExt = HeaderFooterExt(this)
    var onBindViewHolder: ((holder: VHolder<VType>, vType: VType) -> Unit)? = null

    private class Mapping(
        val layoutRes: Int,
        val vhClass: Class<out VHolder<out VType>>,
    )

    init {
        this.list = list
        setup.invoke(this)
    }

    fun addViewType(
        @LayoutRes layoutRes: Int,
        viewType: Int,
        vhClass: KClass<out VHolder<out VType>>,
    ) {
        addViewType(layoutRes, viewType, vhClass.java)
    }

    fun addViewType(
        @LayoutRes layoutRes: Int,
        viewType: Int,
        vhClass: Class<out VHolder<out VType>>,
    ) {
        if (viewTypeMapping[viewType] != null) {
            throw IllegalArgumentException("viewType: (%d) is added!".format(viewType))
        }
        val mapping = Mapping(layoutRes, vhClass)
        viewTypeMapping.put(viewType, mapping)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = headerFooterExt.createViewHolder(parent, viewType)
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

        @Suppress("UNCHECKED_CAST")
        val vHolder = holder as VHolder<VType>
        onBindViewHolder?.invoke(vHolder, vType)
        vHolder.onBindViewHolder(vType)
    }

    override fun getItemViewType(position: Int): Int {
        return headerFooterExt.getViewType(list, position)
    }
}

interface VType {
    val viewType: Int
}

abstract class VHolder<T : VType>(view: View) : RecyclerView.ViewHolder(view) {

    companion object {

        private fun ViewGroup.inflater(layoutRes: Int): View {
            val layoutInflater = LayoutInflater.from(context)
            return layoutInflater.inflate(layoutRes, this, false)
        }

        private val constructorCache =
            WeakHashMap<Class<out VHolder<out VType>>, Constructor<out VHolder<out VType>>>()

        fun createViewHolder(
            parent: ViewGroup,
            clazz: Class<out VHolder<out VType>>,
            layoutRes: Int,
        ): VHolder<out VType> {
            var constructor = constructorCache[clazz]
            if (constructor == null) {
                constructor = clazz.getConstructor(View::class.java)
                constructor!!.isAccessible = true
                constructorCache[clazz] = constructor
            }
            return constructor.newInstance(parent.inflater(layoutRes))
        }
    }

    fun <T : View> findViewById(id: Int): T {
        return itemView.findViewById(id)
    }

    open fun onBindViewHolder(t: T) {
    }
}