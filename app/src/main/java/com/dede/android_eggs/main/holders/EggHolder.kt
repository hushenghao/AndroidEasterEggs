package com.dede.android_eggs.main.holders

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.core.view.isVisible
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.settings.IconShapeOverride
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.SupportAdaptiveIconTransformation
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.drawables.FontIconsDrawable

@VHType(viewType = Egg.VIEW_TYPE_EGG)
open class EggHolder(view: View) : VHolder<Egg>(view) {

    val binding: ItemEasterEggLayoutBinding = ItemEasterEggLayoutBinding.bind(view)
    val context: Context = itemView.context

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onBindViewHolder(egg: Egg) {
        binding.tvTitle.setText(egg.eggNameRes)
        binding.tvSummary.setText(egg.androidRes)
        binding.ivIcon.load(egg.iconRes) {
            applySupportAdaptiveIcon(context, egg.supportAdaptiveIcon)
        }
        binding.cardView.setOnClickListener { EggActionHelp.launchEgg(context, egg) }
        binding.background.tvBgMessage.setText(egg.versionCommentRes)
        binding.background.ivBgIcon.load(egg.iconRes) {
            applySupportAdaptiveIcon(context, egg.supportAdaptiveIcon)
        }
        binding.background.tvAddShortcut.isVisible = EggActionHelp.supportShortcut(context, egg)
        val drawable = FontIconsDrawable(
            context,
            Icons.SHORTCUT,
            22f
        )
        binding.background.tvAddShortcut.setCompoundDrawablesRelative(null, null, drawable, null)
    }

    private fun ImageRequest.Builder.applySupportAdaptiveIcon(
        context: Context,
        supportAdaptiveIcon: Boolean,
    ) {
        if (!supportAdaptiveIcon || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return
        }
        val shapePath = IconShapeOverride.getAppliedValue(context)
        if (!IconShapeOverride.isSquareShape(context, shapePath)) {
            if (!TextUtils.isEmpty(shapePath)) {
                transformations(SupportAdaptiveIconTransformation(shapePath))
            } else {
                transformations(CircleCropTransformation())
            }
        }
    }
}