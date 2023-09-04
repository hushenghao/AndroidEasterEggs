package com.dede.android_eggs.views.settings.prefs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.Icons.Outlined.language
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.SettingPref
import com.dede.basic.createLocalesContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale


class LanguagePref : SettingPref(null, getOptions(), SYSTEM) {
    companion object {

        // For API<24 the application does not have a localeList instead it has a single locale
        // Unsupported region
        private val isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

        private const val SYSTEM = 0
        private const val MORE = -1
        private const val SIMPLIFIED_CHINESE = 2    // zh-CN
        private const val TRADITIONAL_CHINESE = 3   // zh-HK, zh-TW
        private const val ENGLISH = 4               // en
        private const val RUSSIAN = 5               // ru
        private const val ITALIAN = 6               // it
        private const val GERMANY = 7               // de
        private const val PORTUGAL = 8              // es
        private const val INDONESIA = 9             // in-ID

        private val Locale_RUSSIAN = createLocale("ru", "")
        private val Locale_PORTUGAL = createLocale("es", "")
        private val Locale_INDONESIA = createLocale("in", "ID")

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

        private val languageOptions = listOf(
            Op(SIMPLIFIED_CHINESE, titleRes = R.string.language_zh_sc),
            Op(TRADITIONAL_CHINESE, titleRes = R.string.language_zh_tc),
            Op(ENGLISH, titleRes = R.string.language_en),
            Op(RUSSIAN, titleRes = R.string.language_ru),
            Op(ITALIAN, titleRes = R.string.language_it),
            Op(GERMANY, titleRes = R.string.language_de),
            Op(PORTUGAL, titleRes = R.string.language_es),
            Op(INDONESIA, titleRes = R.string.language_in_id),
        )

        private fun getLocaleByValue(value: Int): LocaleListCompat {
            return when (value) {
                SIMPLIFIED_CHINESE -> LocaleListCompat.create(Locale.SIMPLIFIED_CHINESE)
                TRADITIONAL_CHINESE -> LocaleListCompat.create(Locale.TRADITIONAL_CHINESE)
                ENGLISH -> LocaleListCompat.create(Locale.ENGLISH)
                RUSSIAN -> LocaleListCompat.create(Locale_RUSSIAN)
                ITALIAN -> LocaleListCompat.create(Locale.ITALIAN)
                GERMANY -> LocaleListCompat.create(Locale.GERMANY)
                PORTUGAL -> LocaleListCompat.create(Locale_PORTUGAL)
                INDONESIA -> LocaleListCompat.create(Locale_INDONESIA)
                else -> LocaleListCompat.getEmptyLocaleList()
            }
        }

        private fun getValueByLocale(localeList: LocaleListCompat): Int {
            if (localeList.isEmpty) {
                return SYSTEM
            }
            return when (localeList.get(0)) {
                Locale.SIMPLIFIED_CHINESE -> SIMPLIFIED_CHINESE
                Locale.TRADITIONAL_CHINESE -> TRADITIONAL_CHINESE
                Locale.ENGLISH -> ENGLISH
                Locale_RUSSIAN -> RUSSIAN
                Locale.ITALIAN -> ITALIAN
                Locale.GERMANY -> GERMANY
                Locale_PORTUGAL -> PORTUGAL
                Locale_INDONESIA -> INDONESIA
                else -> SYSTEM
            }
        }

        fun getApplicationLocale(): Locale {
            val locales = AppCompatDelegate.getApplicationLocales()
            return if (locales.isEmpty) {
                Locale.getDefault()
            } else {
                locales.get(0) ?: Locale.getDefault()
            }
        }

        fun resetApi23Locale() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            }
        }

        private fun createLocale(language: String, region: String): Locale {
            return Locale.Builder()
                .setLanguage(language)
                .setRegion(region)
                .build()
        }
    }

    override val titleRes: Int
        get() = R.string.pref_title_language

    override val enable: Boolean
        get() = isEnabled

    override fun setValue(context: Context, value: Int): Boolean {
        return false
    }

    override fun getValue(context: Context, default: Int): Int {
        return getValueByLocale(AppCompatDelegate.getApplicationLocales())
    }

    private fun restoreLastOption(lastValue: Int) {
        val selectedOp = options.find { it.value == lastValue }
        if (selectedOp != null) {
            selectedOption(selectedOp)
        }
    }

    override fun onPreOptionSelected(context: Context, option: Op): Boolean {
        if (option.value != MORE) {
            return false
        }
        var choiceIndex = languageOptions.indexOfFirst { it.value == selectedValue }
        val lastChoiceIndex = choiceIndex
        val languages = languageOptions.map { context.getString(it.titleRes) }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.pref_title_language)
            .setSingleChoiceItems(languages, choiceIndex) { dialogInterface, index ->
                choiceIndex = index
                // change to locale text
                val dialog = dialogInterface as Dialog
                val value = languageOptions[index].value
                val locales = getLocaleByValue(value)
                val localesCtx = context.createLocalesContext(locales)
                dialog.setTitle(localesCtx.getString(R.string.pref_title_language))
                dialog.findViewById<TextView>(android.R.id.button1)?.text =
                    localesCtx.getString(android.R.string.ok)
                dialog.findViewById<TextView>(android.R.id.button2)?.text =
                    localesCtx.getString(android.R.string.cancel)
                dialog.findViewById<TextView>(android.R.id.button3)?.text =
                    localesCtx.getString(R.string.label_translation)
            }
            .setOnDismissListener {
                if (lastChoiceIndex == choiceIndex) {
                    restoreLastOption(selectedValue)
                }
            }
            .setNeutralButton(R.string.label_translation) { _, _ ->
                CustomTabsBrowser.launchUrl(
                    context,
                    context.getString(R.string.url_translation).toUri()
                )
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                restoreLastOption(selectedValue)
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (choiceIndex != lastChoiceIndex) {
                    onOptionSelected(context, languageOptions[choiceIndex])
                }
            }
            .show()
        return true
    }

    override fun onOptionSelected(context: Context, option: Op) {
        AppCompatDelegate.setApplicationLocales(getLocaleByValue(option.value))
    }
}