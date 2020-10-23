package com.dede.android_eggs

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

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

    class SettingsFragment : PreferenceFragmentCompat() {

        companion object {
            const val KEY_FORMAT = "pref_key_%d"
        }

        private lateinit var dispatcher: EasterEggsDispatcher

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//            dispatcher = EasterEggsDispatcher(requireContext())
//                .register(Build.VERSION_CODES.R) {
//                    Intent(Intent.ACTION_MAIN)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .setClassName("android", "com.android.internal.app.PlatLogoActivity")
//                }

            // 使用当前版本的彩蛋
//            val key = String.format(KEY_FORMAT, Build.VERSION.SDK_INT)
//            findPreference<Preference>(key)?.setOnPreferenceClickListener {
//                return@setOnPreferenceClickListener dispatcher.dispatch(Build.VERSION.SDK_INT)
//            }
        }
    }

}
