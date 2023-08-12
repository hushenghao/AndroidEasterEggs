package com.dede.android_eggs.main.holders

import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.drawable.toBitmap
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Egg.Companion.getIcon
import com.dede.android_eggs.main.entity.EggGroup
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.updateCompoundDrawablesRelative
import com.dede.basic.dp

@VHType(viewType = Egg.VIEW_TYPE_EGG_GROUP)
class GroupHolder(view: View) : VHolder<EggGroup>(view) {

    private val delegate = EggHolder(view)
    private val binding: ItemEasterEggLayoutBinding = delegate.binding

    override fun onBindViewHolder(eggGroup: EggGroup) {
        delegate.onBindViewHolder(eggGroup.selectedEgg)
        binding.tvSummary.updateCompoundDrawablesRelative(
            end = FontIconsDrawable(context, Icons.Rounded.arrow_drop_down, 22f)
        )
        binding.tvSummary.setOnClickListener {
            val vAdapter = this.vAdapter ?: return@setOnClickListener
            val popupMenu = PopupMenu(context, binding.tvSummary)
            popupMenu.setForceShowIcon(true)
            var order = eggGroup.child.size
            for (egg in eggGroup.child) {
                popupMenu.menu.add(0, egg.androidRes, order--, egg.androidRes).apply {
                    icon = BitmapDrawable(
                        context.resources,
                        egg.getIcon(context).toBitmap(36.dp, 36.dp)
                    )
                }
            }
            popupMenu.setOnMenuItemClickListener {
                val index = eggGroup.child.indexOfFirst { egg ->
                    egg.androidRes == it.itemId
                }
                if (index != -1) {
                    if (index != eggGroup.selectedIndex) {
                        eggGroup.selectedIndex = index
                        vAdapter.notifyItemChanged(bindingAdapterPosition)
                    }
                } else {
                    throw IllegalArgumentException("Menu id: ${it.itemId}, title: ${it.title}, child: ${eggGroup.child.joinToString()}")
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }
    }

}