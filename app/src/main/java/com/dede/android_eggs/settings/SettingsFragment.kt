package com.dede.android_eggs.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        EdgePref.applyEdge(requireContext(), dialog.window!!)
        val bottomSheetBehavior = dialog.behavior
        bottomSheetBehavior.addBottomSheetCallback(callback)
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.skipCollapsed = true
        dialog.dismissWithAnimation = true
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LocalEvent.get(this).register(IconShapePref.ACTION_CLOSE_SETTING) {
            dismiss()
        }
    }

    class Settings : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View {
            return LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.VERTICAL
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val layout = (view as ViewGroup)
            for (pref in SettingsPrefs.providerPrefs()) {
                layout.addView(pref.onCreateView(requireContext()))
            }
        }
    }

}