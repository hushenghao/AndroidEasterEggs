package com.dede.android_eggs.ui.adapter

import android.view.View
import androidx.annotation.LayoutRes

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class VHType(val viewType: Int = 0)

inline fun <reified VH : VHolder<out VType>> VAdapter.addViewType(
    @LayoutRes layoutRes: Int,
    viewType: Int,
) {
    addViewType(layoutRes, viewType, VH::class.java)
}

inline fun <reified VH : VHolder<out VType>> VAdapter.addViewType(@LayoutRes layoutRes: Int) {
    val vhClass = VH::class.java
    val vhType = vhClass.getAnnotation(VHType::class.java)
        ?: throw IllegalArgumentException(
            "%s must declaration ViewType annotation!".format(vhClass.name)
        )
    addViewType(layoutRes, vhType.viewType, vhClass)
}


private class VHolderImpl<T : VType>(view: View) : VHolder<T>(view)
private class VTypeImpl<T>(val impl: T, type: Int) : VType {
    override val viewType: Int = type
}

fun <T> VAdapter(
    @LayoutRes layoutRes: Int,
    list: List<T>,
    onBindView: (holder: VHolder<VType>, t: T) -> Unit,
): VAdapter {
    return VAdapter(list.map { VTypeImpl(it, 0) }) {
        addViewType(layoutRes, 0, VHolderImpl::class)
        onBindViewHolder = { holder, vType ->
            @Suppress("UNCHECKED_CAST")
            val vTypeImpl = vType as VTypeImpl<T>
            onBindView(holder, vTypeImpl.impl)
        }
    }
}