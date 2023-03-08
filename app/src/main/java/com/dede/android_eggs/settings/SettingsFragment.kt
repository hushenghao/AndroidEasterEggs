package com.dede.android_eggs.settings

import android.app.Dialog
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.FontIconsDrawable
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.util.IconShapeOverride
import com.dede.android_eggs.util.NightModeManager
import com.dede.android_eggs.util.WindowEdgeUtilsAccessor
import com.dede.android_eggs.util.requirePreference
import com.dede.basic.dp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsFragment : BottomSheetDialogFragment(R.layout.fragment_settings) {

    var onSlide: Function1<Float, Unit>? = null

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
        WindowEdgeUtilsAccessor.applyEdgeToEdge(dialog.window!!, true)
        val bottomSheetBehavior = dialog.behavior
        bottomSheetBehavior.addBottomSheetCallback(callback)
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.skipCollapsed = true
        dialog.dismissWithAnimation = true
        return dialog
    }

    class Settings : PreferenceFragmentCompat(),
        PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

        companion object {
            private const val PREF_LANGUAGE = "pref_language"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preference_settings)

            requirePreference<SwitchPreferenceCompat>(NightModeManager.KEY_NIGHT_MODE).apply {
                icon = createFontIcon(Icons.BRIGHTNESS_6)
                setOnPreferenceChangeListener { _, newValue ->
                    NightModeManager(requireContext())
                        .setNightMode(newValue as Boolean)
                    return@setOnPreferenceChangeListener true
                }
            }

            requirePreference<ListPreference>(IconShapeOverride.KEY_PREFERENCE).apply {
                icon = createFontIcon(Icons.LANGUAGE)
                isEnabled = IconShapeOverride.isEnabled()
                IconShapeOverride.handlePreferenceUi(this)
            }

            configureChangeLanguagePreference()
        }

        private fun configureChangeLanguagePreference() {
            requirePreference<Preference>(PREF_LANGUAGE).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    setOnPreferenceClickListener {
                        runCatching {
                            startActivity(createActionAppLocaleSettingsIntent())
                        }
                        return@setOnPreferenceClickListener true
                    }
                }
                summary = getLocalDisplayName(requireContext())
                isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                icon = createFontIcon("\ue894")
            }
        }

        private fun getLocalDisplayName(context: Context): String? {
            var name: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val localeManager = context.getSystemService<LocaleManager>()
                if (localeManager != null) {
                    val locales = localeManager.applicationLocales
                    if (!locales.isEmpty) {
                        name = locales.get(0).displayName
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val locales = context.resources.configuration.locales
                if (!locales.isEmpty) {
                    name = locales.get(0).displayName
                }
            } else {
                @Suppress("DEPRECATION")
                val locale = context.resources.configuration.locale
                name = locale.displayName
            }
            return name
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private fun createActionAppLocaleSettingsIntent(): Intent = Intent(
            android.provider.Settings.ACTION_APP_LOCALE_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        private fun createFontIcon(unicode: String): Drawable {
            return FontIconsDrawable(requireContext(), unicode, 36f).apply {
                setPadding(12.dp, 6.dp, 0, 0)
            }
        }

        override fun onPreferenceDisplayDialog(
            caller: PreferenceFragmentCompat,
            pref: Preference,
        ): Boolean {
            if (pref is ListPreference) {
                MaterialListPreferenceDialog.newInstance(pref).show()
                return true
            }
            return false
        }
    }
}