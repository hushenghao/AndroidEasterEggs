package com.dede.android_eggs.settings

import android.app.Dialog
import android.app.LocaleManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.FontIconsDrawable
import com.dede.android_eggs.util.IconShapeOverride
import com.dede.android_eggs.util.NightModeManager
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

    class Settings : PreferenceFragmentCompat(),
        PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

        companion object {
            private const val PREF_LANGUAGE = "pref_language"
            private const val PREF_VERSION = "pref_version"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preference_settings)

            requirePreference<SwitchPreferenceCompat>(NightModeManager.KEY_NIGHT_MODE).apply {
                icon = createFontIcon("\ue3ab")
                setOnPreferenceChangeListener { _, newValue ->
                    NightModeManager(requireContext())
                        .setNightMode(newValue as Boolean)
                    return@setOnPreferenceChangeListener true
                }
            }

            requirePreference<ListPreference>(IconShapeOverride.KEY_PREFERENCE).apply {
                icon = createFontIcon("\ue920")
                isEnabled = IconShapeOverride.isSupported()
                IconShapeOverride.handlePreferenceUi(this)
            }

            requirePreference<Preference>(PREF_VERSION).apply {
                icon = createFontIcon("\ue88e")
                summary = requireContext().getString(
                    R.string.label_version,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                )
            }

            configureChangeLanguagePreference()
        }

        private fun configureChangeLanguagePreference() {
            requirePreference<Preference>(PREF_LANGUAGE).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val localeManager = requireContext().getSystemService<LocaleManager>()
                    if (localeManager != null) {
                        val locales = localeManager.applicationLocales
                        if (!locales.isEmpty) {
                            summary = locales.get(0).displayName
                        }
                    }
                    setOnPreferenceClickListener {
                        startActivity(createActionAppLocaleSettingsIntent())
                        return@setOnPreferenceClickListener true
                    }
                }
                isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                icon = createFontIcon("\ue894")
            }
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun createActionAppLocaleSettingsIntent(): Intent = Intent(
            android.provider.Settings.ACTION_APP_LOCALE_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )

        private fun createFontIcon(unicode: String): Drawable {
            return FontIconsDrawable(requireContext(), unicode, 36f).apply {
                setPadding(12.dp, 6.dp, 0, 0)
            }
        }

        override fun onPreferenceDisplayDialog(
            caller: PreferenceFragmentCompat,
            pref: Preference
        ): Boolean {
            if (pref is ListPreference) {
                MaterialListPreferenceDialog.newInstance(pref).show()
                return true
            }
            return false
        }
    }
}