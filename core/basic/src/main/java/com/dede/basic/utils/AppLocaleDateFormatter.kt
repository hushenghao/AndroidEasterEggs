package com.dede.basic.utils

import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import java.text.DateFormat
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.util.Date
import java.util.Locale


class AppLocaleDateFormatter private constructor(pattern: String, locale: Locale) : DateFormat() {

    companion object {
        fun getInstance(pattern: String): AppLocaleDateFormatter {
            return AppLocaleDateFormatter(pattern, getApplicationLocale())
        }

        private fun getApplicationLocale(): Locale {
            val locales = AppCompatDelegate.getApplicationLocales()
            return if (locales.isEmpty) {
                Locale.getDefault()
            } else {
                locales.get(0) ?: Locale.getDefault()
            }
        }
    }

    private val format: Format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat(pattern, locale)
    } else {
        java.text.SimpleDateFormat(pattern, locale)
    }

    override fun format(
        date: Date,
        toAppendTo: StringBuffer,
        fieldPosition: FieldPosition
    ): StringBuffer {
        return format.format(date, toAppendTo, fieldPosition)
    }

    override fun parse(source: String, pos: ParsePosition): Date? {
        return format.parseObject(source, pos) as? Date
    }

}
