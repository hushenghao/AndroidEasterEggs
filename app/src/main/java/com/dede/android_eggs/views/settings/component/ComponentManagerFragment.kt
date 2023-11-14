package com.dede.android_eggs.views.settings.component

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import coil.imageLoader
import coil.request.ImageRequest
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EasterEggHelp.VersionFormatter
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.requirePreference
import com.dede.basic.dp
import com.dede.basic.provider.ComponentProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.android.material.R as M3R

class ComponentManagerFragment : BottomSheetDialogFragment(R.layout.fragment_component_manager) {

    companion object {
        fun show(fm: FragmentManager) {
            val fragment = ComponentManagerFragment()
            fragment.show(fm, "ComponentManagerFragment")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        EdgeUtils.applyEdge(dialog.window)
        return dialog
    }

    @AndroidEntryPoint
    class ComponentSettings : PreferenceFragmentCompat() {

        @Inject
        lateinit var componentList: List<@JvmSuppressWildcards ComponentProvider.Component>

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_component_settings)

            val preferenceCategory = requirePreference<PreferenceCategory>("key_component_manager")
            preferenceCategory.icon = FontIconsDrawable(
                requireContext(),
                Icons.Rounded.app_registration, M3R.attr.colorControlNormal, 24f
            )
            for (component in componentList) {
                val preference = createPreference(component)
                preferenceCategory.addPreference(preference)
            }
        }

        private fun createPreference(component: ComponentProvider.Component): Preference {
            val context = requireContext()
            return SwitchPreferenceCompat(context).apply {
                val request = ImageRequest.Builder(context)
                    .data(component.iconRes)
                    .size(30.dp)
                    .target { icon = it }
                    .build()
                context.imageLoader.enqueue(request)
                setTitle(component.nameRes)
                val formatter = VersionFormatter.create(component.apiLevel, component.nicknameRes)
                summary = formatter.format(context)
                isEnabled = component.isSupported()
                if (isEnabled) {
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