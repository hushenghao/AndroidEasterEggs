package com.dede.android_eggs.views.settings

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentSettingsBinding
import com.dede.android_eggs.databinding.ItemSettingPrefButtonBinding
import com.dede.android_eggs.databinding.ItemSettingPrefGroupBinding
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.basic.requireDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.R as M3R

class SettingsFragment : BottomSheetDialogFragment(R.layout.fragment_settings) {

    var onSlide: ((offset: Float) -> Unit)? = null

    var onPreDismiss: (() -> Unit)? = null

    var onDismiss: (() -> Unit)? = null

    private var lastSlideOffset: Float = -1f
    private val callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_SETTLING) {
                onPreDismiss?.invoke()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (lastSlideOffset == slideOffset) return
            onSlide?.invoke(slideOffset)
            lastSlideOffset = slideOffset
        }
    }

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onDismiss(dialog: DialogInterface) {
        onDismiss?.invoke()
        super.onDismiss(dialog)
    }

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
                addOptionButton(context, binding.btGroup, op)
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

    private fun addOptionButton(context: Context, parent: ViewGroup, op: SettingPref.Op): View {
        val binding = ItemSettingPrefButtonBinding
            .inflate(LayoutInflater.from(context), parent, true)
        return binding.root.apply {
            id = op.id
            if (op.titleRes != View.NO_ID) {
                text = context.getString(op.titleRes)
            } else if (op.title != null) {
                text = op.title
            } else {
                iconPadding = 0
            }
            if (text != null) {
                TooltipCompat.setTooltipText(this, text)
            }
            val iconMaker = op.iconMaker
            if (iconMaker != null) {
                icon = iconMaker.invoke(context, this)
                iconSize = 0
            } else if (op.iconUnicode != null) {
                icon = FontIconsDrawable(context, op.iconUnicode, M3R.attr.colorSecondary)
            } else if (op.iconRes != View.NO_ID) {
                icon = context.requireDrawable(op.iconRes)
            }
        }
    }

}