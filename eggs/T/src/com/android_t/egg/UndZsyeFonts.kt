package com.android_t.egg

import android.annotation.SuppressLint
import android.graphics.fonts.SystemFonts
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import com.dede.basic.DynamicInvokeResult.Companion.getValue
import com.dede.basic.DynamicObjectUtils
import java.io.File

/**
 * Find NotoColorEmoji font
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("PrivateApi")
object UndZsyeFonts {

    private const val LANGUAGE_TAGS = "und-Zsye"

    private fun getSystemPreinstalledFontConfig(): Any? {
        return DynamicObjectUtils.asDynamicObject(SystemFonts::class.java)
            .tryInvokeMethod("getSystemPreinstalledFontConfig")
            .getValue()// android.text.FontConfig
    }

    private fun getFontFamilies(fontConfig: Any): List<*>? {
        // android.text.FontConfig
        return DynamicObjectUtils.asDynamicObject(fontConfig, "android.text.FontConfig")
            .tryInvokeMethod("getFontFamilies")
            .getValue<List<*>>()// List<android.text.FontConfig$FontFamily>
    }

    private fun findFirstUndZsyeFont(fontFamilys: List<Any?>): Any? {
        // List<android.text.FontConfig$FontFamily>
        for (fontFamily in fontFamilys) {
            // android.text.FontConfig$FontFamily
            if (fontFamily == null) continue
            val localeList: LocaleList = DynamicObjectUtils.asDynamicObject(fontFamily)
                .tryInvokeMethod("getLocaleList")
                .getValue<LocaleList>() ?: continue
            if (localeList.toLanguageTags() != LANGUAGE_TAGS) {
                continue
            }
            val fonts = DynamicObjectUtils.asDynamicObject(fontFamily)
                .tryInvokeMethod("getFontList")
                .getValue<List<Any?>>()// List<android.text.FontConfig$Font>
            if (!fonts.isNullOrEmpty()) {
                return fonts[0]// android.text.FontConfig$Font
            }
        }
        return null
    }

    private fun getFontFile(font: Any): File? {
        // android.text.FontConfig$Font
        return DynamicObjectUtils.asDynamicObject(font)
            .tryInvokeMethod("getFile")
            .getValue<File>()
    }

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