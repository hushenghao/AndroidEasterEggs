package com.dede.android_eggs.main.holders

import android.content.Context
import android.graphics.*
import android.view.View
import coil.dispose
import coil.load
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.settings.IconShapePerf
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.resolveColorStateList
import com.dede.android_eggs.util.updateCompoundDrawablesRelative
import com.google.android.material.R as M3R

@VHType(viewType = Egg.VIEW_TYPE_EGG)
open class EggHolder(view: View) : VHolder<Egg>(view) {

    val binding: ItemEasterEggLayoutBinding = ItemEasterEggLayoutBinding.bind(view)
    val context: Context = itemView.context

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onBindViewHolder(egg: Egg) {
        binding.tvTitle.setText(egg.eggNameRes)
        binding.tvSummary.setText(egg.androidRes)
        binding.cardView.setOnClickListener { EggActionHelp.launchEgg(context, egg) }
        binding.background.tvBgMessage.setText(egg.versionCommentRes)
        binding.background.tvAddShortcut.isEnabled = EggActionHelp.supportShortcut(context, egg)

        binding.ivIcon.dispose()
        binding.background.ivBgIcon.dispose()
        if (egg.supportAdaptiveIcon) {
            val pathStr = IconShapePerf.getMaskPath(context)
            binding.ivIcon.setImageDrawable(
                AlterableAdaptiveIconDrawable(context, egg.iconRes, pathStr)
            )
            binding.background.ivBgIcon.setImageDrawable(
                AlterableAdaptiveIconDrawable(context, egg.iconRes, pathStr)
            )
        } else {
            binding.ivIcon.load(egg.iconRes)
            binding.background.ivBgIcon.load(egg.iconRes)
        }

        val color = context.resolveColorStateList(
            M3R.attr.textAppearanceLabelMedium, android.R.attr.textColor
        )
        val drawable = FontIconsDrawable(context, Icons.Outlined.app_shortcut, 22f).apply {
            setColorStateList(color)
        }
        binding.background.tvAddShortcut.updateCompoundDrawablesRelative(end = drawable)
    }

}