package com.dede.android_eggs.views.settings.more

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.util.requirePreference
import com.dede.android_eggs.views.settings.SettingsPrefs
import com.dede.android_eggs.views.settings.component.ComponentManagerFragment
import com.dede.android_eggs.views.timeline.AndroidTimelineFragment
import com.dede.basic.dp
import com.dede.basic.requireDrawable
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.R as M3R


@AndroidEntryPoint
class MoreSettingsActivity : AppCompatActivity(R.layout.activity_more_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        EdgeUtils.applyEdge(window)
        ThemeUtils.tryApplyOLEDTheme(this)
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            @Suppress("DEPRECATION")
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class MoreSettings : PreferenceFragmentCompat() {

        companion object {
            private const val KEY_COMPONENT_MANAGER = "key_component_manager"
            private const val KEY_TIMELINE = "key_timeline"
            private const val KEY_ABOUT = "key_about"
            private const val KEY_GET_BETA = "key_get_beta"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_more_settings)
            val prefAbout = requirePreference<Preference>(KEY_ABOUT)
            prefAbout.summary = getString(
                R.string.label_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE
            )
            prefAbout.icon = InsetDrawable(
                requireContext().requireDrawable(R.mipmap.ic_launcher_round),
                0, 14.dp, 28.dp, 14.dp
            )
            setPreferenceIcon(KEY_COMPONENT_MANAGER, Icons.Rounded.app_registration)
            setPreferenceIcon(KEY_TIMELINE, Icons.Rounded.timeline)
            setPreferenceIcon(KEY_GET_BETA, Icons.Rounded.adb)
        }

        private fun setPreferenceIcon(key: String, unicode: String) {
            requirePreference<Preference>(key).icon = FontIconsDrawable(
                requireContext(), unicode, M3R.attr.colorSecondary, 30f
            )
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            (requireActivity() as AppCompatActivity).title = preferenceScreen.title
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                KEY_COMPONENT_MANAGER -> {
                    ComponentManagerFragment.show(childFragmentManager)
                }

                KEY_TIMELINE -> {
                    AndroidTimelineFragment.show(childFragmentManager)
                }

                else -> return super.onPreferenceTreeClick(preference)
            }
            return true
        }
    }
}