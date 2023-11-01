package com.dede.android_eggs.views.settings.prefs

import android.app.Dialog
import android.app.LocaleConfig
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons.Outlined.language
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.SettingPref
import com.dede.basic.createLocalesContext
import com.dede.basic.getLayoutDirection
import com.dede.basic.globalContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale


class LanguagePref : SettingPref(null, getOptions(), SYSTEM) {
    companion object {

        private const val TAG = "LanguagePref"

        private const val SYSTEM = 0
        private const val MORE = -1
        private const val SIMPLIFIED_CHINESE = 2    // zh-CN
        private const val TRADITIONAL_CHINESE = 3   // zh-HK, zh-TW
        private const val ENGLISH = 4               // en
        private const val RUSSIAN = 5               // ru
        private const val ITALIAN = 6               // it
        private const val GERMANY = 7               // de
        private const val PORTUGAL = 8              // pt
        private const val PORTUGAL_BRAZIL = 29      // pt-BR
        private const val INDONESIA = 9             // in-ID
        private const val JAPANESE = 10             // ja-JP
        private const val KOREAN = 11               // ko
        private const val FRENCH = 12               // fr
        private const val ARABIC = 13               // ar-SA, RTL
        private const val POLISH = 14               // pl-PL
        private const val TURKISH = 15              // tr-TR
        private const val UKRAINIAN = 16            // uk-UA
        private const val SPANISH = 17              // es
        private const val CROATIAN = 18             // hr-HR
        private const val DUTCH = 19                // nl-NL
        private const val LATIN = 20                // la-LA
        private const val GREEK = 21                // el-GR
        private const val FINNISH = 22              // fi-FI
        private const val VIETNAMESE = 23           // vi-VN
        private const val HUNGARIAN = 24            // hu-HU
        private const val THAI = 25                 // th-TH
        private const val NORWEGIAN = 26            // no-NO
        private const val FILIPINO = 27             // fil-PH
        private const val LAO = 28                  // lo-LA

        private fun getOptions(): List<Op> {
            val options = mutableListOf(
                Op(SYSTEM, iconUnicode = language, titleRes = R.string.summary_system_default),
                Op(MORE, titleRes = R.string.pref_title_language_more),
            )
            val value = getValueByLocale(AppCompatDelegate.getApplicationLocales())
            val languageOp = languageOptions.find { it.value == value }
            if (languageOp != null) {
                options.add(1, languageOp)
            }
            return options
        }

        private class LangOp(value: Int, titleRes: Int, val locale: Locale) :
            Op(value, titleRes = titleRes)

        private val languageOptions = listOf(
            LangOp(SIMPLIFIED_CHINESE, R.string.language_zh_SC, Locale.SIMPLIFIED_CHINESE),
            LangOp(TRADITIONAL_CHINESE, R.string.language_zh_TC, Locale.TRADITIONAL_CHINESE),
            LangOp(ENGLISH, R.string.language_en, Locale.ENGLISH),
            LangOp(RUSSIAN, R.string.language_ru, createLocale("ru")),
            LangOp(ITALIAN, R.string.language_it, Locale.ITALIAN),
            LangOp(GERMANY, R.string.language_de, Locale.GERMANY),
            LangOp(SPANISH, R.string.language_es, createLocale("es")),
            LangOp(PORTUGAL, R.string.language_pt, createLocale("pt")),
            LangOp(PORTUGAL_BRAZIL, R.string.language_pt_BR, createLocale("pt", "BR")),
            LangOp(INDONESIA, R.string.language_in_ID, createLocale("in", "ID")),
            LangOp(ARABIC, R.string.language_ar_SA, createLocale("ar", "SA")),
            LangOp(CROATIAN, R.string.language_hr_HR, createLocale("hr", "HR")),
            LangOp(FRENCH, R.string.language_fr, Locale.FRENCH),
            LangOp(POLISH, R.string.language_pl_PL, createLocale("pl", "PL")),
            LangOp(DUTCH, R.string.language_nl_NL, createLocale("nl", "NL")),
//            LangOp(LATIN, R.string.language_la_LA, createLocale("la", "LA")),
            LangOp(TURKISH, R.string.language_tr_TR, createLocale("tr", "TR")),
            LangOp(UKRAINIAN, R.string.language_uk_UA, createLocale("uk", "UA")),
            LangOp(JAPANESE, R.string.language_ja_JP, Locale.JAPAN),
            LangOp(KOREAN, R.string.language_ko, Locale.KOREAN),
            LangOp(GREEK, R.string.language_el_GR, createLocale("el", "GR")),
            LangOp(FINNISH, R.string.language_fi_FI, createLocale("fi", "FI")),
            LangOp(VIETNAMESE, R.string.language_vi_VN, createLocale("vi", "VN")),
            LangOp(HUNGARIAN, R.string.language_hu_HU, createLocale("hu", "HU")),
            LangOp(THAI, R.string.language_th_TH, createLocale("th", "TH")),
            LangOp(NORWEGIAN, R.string.language_no_NO, createLocale("no", "NO")),
            LangOp(FILIPINO, R.string.language_fil_PH, createLocale("fil", "PH")),
            LangOp(LAO, R.string.language_lo_LA, createLocale("lo", "LA")),
        )

        init {
            if (BuildConfig.DEBUG) {
                checkLocaleConfig()
            }
        }

        private fun checkLocaleConfig() {
            // check languageOptions count
            val expected = languageOptions.size
            var actual = HashSet(languageOptions).size
            check(expected == actual) {
                "Language option length, expected: %d, actual: %d."
                    .format(expected, actual)
            }

            // check locale-config.xml locale count
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val localeConfig = LocaleConfig.fromContextIgnoringOverride(globalContext)
                actual = localeConfig.supportedLocales?.size() ?: -1
                check(expected == actual) {
                    "locale-config.xml child node length, expected: %d, actual: %d."
                        .format(expected, actual)
                }
            }

            // check gradle resourceConfigurations count
            actual = BuildConfig.LANGUAGE_RES
            check(expected == actual) {
                "android.defaultConfig.resourceConfigurations length, expected: %d, actual: %d."
                    .format(expected, actual)
            }
        }

        private class LangOpComparator(val context: Context) : Comparator<LangOp> {

            private val LangOp.compareValue: Int
                get() = when (value) {
                    SIMPLIFIED_CHINESE -> -2// first in list
                    TRADITIONAL_CHINESE -> -1
                    else -> 0
                }

            private val LangOp.compareName: String
                get() = context.getString(titleRes)

            override fun compare(o1: LangOp, o2: LangOp): Int {
                var r = o1.compareValue.compareTo(o2.compareValue)
                if (r == 0) {
                    r = o1.compareName.compareTo(o2.compareName)
                }
                return r
            }
        }

        private fun getLocaleByValue(value: Int): LocaleListCompat {
            val op = languageOptions.find { it.value == value }
            if (op != null) {
                return LocaleListCompat.create(op.locale)
            }
            return LocaleListCompat.getEmptyLocaleList()
        }

        private fun getValueByLocale(localeList: LocaleListCompat): Int {
            if (localeList.isEmpty) {
                return SYSTEM
            }
            val locale = localeList.get(0)
            val op = languageOptions.find { it.locale == locale }
            if (op != null) {
                return op.value
            }
            Log.w(TAG, "Not found language value by locale: $localeList, use default!")
            return SYSTEM
        }

        fun getApplicationLocale(): Locale {
            val locales = AppCompatDelegate.getApplicationLocales()
            return if (locales.isEmpty) {
                Locale.getDefault()
            } else {
                locales.get(0) ?: Locale.getDefault()
            }
        }

        private fun createLocale(language: String, region: String = ""): Locale {
            return Locale.Builder()
                .setLanguage(language)
                .setRegion(region)
                .build()
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_language

    override val enable: Boolean
        // For API<24 the application does not have a localeList instead it has a single locale
        // Unsupported region
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    override fun setValue(context: Context, value: Int): Boolean {
        return false
    }

    override fun getValue(context: Context, default: Int): Int {
        return getValueByLocale(AppCompatDelegate.getApplicationLocales())
    }

    private fun handleLocaleChoice(context: Context, dialog: Dialog, locales: LocaleListCompat) {
        val localesCtx = context.createLocalesContext(locales)
        val decorView = requireNotNull(dialog.window).decorView
        ViewCompat.setLayoutDirection(decorView, localesCtx.getLayoutDirection())
        dialog.setTitle(localesCtx.getString(R.string.pref_title_language))
        decorView.findViewById<TextView>(android.R.id.button1).text =
            localesCtx.getString(android.R.string.ok)
        decorView.findViewById<TextView>(android.R.id.button2).text =
            localesCtx.getString(android.R.string.cancel)
        decorView.findViewById<TextView>(android.R.id.button3).text =
            localesCtx.getString(R.string.label_translation)
    }

    override fun onPreOptionSelected(context: Context, option: Op): Boolean {
        if (option.value != MORE) {
            return false
        }
        val languageOptions = languageOptions.sortedWith(LangOpComparator(context))
        var choiceIndex = languageOptions.indexOfFirst { it.value == currentSelectedValue }
        val lastChoiceIndex = choiceIndex
        val languages = languageOptions.map { context.getString(it.titleRes) }.toTypedArray()
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_LanguagePref_MaterialAlertDialog)
            .setTitle(R.string.pref_title_language)
            .setSingleChoiceItems(languages, choiceIndex) { dialogInterface, index ->
                choiceIndex = index
                // change to locale text
                val value = languageOptions[index].value
                handleLocaleChoice(context, dialogInterface as Dialog, getLocaleByValue(value))
            }
            .setNeutralButton(R.string.label_translation) { _, _ ->
                CustomTabsBrowser.launchUrl(
                    context,
                    context.getString(R.string.url_translation).toUri()
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (choiceIndex != lastChoiceIndex) {
                    val op = languageOptions[choiceIndex]
                    apply(context, op)
                }
            }
            .show()
        // selected last value
        setSelectedOptionByValue(context, currentSelectedValue)
        return true
    }

    override fun apply(context: Context, option: Op) {
        val locales = getLocaleByValue(option.value)
        AppCompatDelegate.setApplicationLocales(locales)
        updateOptions(getOptions())// update options
    }
}