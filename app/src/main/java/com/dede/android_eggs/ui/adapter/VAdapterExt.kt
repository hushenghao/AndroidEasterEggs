package com.dede.android_eggs.ui.adapter

import android.view.View
import androidx.annotation.LayoutRes
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class VHType(val viewType: Int = 0)

inline fun <reified VH : VHolder<out Any>> VAdapter.addViewType(
    @LayoutRes layoutRes: Int,
    viewType: Int,
) {
    addViewType(layoutRes, viewType, VH::class.java)
}

inline fun <reified VH : VHolder<out Any>> VAdapter.addViewType(@LayoutRes layoutRes: Int) {
    val vhClass = VH::class.java
    val vhType = vhClass.getAnnotation(VHType::class.java)
        ?: throw IllegalArgumentException(
            "%s must declaration ViewType annotation!".format(vhClass.name)
        )
    addViewType(layoutRes, vhType.viewType, vhClass)
}


private class VHolderImpl(view: View) : VHolder<Any>(view)

fun <T : Any> VAdapter(
    @LayoutRes layoutRes: Int,
    list: List<T>,
    onBindView: BindViewHolder<T>,
): VAdapter {
    return VAdapter(list) {
        addViewType<VHolderImpl>(layoutRes, 0)
        onBindViewHolder = { vHolder: VHolder<Any>, vType: Any ->
            @Suppress("UNCHECKED_CAST") val holder = vHolder as VHolder<T>
            @Suppress("UNCHECKED_CAST") val data = vType as T
            onBindView(holder, data)
        }
    }
}