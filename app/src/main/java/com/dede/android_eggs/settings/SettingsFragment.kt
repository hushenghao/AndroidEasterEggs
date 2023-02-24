package com.dede.android_eggs.settings

import android.app.Dialog
import android.app.LocaleManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.core.view.updatePadding
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.util.IconShapeOverride
import com.dede.android_eggs.util.WindowEdgeUtilsAccessor
import com.dede.android_eggs.util.requirePreference
import com.dede.basic.dp
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsFragment : BottomSheetDialogFragment(R.layout.fragment_settings) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        WindowEdgeUtilsAccessor.applyEdgeToEdge(dialog.window!!, true)
        val bottomSheetBehavior = dialog.behavior
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.skipCollapsed = true
        dialog.dismissWithAnimation = true
        return dialog
    }

    class Settings : PreferenceFragmentCompat() {

        companion object {
            const val PREF_LANGUAGE = "pref_language"
            const val PREF_VERSION = "pref_version"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preference_settings)
            requirePreference<ListPreference>(IconShapeOverride.KEY_PREFERENCE).apply {
                isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                IconShapeOverride.handlePreferenceUi(this)
            }

            requirePreference<Preference>(PREF_VERSION).summary =
                requireContext().getString(
                    R.string.label_version,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                configureChangeLanguagePreference()
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            listView.updatePadding(top = 20.dp, bottom = 180.dp)
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun configureChangeLanguagePreference() {
            requirePreference<Preference>(PREF_LANGUAGE).apply {
                val localeManager = requireContext().getSystemService<LocaleManager>()
                if (localeManager != null) {
                    summary = localeManager.applicationLocales.toLanguageTags()
                }
                isEnabled = true
                setOnPreferenceClickListener {
                    startActivity(createActionAppLocaleSettingsIntent())
                    return@setOnPreferenceClickListener true
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun createActionAppLocaleSettingsIntent(): Intent = Intent(
            android.provider.Settings.ACTION_APP_LOCALE_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
    }
}