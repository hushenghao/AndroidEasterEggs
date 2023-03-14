package com.dede.android_eggs.ui.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private class Mapping(
        val layoutRes: Int,
        val viewHolderClass: Class<out VHolder<out VType>>,
    )

    init {
        this.list = list
        setup.invoke(this)
    }

    fun addViewType(layoutRes: Int, viewType: Int, viewHolderClass: KClass<out VHolder<out VType>>) {
        addViewType(layoutRes, viewType, viewHolderClass.java)
    }

    fun addViewType(layoutRes: Int, viewType: Int, viewHolderClass: Class<out VHolder<out VType>>) {
        if (viewTypeMapping[viewType] != null) {
            throw IllegalArgumentException("viewType: (%d) is added!".format(viewType))
        }
        val mapping = Mapping(layoutRes, viewHolderClass)
        viewTypeMapping.put(viewType, mapping)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mapping = viewTypeMapping[viewType]
        return VHolder.createViewHolder(parent, mapping.viewHolderClass, mapping.layoutRes)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        (holder as VHolder<VType>).onBindViewHolder(list[position])
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
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

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    open fun onBindViewHolder(t: T) {
    }
}