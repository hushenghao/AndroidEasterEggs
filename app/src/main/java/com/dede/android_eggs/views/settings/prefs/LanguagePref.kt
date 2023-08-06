package com.dede.android_eggs.views.settings.prefs

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons.Outlined.language
import com.dede.android_eggs.views.settings.SettingPref
import java.util.Locale


class LanguagePref : SettingPref(null, options, SYSTEM) {
    companion object {

        private const val SYSTEM = 0
        private const val CHINESE = 1               // zh, API<24
        private const val SIMPLIFIED_CHINESE = 2    // zh-CN
        private const val TRADITIONAL_CHINESE = 3   // zh-HK, zh-TW
        private const val ENGLISH = 4               // en
        private const val RUSSIAN = 5               // ru
        private const val ITALIAN = 6               // it

        // Locale.TRADITIONAL_CHINESE is zh-TW, expected is HongKong.
        private const val HK = "zh-HK"
        private const val RU = "ru"

        private val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listOf(
                Op(SYSTEM, iconUnicode = language),
                Op(SIMPLIFIED_CHINESE, titleRes = R.string.language_zh_sc),
                Op(TRADITIONAL_CHINESE, titleRes = R.string.language_zh_tc),
                Op(ENGLISH, titleRes = R.string.language_en),
                Op(RUSSIAN, titleRes = R.string.language_ru),
                Op(ITALIAN, titleRes = R.string.language_it),
            )
        } else {
            // For API<24 the application does not have a localeList instead it has a single locale
            // Unsupported region
            listOf(
                Op(SYSTEM, iconUnicode = language),
                Op(CHINESE, titleRes = R.string.language_ch),
                Op(ENGLISH, titleRes = R.string.language_en),
                Op(RUSSIAN, titleRes = R.string.language_ru),
                Op(ITALIAN, titleRes = R.string.language_it),
            )
        }

        private fun getLocaleByValue(value: Int): LocaleListCompat {
            return when (value) {
                CHINESE -> LocaleListCompat.create(Locale.CHINESE)
                SIMPLIFIED_CHINESE -> LocaleListCompat.create(Locale.SIMPLIFIED_CHINESE)
                TRADITIONAL_CHINESE -> LocaleListCompat.forLanguageTags(HK)
                ENGLISH -> LocaleListCompat.create(Locale.ENGLISH)
                RUSSIAN -> LocaleListCompat.forLanguageTags(RU)
                ITALIAN -> LocaleListCompat.create(Locale.ITALIAN)
                else -> LocaleListCompat.getEmptyLocaleList()
            }
        }

        private fun getValueByLocale(localeList: LocaleListCompat): Int {
            if (localeList.isEmpty) {
                return SYSTEM
            }
            when (localeList.toLanguageTags()) {
                HK -> return TRADITIONAL_CHINESE
                RU -> return RUSSIAN
            }
            return when (localeList.get(0)) {
                Locale.CHINESE -> CHINESE
                Locale.SIMPLIFIED_CHINESE -> SIMPLIFIED_CHINESE
                Locale.TRADITIONAL_CHINESE -> TRADITIONAL_CHINESE
                Locale.ENGLISH -> ENGLISH
                Locale.ITALIAN -> ITALIAN
                else -> SYSTEM
            }
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_language

    override fun setValue(context: Context, value: Int): Boolean {
        return false
    }

    override fun getValue(context: Context, default: Int): Int {
        return getValueByLocale(AppCompatDelegate.getApplicationLocales())
    }

    override fun onOptionSelected(context: Context, option: Op) {
        AppCompatDelegate.setApplicationLocales(getLocaleByValue(option.value))
    }
}