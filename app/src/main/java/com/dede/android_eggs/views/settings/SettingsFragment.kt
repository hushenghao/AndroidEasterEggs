package com.dede.android_eggs.views.settings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.TooltipCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentSettingsBinding
import com.dede.android_eggs.databinding.ItemSettingPrefGroupBinding
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.basic.dp
import com.dede.basic.requireDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.R as M3R

class SettingsFragment : BottomSheetDialogFragment(R.layout.fragment_settings) {

    var onSlide: ((offset: Float) -> Unit)? = null

    private var lastSlideOffset: Float = -1f
    private val callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (lastSlideOffset == slideOffset) return
            onSlide?.invoke(slideOffset)
            lastSlideOffset = slideOffset
        }
    }

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        EdgeUtils.applyEdge(dialog.window)
        val bottomSheetBehavior = dialog.behavior
        bottomSheetBehavior.addBottomSheetCallback(callback)
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.skipCollapsed = true
        dialog.dismissWithAnimation = true
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (pref in SettingsPrefs.providerPrefs()) {
            if (!pref.enable) continue
            binding.llSettings.addView(createPrefView(pref, requireContext()))
        }
        LocalEvent.receiver(this).register(SettingsPrefs.ACTION_CLOSE_SETTING) {
            dismiss()
        }
    }

    private fun createPrefView(pref: SettingPref, context: Context): View {
        val binding = ItemSettingPrefGroupBinding.inflate(LayoutInflater.from(context))
        if (pref.titleRes != View.NO_ID) {
            binding.tvTitle.setText(pref.titleRes)
        } else if (pref.title != null) {
            binding.tvTitle.text = pref.title
        }

        fun updateOptions() {
            binding.btGroup.removeAllViewsInLayout()
            for (op in pref.options) {
                val button = createOptionView(context, op)
                binding.btGroup.addView(button)
            }

            val op = pref.getSelectedOption(context)
            if (op != null) {
                binding.btGroup.check(op.id)
            }
        }

        updateOptions()

        val listener = object : SettingPref.PrefViewListener {
            override fun onUpdateOptions(options: List<SettingPref.Op>) {
                updateOptions()
            }

            override fun onSelectedOptionChange(op: SettingPref.Op) {
                binding.btGroup.check(op.id)
            }
        }
        binding.btGroup.tag = listener// hold viewListener
        pref.setViewListener(listener)

        binding.btGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val op = pref.findOptionById(checkedId)
                if (op != null) {
                    pref.preformOptionSelected(context, op)
                }
            }
        }
        return binding.root
    }

    private fun createOptionView(context: Context, op: SettingPref.Op): View {
        return MaterialButton(context, null, M3R.attr.materialButtonOutlinedStyle).apply {
            id = op.id
            if (op.titleRes != View.NO_ID) {
                text = context.getString(op.titleRes)
            } else if (op.title != null) {
                text = op.title
            }
            val iconMaker = op.iconMaker
            if (iconMaker != null) {
                icon = iconMaker.invoke(context, this)
            } else if (op.iconUnicode != null) {
                icon = FontIconsDrawable(
                    context, op.iconUnicode, M3R.attr.colorSecondary, 24f
                )
            } else if (op.iconRes != View.NO_ID) {
                icon = context.requireDrawable(op.iconRes)
                iconSize = 24.dp
            }
            iconTint = MaterialColors.getColorStateListOrNull(context, M3R.attr.colorSecondary)
            if (text.isNullOrEmpty()) {
                iconPadding = 0
            } else {
                iconPadding = 4.dp
                TooltipCompat.setTooltipText(this, text)
            }
            setPadding(12.dp, 0, 12.dp, 0)
            minWidth = 0
            minimumWidth = 0
            isSaveEnabled = false
        }
    }

}