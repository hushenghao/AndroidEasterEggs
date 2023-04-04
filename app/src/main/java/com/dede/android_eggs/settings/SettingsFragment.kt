package com.dede.android_eggs.settings

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.preferences.MaterialListPreferenceDialog
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

    class Settings : PreferenceFragmentCompat(),
        PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
                for (pref in SettingsPrefs.providePrefs()) {
                    addPreference(pref.onCreatePreference(requireContext()))
                }
            }
        }

        override fun onPreferenceDisplayDialog(
            caller: PreferenceFragmentCompat,
            pref: Preference,
        ): Boolean {
            if (pref is ListPreference) {
                if (pref.key == IconShapeOverride.KEY_PREFERENCE) {
                    MaterialListPreferenceDialog.IconShape.newInstance(pref).show()
                } else {
                    MaterialListPreferenceDialog.newInstance(pref).show()
                }
                return true
            }
            return false
        }
    }
}