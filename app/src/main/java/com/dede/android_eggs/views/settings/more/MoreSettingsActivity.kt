package com.dede.android_eggs.views.settings.more

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.SplitUtils
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.util.requirePreference
import com.dede.android_eggs.views.settings.component.ComponentManagerFragment
import com.dede.android_eggs.views.settings.prefs.DynamicColorPref
import com.dede.android_eggs.views.settings.prefs.NightModePref
import com.dede.android_eggs.views.timeline.AndroidTimelineFragment
import com.dede.basic.dp
import com.dede.basic.requireDrawable
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.R as M3R


@AndroidEntryPoint
class MoreSettingsActivity : AppCompatActivity(R.layout.activity_more_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!SplitUtils.isActivityEmbedded(this)) {
            with(window) {
                allowEnterTransitionOverlap = true
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                    .addTarget(R.id.root)
                returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
                    .addTarget(R.id.root)
            }
        }
        ThemeUtils.tryApplyOLEDTheme(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        with(LocalEvent.receiver(this)) {
            register(NightModePref.ACTION_NIGHT_MODE_CHANGED) {
                recreate()
            }
            register(DynamicColorPref.ACTION_DYNAMIC_COLOR_CHANGED) {
                recreate()
            }
        }
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
            private const val KEY_TRANSLATION = "key_translation"
            private const val KEY_LICENSE = "key_license"
            private const val KEY_PRIVACY = "key_privacy"
            private const val KEY_FEEDBACK = "key_feedback"
            private const val KEY_RETAIN_IN_RECENTS = "key_retain_in_recents"

            fun isRetainInRecentsEnabled(context: Context): Boolean {
                return context.pref.getBoolean(KEY_RETAIN_IN_RECENTS, false)
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.pref_more_settings)
            val prefAbout = requirePreference<Preference>(KEY_ABOUT)
            prefAbout.title = getString(
                R.string.label_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE
            )
            prefAbout.summary = SpannableString(BuildConfig.GIT_HASH).apply {
                setSpan(StyleSpan(Typeface.ITALIC), 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                setSpan(AbsoluteSizeSpan(14, true), 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            prefAbout.icon = InsetDrawable(
                requireContext().requireDrawable(R.mipmap.ic_launcher_round),
                0, 12.dp, 24.dp, 12.dp
            )
            setPreferenceIcon(KEY_COMPONENT_MANAGER, Icons.Rounded.app_registration)
            setPreferenceIcon(KEY_TIMELINE, Icons.Rounded.timeline)
            setPreferenceIcon(KEY_RETAIN_IN_RECENTS, Icons.Rounded.view_carousel)
            setPreferenceIcon(KEY_TRANSLATION, Icons.Rounded.g_translate)
            setPreferenceIcon(KEY_LICENSE, Icons.Rounded.balance)
            setPreferenceIcon(KEY_PRIVACY, Icons.Outlined.privacy_tip)
            setPreferenceIcon(KEY_FEEDBACK, Icons.Outlined.feedback)
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
            val context = preference.context
            when (preference.key) {
                KEY_ABOUT -> {
                    val commitId =
                        context.getString(R.string.url_github_commit, BuildConfig.GIT_HASH)
                    CustomTabsBrowser.launchUrl(context, commitId.toUri())
                }

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