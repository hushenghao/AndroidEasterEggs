package com.dede.android_eggs

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.androidbrowserhelper.trusted.TwaLauncher

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

        companion object {
            const val KEY_GITHUB = "key_github"
            const val KEY_SOURCE = "key_source"
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            findPreference<Preference>(KEY_GITHUB)?.onPreferenceClickListener = this
            findPreference<Preference>(KEY_SOURCE)?.onPreferenceClickListener = this
        }

        override fun onPreferenceClick(preference: Preference?): Boolean {
            return when (preference?.key) {
                KEY_GITHUB -> {
                    openTwaWeb("https://github.com/hushenghao/AndroidEasterEggs")
                    true
                }
                KEY_SOURCE -> {
                    openTwaWeb("https://github.com/aosp-mirror/platform_frameworks_base")
                    true
                }
                else -> false
            }
        }

        private fun openTwaWeb(url: String) {
            val uri = Uri.parse(url)
            val twaLauncher = TwaLauncher(requireContext())
            val builder = TrustedWebActivityIntentBuilder(uri)
            try {
                val field =
                    TrustedWebActivityIntentBuilder::class.java.getDeclaredField("mIntentBuilder")
                field.isAccessible = true
                val customTabsBuilder = field.get(builder) as CustomTabsIntent.Builder
                customTabsBuilder.setShowTitle(true)// 显示标题栏
                    .addDefaultShareMenuItem()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            twaLauncher.launch(builder, null, null, null)
        }
    }

}
