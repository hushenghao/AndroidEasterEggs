@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.settings.compose

import android.app.LocaleConfig
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.basic.createLocalesContext
import com.dede.basic.getLayoutDirection
import java.util.Locale

object LanguagePrefUtil {

    private const val TAG = "LanguagePref"

    const val SYSTEM = 0
    private const val MORE = -1
    private const val SIMPLIFIED_CHINESE = 2    // zh-CN
    private const val TRADITIONAL_CHINESE = 3   // zh-TW
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
    private const val CZECH = 30                // cs-CZ
    private const val TAMIL = 31                // ta
    private const val ROMANIAN = 32             // ro-RO
    private const val SWEDISH = 33              // sv-SE

    class LangOp(
        val value: Int,
        @StringRes val titleRes: Int,
        @StringRes val localeTitleRes: Int,
        val locale: Locale
    )

    // @formatter:off
    private val languageOptions = listOf(
        LangOp(SIMPLIFIED_CHINESE,  R.string.language_zh_SC,  R.string.locale_lang_zh_SC,  Locale.SIMPLIFIED_CHINESE),
        LangOp(TRADITIONAL_CHINESE, R.string.language_zh_TC,  R.string.locale_lang_zh_TC,  Locale.TRADITIONAL_CHINESE),
        LangOp(ENGLISH,             R.string.language_en,     R.string.locale_lang_en,     Locale.ENGLISH),
        LangOp(RUSSIAN,             R.string.language_ru,     R.string.locale_lang_ru,     createLocale("ru")),
        LangOp(ITALIAN,             R.string.language_it,     R.string.locale_lang_it,     Locale.ITALIAN),
        LangOp(GERMANY,             R.string.language_de,     R.string.locale_lang_de,     Locale.GERMANY),
        LangOp(SPANISH,             R.string.language_es,     R.string.locale_lang_es,     createLocale("es")),
        LangOp(PORTUGAL,            R.string.language_pt,     R.string.locale_lang_pt,     createLocale("pt")),
        LangOp(PORTUGAL_BRAZIL,     R.string.language_pt_BR,  R.string.locale_lang_pt_BR,  createLocale("pt", "BR")),
        LangOp(INDONESIA,           R.string.language_in_ID,  R.string.locale_lang_in_ID,  createLocale("in", "ID")),
        LangOp(ARABIC,              R.string.language_ar_SA,  R.string.locale_lang_ar_SA,  createLocale("ar", "SA")),
        LangOp(CROATIAN,            R.string.language_hr_HR,  R.string.locale_lang_hr_HR,  createLocale("hr", "HR")),
        LangOp(FRENCH,              R.string.language_fr,     R.string.locale_lang_fr,     Locale.FRENCH),
        LangOp(POLISH,              R.string.language_pl_PL,  R.string.locale_lang_pl_PL,  createLocale("pl", "PL")),
        LangOp(DUTCH,               R.string.language_nl_NL,  R.string.locale_lang_nl_NL,  createLocale("nl", "NL")),
        //(LATIN,               R.string.language_la_LA,  R.string.locale_lang_la_LA,  createLocale("la", "LA")),
        LangOp(TURKISH,             R.string.language_tr_TR,  R.string.locale_lang_tr_TR,  createLocale("tr", "TR")),
        LangOp(UKRAINIAN,           R.string.language_uk_UA,  R.string.locale_lang_uk_UA,  createLocale("uk", "UA")),
        LangOp(JAPANESE,            R.string.language_ja_JP,  R.string.locale_lang_ja_JP,  Locale.JAPAN),
        LangOp(KOREAN,              R.string.language_ko,     R.string.locale_lang_ko,     Locale.KOREAN),
        LangOp(GREEK,               R.string.language_el_GR,  R.string.locale_lang_el_GR,  createLocale("el", "GR")),
        LangOp(FINNISH,             R.string.language_fi_FI,  R.string.locale_lang_fi_FI,  createLocale("fi", "FI")),
        LangOp(VIETNAMESE,          R.string.language_vi_VN,  R.string.locale_lang_vi_VN,  createLocale("vi", "VN")),
        LangOp(HUNGARIAN,           R.string.language_hu_HU,  R.string.locale_lang_hu_HU,  createLocale("hu", "HU")),
        LangOp(THAI,                R.string.language_th_TH,  R.string.locale_lang_th_TH,  createLocale("th", "TH")),
        LangOp(NORWEGIAN,           R.string.language_no_NO,  R.string.locale_lang_no_NO,  createLocale("no", "NO")),
        LangOp(FILIPINO,            R.string.language_fil_PH, R.string.locale_lang_fil_PH, createLocale("fil", "PH")),
        LangOp(LAO,                 R.string.language_lo_LA,  R.string.locale_lang_lo_LA,  createLocale("lo", "LA")),
        LangOp(CZECH,               R.string.language_cs_CZ,  R.string.locale_lang_cs_CZ,  createLocale("cs", "CZ")),
        LangOp(TAMIL,               R.string.language_ta,     R.string.locale_lang_ta,     createLocale("ta","IN")),
        LangOp(ROMANIAN,            R.string.language_ro,     R.string.locale_lang_ro,     createLocale("ro","RO")),
        LangOp(SWEDISH,             R.string.language_sv_SE,  R.string.locale_lang_sv_SE,  createLocale("sv","SE")),
    )
    // @formatter:on

    fun isSupported(): Boolean {
        // For API<24 the application does not have a localeList instead it has a single locale
        // Unsupported region
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    fun checkLocaleConfig(context: Context) {
        // check languageOptions count
        val expected = languageOptions.size
        var actual = HashSet(languageOptions).size
        check(expected == actual) {
            "Language option length, expected: %d, actual: %d."
                .format(expected, actual)
        }

        // check locale-config.xml locale count
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val localeConfig = LocaleConfig.fromContextIgnoringOverride(context)
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


    fun getLocaleByValue(value: Int): LocaleListCompat {
        val op = getLangOpByValue(value)
        if (op != null) {
            return LocaleListCompat.create(op.locale)
        }
        return LocaleListCompat.getEmptyLocaleList()
    }

    fun getValueByLocale(localeList: LocaleListCompat): Int {
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

    fun getLangOpByValue(value: Int): LangOp? {
        if (value == SYSTEM || value == MORE) {
            return null
        }
        return checkNotNull(languageOptions.find { it.value == value }) {
            "Not found language by value: $value"
        }
    }

    private fun createLocale(language: String, region: String = ""): Locale {
        return Locale.Builder()
            .setLanguage(language)
            .setRegion(region)
            .build()
    }

    fun getLanguages(context: Context): List<LangOp> {
        return languageOptions.sortedWith(LangOpComparator(context))
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
}

@Preview
@Composable
fun LanguagePref() {
    val applicationLocales = AppCompatDelegate.getApplicationLocales()

    var languageOptionValue by remember(applicationLocales) {
        mutableIntStateOf(LanguagePrefUtil.getValueByLocale(applicationLocales))
    }
    val langOp = remember(languageOptionValue) {
        LanguagePrefUtil.getLangOpByValue(languageOptionValue)
    }

    val onOptionClick = { option: Int ->
        languageOptionValue = option
        val locales = LanguagePrefUtil.getLocaleByValue(option)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    val context = LocalContext.current

    var moreDialogVisible by remember { mutableStateOf(false) }

    if (moreDialogVisible) {
        LanguageSelectedDialog(
            languageOptions = LanguagePrefUtil.getLanguages(context),
            currentLang = langOp,
            onDismissRequest = {
                moreDialogVisible = false
            },
            onLanguageSelected = {
                moreDialogVisible = false
                onOptionClick.invoke(it.value)
            }
        )
    }

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Language,
        title = stringResource(R.string.pref_title_language),
    ) {
        ValueOption(
            leadingIcon = imageVectorIconBlock(imageVector = Icons.Rounded.Android),
            shape = OptionShapes.firstShape(),
            title = stringResource(id = R.string.summary_system_default),
            trailingContent = radioButtonBlock(languageOptionValue == LanguagePrefUtil.SYSTEM),
            onOptionClick = onOptionClick,
            value = LanguagePrefUtil.SYSTEM
        )
        if (langOp != null) {
            ValueOption(
                leadingIcon = imageVectorIconBlock(imageVector = Icons.Rounded.Spellcheck),
                title = stringResource(id = langOp.titleRes),
                trailingContent = radioButtonBlock(languageOptionValue == langOp.value),
                onOptionClick = onOptionClick,
                value = langOp.value
            )
        }
        Option(
            shape = OptionShapes.lastShape(),
            leadingIcon = imageVectorIconBlock(imageVector = Icons.AutoMirrored.Rounded.FormatListBulleted),
            title = stringResource(id = R.string.pref_title_language_more),
            onClick = {
                moreDialogVisible = true
                if (BuildConfig.DEBUG) {
                    LanguagePrefUtil.checkLocaleConfig(context)
                }
            }
        )
    }
}

private fun LanguagePrefUtil.LangOp?.toLocalContext(base: Context): Context {
    if (this == null) {
        return base
    }
    return base.createLocalesContext(LocaleListCompat.create(this.locale))
}

@Composable
private fun LanguageSelectedDialog(
    languageOptions: List<LanguagePrefUtil.LangOp>,
    currentLang: LanguagePrefUtil.LangOp? = null,
    onDismissRequest: () -> Unit,
    onLanguageSelected: (LanguagePrefUtil.LangOp) -> Unit
) {
    val basicContext = LocalContext.current

    var selectedLangOp by remember { mutableStateOf(currentLang) }
    val localeContext = remember(selectedLangOp) {
        selectedLangOp.toLocalContext(basicContext)
    }
    val localLayoutDirection = remember(localeContext) {
        if (localeContext.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) LayoutDirection.Rtl else LayoutDirection.Ltr
    }
    // todo https://issuetracker.google.com/issues/204914500
    CompositionLocalProvider(
        LocalContext provides localeContext,
        LocalLayoutDirection provides localLayoutDirection
    ) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = localeContext.getString(R.string.pref_title_language),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(0.7f)
                ) {
                    items(languageOptions) {
                        Row(
                            modifier = Modifier.clickable {
                                selectedLangOp = it
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.padding(start = 14.dp, end = 16.dp)) {
                                RadioButton(selected = selectedLangOp == it, onClick = null)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = localeContext.getString(it.titleRes),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = localeContext.getString(it.localeTitleRes),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = localeContext.getString(android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val op = selectedLangOp
                    if (op != null) {
                        onLanguageSelected(op)
                    }
                }) {
                    Text(text = localeContext.getString(android.R.string.ok))
                }
            }
        )
    }
}
