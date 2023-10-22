package com.dede.android_eggs.main.holders

import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.View
import androidx.appcompat.view.menu.MenuPopupAccessor
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.drawable.toBitmap
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Egg.Companion.getIcon
import com.dede.android_eggs.main.entity.EggGroup
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.createOvalRipple
import com.dede.android_eggs.util.createOvalWrapper
import com.dede.android_eggs.util.resolveColor
import com.dede.android_eggs.util.updateCompoundDrawablesRelative
import com.dede.basic.dp
import kotlin.math.roundToInt
import com.google.android.material.R as M3R

@VHType(viewType = Egg.VIEW_TYPE_EGG_GROUP)
class GroupHolder(view: View) : VHolder<EggGroup>(view),
    OrientationAngleSensor.OnOrientationAnglesUpdate {

    private val delegate = EggHolder(view)
    private val binding: ItemEasterEggLayoutBinding = delegate.binding

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onBindViewHolder(eggGroup: EggGroup) {
        delegate.onBindViewHolder(eggGroup.selectedEgg)

        val drawable = createOvalRipple(
            context,
            createOvalWrapper(
                FontIconsDrawable(context, Icons.Rounded.expand_more).apply {
                    setColor(binding.tvSummary.currentTextColor)
                },
                context.resolveColor(M3R.attr.colorButtonNormal)
            )
        ).apply {
            setBounds(0, 0, 24.dp, 24.dp)
        }
        binding.tvSummary.updateCompoundDrawablesRelative(end = drawable)
        binding.tvSummary.compoundDrawablePadding = 6.dp
        binding.tvSummary.setOnClickListener {
            showEggGroupMenu(eggGroup)
        }
        if (!EggActionHelp.isSupportedLaunch(eggGroup.selectedEgg)) {
            // override delegate click
            binding.cardView.setOnClickListener {
                showEggGroupMenu(eggGroup)
            }
        }
    }

    private fun showEggGroupMenu(eggGroup: EggGroup) {
        val vAdapter = this.vAdapter ?: return
        val popupMenu = PopupMenu(
            context, binding.tvSummary, Gravity.NO_GRAVITY,
            0,
            R.style.Theme_EggGroup_PopupMenu_ListPopupWindow
        )
        popupMenu.setForceShowIcon(true)
        for ((index, egg) in eggGroup.child.withIndex()) {
            val menuTitle = egg.versionFormatter.format(context)
            popupMenu.menu.add(0, egg.id, index, menuTitle).apply {
                val drawable = egg.getIcon(context)
                val drawH = drawable.intrinsicHeight
                val drawW = drawable.intrinsicWidth
                val width: Int = 28.dp// Use the width as the basis to align the text
                val height: Int = (width / drawW.toFloat() * drawH).roundToInt()
                icon = BitmapDrawable(
                    context.resources,
                    drawable.toBitmap(width, height)
                )
            }
        }
        MenuPopupAccessor.setApi23Transitions(popupMenu)
        popupMenu.setOnMenuItemClickListener {
            val index = eggGroup.child.indexOfFirst { egg ->
                egg.id == it.itemId
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

    override fun updateOrientationAngles(zAngle: Float, xAngle: Float, yAngle: Float) {
        delegate.updateOrientationAngles(zAngle, xAngle, yAngle)
    }

}