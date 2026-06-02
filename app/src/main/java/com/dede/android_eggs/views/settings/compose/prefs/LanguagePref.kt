@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.settings.compose.prefs

import android.app.LocaleConfig
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.local_provider.currentOutInspectionMode
import com.dede.android_eggs.views.main.compose.LocalDrawerState
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.basic.createLocalesContext
import kotlinx.coroutines.launch
import java.util.Locale
import com.dede.android_eggs.resources.R as StringsR

object LanguagePrefUtil {

    private const val TAG = "LanguagePref"

    const val SYSTEM = 0
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
    private const val KOREAN_KOREA = 11         // ko-KR
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
    private const val BURMESE = 34              // my-MM
    private const val BENGALI = 35              // bn-BD
    private const val ALBANIAN = 36             // sq
    private const val BULGARIAN = 37            // bg-BG

    @Stable
    @Immutable
    class LangOp(val value: Int, @StringRes val langRes: Int, val locale: Locale)

    val systemLangOp = LangOp(SYSTEM, StringsR.string.summary_system_default, Locale.getDefault())

    // @formatter:off
    private val languageOptions = listOf(
        LangOp(SIMPLIFIED_CHINESE,  StringsR.string.locale_lang_zh_SC,  Locale.SIMPLIFIED_CHINESE),
        LangOp(TRADITIONAL_CHINESE, StringsR.string.locale_lang_zh_TC,  Locale.TRADITIONAL_CHINESE),
        LangOp(ENGLISH,             StringsR.string.locale_lang_en,     Locale.ENGLISH),
        LangOp(RUSSIAN,             StringsR.string.locale_lang_ru,     createLocale("ru")),
        LangOp(ITALIAN,             StringsR.string.locale_lang_it,     Locale.ITALIAN),
        LangOp(GERMANY,             StringsR.string.locale_lang_de,     Locale.GERMANY),
        LangOp(SPANISH,             StringsR.string.locale_lang_es,     createLocale("es")),
        LangOp(PORTUGAL,            StringsR.string.locale_lang_pt,     createLocale("pt")),
        LangOp(PORTUGAL_BRAZIL,     StringsR.string.locale_lang_pt_BR,  createLocale("pt", "BR")),
        LangOp(INDONESIA,           StringsR.string.locale_lang_in_ID,  createLocale("in", "ID")),
        LangOp(ARABIC,              StringsR.string.locale_lang_ar_SA,  createLocale("ar", "SA")),
        LangOp(CROATIAN,            StringsR.string.locale_lang_hr_HR,  createLocale("hr", "HR")),
        LangOp(FRENCH,              StringsR.string.locale_lang_fr,     Locale.FRENCH),
        LangOp(POLISH,              StringsR.string.locale_lang_pl_PL,  createLocale("pl", "PL")),
        LangOp(DUTCH,               StringsR.string.locale_lang_nl_NL,  createLocale("nl", "NL")),
        LangOp(LATIN,               StringsR.string.locale_lang_la_LA,  createLocale("la", "LA")),
        LangOp(TURKISH,             StringsR.string.locale_lang_tr_TR,  createLocale("tr", "TR")),
        LangOp(UKRAINIAN,           StringsR.string.locale_lang_uk_UA,  createLocale("uk", "UA")),
        LangOp(JAPANESE,            StringsR.string.locale_lang_ja_JP,  Locale.JAPAN),
        LangOp(KOREAN_KOREA,        StringsR.string.locale_lang_ko_KR,  createLocale("ko","KR")),
        LangOp(GREEK,               StringsR.string.locale_lang_el_GR,  createLocale("el", "GR")),
        LangOp(FINNISH,             StringsR.string.locale_lang_fi_FI,  createLocale("fi", "FI")),
        LangOp(VIETNAMESE,          StringsR.string.locale_lang_vi_VN,  createLocale("vi", "VN")),
        LangOp(HUNGARIAN,           StringsR.string.locale_lang_hu_HU,  createLocale("hu", "HU")),
        LangOp(THAI,                StringsR.string.locale_lang_th_TH,  createLocale("th", "TH")),
        LangOp(NORWEGIAN,           StringsR.string.locale_lang_no_NO,  createLocale("no", "NO")),
        LangOp(FILIPINO,            StringsR.string.locale_lang_fil_PH, createLocale("fil", "PH")),
        LangOp(LAO,                 StringsR.string.locale_lang_lo_LA,  createLocale("lo", "LA")),
        LangOp(CZECH,               StringsR.string.locale_lang_cs_CZ,  createLocale("cs", "CZ")),
        LangOp(TAMIL,               StringsR.string.locale_lang_ta,     createLocale("ta","IN")),
        LangOp(ROMANIAN,            StringsR.string.locale_lang_ro,     createLocale("ro","RO")),
        LangOp(SWEDISH,             StringsR.string.locale_lang_sv_SE,  createLocale("sv","SE")),
        LangOp(BURMESE,             StringsR.string.locale_lang_my_MM,  createLocale("my","MM")),
        LangOp(BENGALI,             StringsR.string.locale_lang_bn_BD,  createLocale("bn","BD")),
        LangOp(ALBANIAN,            StringsR.string.locale_lang_sq,     createLocale("sq")),
        LangOp(BULGARIAN,           StringsR.string.locale_lang_bg,     createLocale("bg","BG")),
    )
    // @formatter:on

    fun isSupported(): Boolean {
        // For API<24 the application does not have a localeList instead it has a single locale
        // Unsupported region
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    internal fun checkLocaleConfig(context: Context) {
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
            "android.androidResources.localeFilters length, expected: %d, actual: %d."
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
        if (value == SYSTEM) {
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

    private class LangOpComparator(private val context: Context) : Comparator<LangOp> {

        private val LangOp.compareValue: Int
            get() = when (value) {
                SIMPLIFIED_CHINESE -> -2// first in list
                TRADITIONAL_CHINESE -> -1
                else -> 0
            }

        private val LangOp.compareName: String
            get() = toLocalContext(context).getString(langRes)

        override fun compare(o1: LangOp, o2: LangOp): Int {
            var r = o1.compareValue.compareTo(o2.compareValue)
            if (r == 0) {
                r = o1.compareName.compareTo(o2.compareName)
            }
            return r
        }
    }
}

private fun launchAppLocaleSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
            .setData("package:${context.packageName}".toUri())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
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

    val context = LocalContext.current
    val drawerState = LocalDrawerState.currentOutInspectionMode
    val scope = rememberCoroutineScope()

    fun performOnOptionSelected(option: Int) {
        scope.launch {
            drawerState?.close()

            languageOptionValue = option
            val locales = LanguagePrefUtil.getLocaleByValue(option)
            try {
                AppCompatDelegate.setApplicationLocales(locales)
            } catch (_: RuntimeException) {
                // https://issuetracker.google.com/issues/318314368
                launchAppLocaleSettings(context)
            }
        }
    }

    var moreSheetVisible by remember { mutableStateOf(false) }

    if (moreSheetVisible) {
        LanguageSelectedBottomSheet(
            languageOptions = LanguagePrefUtil.getLanguages(context),
            selectedLangOp = langOp,
            onDismissRequest = {
                moreSheetVisible = false
            },
            onLanguageSelected = { value ->
                moreSheetVisible = false
                performOnOptionSelected(value)
            }
        )
    }

    SettingPref(
        leadingIcon = Icons.Rounded.Language,
        title = stringResource(StringsR.string.pref_title_language),
        desc = langOp?.let { stringResource(id = it.langRes) },
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            moreSheetVisible = true
            if (BuildConfig.DEBUG) {
                LanguagePrefUtil.checkLocaleConfig(context)
            }
        }
    )
}

private fun LanguagePrefUtil.LangOp?.toLocalContext(base: Context): Context {
    if (this == null) {
        return base
    }
    return base.createLocalesContext(LocaleListCompat.create(this.locale))
}

@Composable
@Preview
private fun LanguageSelectedBottomSheetPreview() {
    val context = LocalContext.current
    val languages = remember(context) { LanguagePrefUtil.getLanguages(context) }
    LanguageSelectedBottomSheet(
        languageOptions = languages,
        onDismissRequest = {},
        onLanguageSelected = {}
    )
}

@Composable
private fun LanguageSelectedBottomSheet(
    languageOptions: List<LanguagePrefUtil.LangOp>,
    selectedLangOp: LanguagePrefUtil.LangOp? = null,
    onDismissRequest: () -> Unit,
    onLanguageSelected: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lazyListState = rememberLazyListState()
    val sheetGesturesEnabled by remember {
        // disable sheet gestures when the list can scroll backward
        derivedStateOf { !lazyListState.canScrollBackward }
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        contentWindowInsets = {
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
        },
        sheetGesturesEnabled = sheetGesturesEnabled,
    ) {
        val paddingValues = WindowInsets.safeDrawing.asPaddingValues()
        val layoutDirection = LocalLayoutDirection.current
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(
                    start = paddingValues.calculateStartPadding(layoutDirection) + 14.dp,
                    end = paddingValues.calculateEndPadding(layoutDirection) + 14.dp,
                    bottom = paddingValues.calculateBottomPadding(),
                )
        ) {
            item {
                LanguageItem(
                    langOp = LanguagePrefUtil.systemLangOp,
                    selected = selectedLangOp == null || selectedLangOp.value == LanguagePrefUtil.SYSTEM
                ) {
                    onLanguageSelected(LanguagePrefUtil.SYSTEM)
                }
            }
            items(languageOptions, key = { it.value }) {
                LanguageItem(it, it == selectedLangOp) {
                    onLanguageSelected(it.value)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LanguageItem(
    langOp: LanguagePrefUtil.LangOp = LanguagePrefUtil.systemLangOp,
    selected: Boolean = true,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val localeContext = remember(langOp) { langOp.toLocalContext(context) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier.padding(horizontal = 12.dp),
            selected = selected,
            onClick = onClick,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = localeContext.getString(langOp.langRes),
                style = typography.titleMedium.copy(color = colorScheme.onSurface),
            )
            Text(
                text = stringResource(langOp.langRes),
                style = typography.bodySmall,
            )
        }
    }
}
