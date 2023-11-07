package com.dede.android_eggs.views.settings.component

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg.VersionFormatter
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.inset
import com.dede.android_eggs.util.requirePreference
import com.dede.basic.dp
import com.dede.basic.provider.ComponentProvider
import com.dede.basic.requireDrawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class ComponentManagerFragment : BottomSheetDialogFragment(R.layout.fragment_component_manager) {

    companion object {
        fun show(fm: FragmentManager) {
            val fragment = ComponentManagerFragment()
            fragment.show(fm, "ComponentManagerFragment")
        }
    }

    @AndroidEntryPoint
    class ComponentSettings : PreferenceFragmentCompat() {

        @Inject
        lateinit var componentList: List<@JvmSuppressWildcards ComponentProvider.Component>

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_component_settings)

            val preferenceCategory = requirePreference<PreferenceCategory>("key_component_manager")
            preferenceCategory.icon =
                FontIconsDrawable(requireContext(), Icons.Rounded.grid_view, 24f)
                    .inset(start = 6.dp, end = 6.dp)
            for (component in componentList) {
                val preference = createPreference(component)
                preferenceCategory.addPreference(preference)
            }
        }

        private fun createPreference(component: ComponentProvider.Component): Preference {
            val context = requireContext()
            return SwitchPreferenceCompat(context).apply {
                setIcon(component.iconRes)
                setTitle(component.nameRes)
                val formatter = VersionFormatter.create(component.nicknameRes, component.apiLevel)
                summary = formatter.format(context)
                val supported = component.isSupported()
                isEnabled = supported
                if (supported) {
                    isChecked = component.isEnabled(context)
                    setOnPreferenceChangeListener { _, newValue ->
                        component.setEnabled(context, newValue as Boolean)
                        return@setOnPreferenceChangeListener true
                    }
                }
            }
        }

    }
}