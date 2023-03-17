package com.dede.android_eggs.main.holders

import android.content.Context
import android.view.View
import coil.load
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.EggActionController
import com.dede.android_eggs.main.EggActionController.Companion.applySupportAdaptiveIcon
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.ui.adapter.VHolder

open class EggHolder(view: View) : VHolder<Egg>(view) {

    val binding: ItemEasterEggLayoutBinding = ItemEasterEggLayoutBinding.bind(view)
    val context: Context = itemView.context
    private val eggActionController = EggActionController(context)

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onBindViewHolder(egg: Egg) {
        binding.tvTitle.setText(egg.eggNameRes)
        binding.tvSummary.setText(egg.androidRes)
        binding.ivIcon.load(egg.iconRes) {
            applySupportAdaptiveIcon(context, egg.supportAdaptiveIcon)
        }
        itemView.setOnClickListener { eggActionController.openEgg(egg) }
        binding.ivIcon.setOnClickListener { eggActionController.showVersionCommentDialog(egg) }
    }
}