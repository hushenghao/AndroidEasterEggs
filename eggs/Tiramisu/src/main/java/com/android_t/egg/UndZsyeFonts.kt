package com.android_t.egg

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.graphics.fonts.SystemFonts
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import com.dede.basic.utils.DynamicObjectUtils
import com.dede.basic.utils.dynamic.DynamicResult.Companion.getTypeValue
import java.io.File

/**
 * Find NotoColorEmoji font
 *
 * [Implement custom fonts](https://source.android.com/docs/core/fonts/custom-font-fallback)
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("KDocUnresolvedReference")
@SuppressLint("PrivateApi")
internal object UndZsyeFonts {

    private const val LANGUAGE_TAGS = "und-Zsye"

    /**
     * Parse `/system/etc/fonts.xml`
     * @see [SystemFonts.getSystemPreinstalledFontConfig]
     * @see [android.text.FontConfig]
     */
    private fun getSystemPreinstalledFontConfig(): Any? {
        return DynamicObjectUtils.asDynamicObject(SystemFonts::class)
            .invokeMethod("getSystemPreinstalledFontConfig")
            .getValue()// android.text.FontConfig
    }

    /**
     * Get font families from [android.text.FontConfig]
     * @see [android.text.FontConfig.getFontFamilies]
     */
    private fun getFontFamilies(fontConfig: Any): List<*>? {
        // android.text.FontConfig
        return DynamicObjectUtils.asDynamicObject(fontConfig)
            .invokeMethod("getFontFamilies")
            .getTypeValue(List::class)// List<android.text.FontConfig$FontFamily>
    }

    /**
     * Find first locale is `und-Zsye` font.
     * @see [android.text.FontConfig.FontFamily.getLocaleList]
     * @see [android.text.FontConfig.FontFamily.getFontList]
     * @see [android.text.FontConfig.Font]
     */
    private fun findFirstUndZsyeFont(fontFamilys: List<Any?>): Any? {
        // List<android.text.FontConfig$FontFamily>
        for (fontFamily in fontFamilys) {
            // android.text.FontConfig$FontFamily
            if (fontFamily == null) continue
            val localeList: LocaleList = DynamicObjectUtils.asDynamicObject(fontFamily)
                .invokeMethod("getLocaleList")
                .getTypeValue(LocaleList::class) ?: continue
            if (localeList.toLanguageTags() != LANGUAGE_TAGS) {
                continue
            }
            val fonts = DynamicObjectUtils.asDynamicObject(fontFamily)
                .invokeMethod("getFontList")
                .getTypeValue(List::class)// List<android.text.FontConfig$Font>
            if (!fonts.isNullOrEmpty()) {
                return fonts[0]// android.text.FontConfig$Font
            }
        }
        return null
    }

    /**
     * Get font file
     * @see [android.text.FontConfig.Font]
     */
    private fun getFontFile(font: Any): File? {
        // android.text.FontConfig$Font
        return DynamicObjectUtils.asDynamicObject(font)
            .invokeMethod("getFile")
            .getTypeValue(File::class)
    }

    /**
     * Find first locale is `und-Zsye` font file.
     *
     * `und-Zsye` is emoji font, declare in `/system/etc/fonts.xml`.
     * OEM manufacturers may modify file declarations, so [SystemFonts.getSystemPreinstalledFontConfig] is used to read.
     *
     * ```xml
     * <family lang="und-Zsye">
     *  <font weight="400" style="normal">NotoColorEmoji.ttf</font>
     * </family>
     * ```
     *
     * @see [SystemFonts.getSystemPreinstalledFontConfig]
     * @see [Typeface.loadPreinstalledSystemFontMap]
     */
    fun findFirstUndZsyeFontFile(): File? {
        // android.text.FontConfig
        val fontConfig = getSystemPreinstalledFontConfig() ?: return null
        // List<android.text.FontConfig$FontFamily>
        val fontFamilys = getFontFamilies(fontConfig) ?: return null
        // android.text.FontConfig$Font
        val undZsyeFont = findFirstUndZsyeFont(fontFamilys) ?: return null
        // java.io.File
        return getFontFile(undZsyeFont)
    }

}