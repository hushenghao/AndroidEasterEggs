package com.dede.android_eggs.main

import android.content.Context
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.SparseArray
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.views.main.EasterEggModules
import com.dede.android_eggs.views.settings.compose.IconShapePrefUtil
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.requireDrawable
import dagger.Module
import java.text.Format
import java.util.Date
import java.util.Locale


object EasterEggHelp {

    @Composable
    fun previewEasterEggs(): List<EasterEgg> {
        if (!LocalInspectionMode.current) {
            throw IllegalStateException("Only call from Inspection Mode")
        }
        val module = EasterEggModules::class.java.getAnnotation(Module::class.java)
            ?: throw IllegalStateException("EasterEggModules is empty")
        val baseEasterEggs = module.includes.map {
            val instance = try {
                it.java.getField("INSTANCE").get(null)
            } catch (e: Exception) {
                it.java.getConstructor().newInstance()
            }
            val provider = instance as EasterEggProvider
            provider.provideEasterEgg()
        }
        return EasterEggModules.providePureEasterEggList(baseEasterEggs)
    }

    class DateFormatter private constructor(pattern: String, locale: Locale) {

        companion object {
            fun getInstance(pattern: String): DateFormatter {
                return DateFormatter(pattern, getApplicationLocale())
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

        fun format(date: Date): String {
            return format.format(date)
        }
    }

    class VersionFormatter private constructor(
        @StringRes val nicknameRes: Int,
        private vararg val versionNames: CharSequence,
    ) {

        companion object {
            fun create(apiLevel: IntRange, @StringRes nicknameRes: Int = -1): VersionFormatter {
                return if (apiLevel.first == apiLevel.last) {
                    VersionFormatter(
                        nicknameRes,
                        getVersionNameByApiLevel(apiLevel.first)
                    )
                } else {
                    VersionFormatter(
                        nicknameRes,
                        getVersionNameByApiLevel(apiLevel.first),
                        getVersionNameByApiLevel(apiLevel.last),
                    )
                }
            }
        }

        fun format(context: Context): String {
            val enDash = context.getString(R.string.char_en_dash)
            val sb = StringBuilder()
            versionNames.joinTo(sb, separator = enDash)
            return if (nicknameRes != -1) {
                val nickname = context.getString(nicknameRes)
                context.getString(
                    R.string.android_version_nickname_format,
                    sb.toString(), nickname
                )
            } else {
                context.getString(R.string.android_version_format, sb.toString())
            }
        }
    }

    class ApiLevelFormatter private constructor(private val apiLevel: IntRange) {

        companion object {
            fun create(apiLevel: IntRange): ApiLevelFormatter {
                return ApiLevelFormatter(apiLevel)
            }
        }

        fun format(context: Context): String {
            return if (apiLevel.first == apiLevel.last) {
                context.getString(R.string.api_version_format, apiLevel.first.toString())
            } else {
                val enDash = context.getString(R.string.char_en_dash)
                context.getString(R.string.api_version_format, apiLevel.join(enDash))
            }
        }
    }

    private fun IntRange.join(s: CharSequence): CharSequence {
        return StringBuilder()
            .append(first)
            .append(s)
            .append(last)
    }

    fun EasterEgg.getIcon(context: Context): Drawable {
        if (supportAdaptiveIcon) {
            val pathStr = IconShapePrefUtil.getMaskPath(context)
            return AlterableAdaptiveIconDrawable(context, iconRes, pathStr)
        }
        return context.requireDrawable(iconRes)
    }

    fun getVersionNameByApiLevel(level: Int): String {
        return apiLevelArrays[level]
            ?: throw IllegalArgumentException("Illegal Api level: $level")
    }

    private val apiLevelArrays = SparseArray<String>()

    init {
        apiLevelArrays[AndroidPreviewHelp.API] = AndroidPreviewHelp.API_VERSION_NAME
        apiLevelArrays[Build.VERSION_CODES.UPSIDE_DOWN_CAKE] = "14"
        apiLevelArrays[Build.VERSION_CODES.TIRAMISU] = "13"
        apiLevelArrays[Build.VERSION_CODES.S_V2] = "12L"
        apiLevelArrays[Build.VERSION_CODES.S] = "12"
        apiLevelArrays[Build.VERSION_CODES.R] = "11"
        apiLevelArrays[Build.VERSION_CODES.Q] = "10"
        apiLevelArrays[Build.VERSION_CODES.P] = "9"
        apiLevelArrays[Build.VERSION_CODES.O_MR1] = "8.1"
        apiLevelArrays[Build.VERSION_CODES.O] = "8.0"
        apiLevelArrays[Build.VERSION_CODES.N_MR1] = "7.1"
        apiLevelArrays[Build.VERSION_CODES.N] = "7.0"
        apiLevelArrays[Build.VERSION_CODES.M] = "6.0"
        apiLevelArrays[Build.VERSION_CODES.LOLLIPOP_MR1] = "5.1"
        apiLevelArrays[Build.VERSION_CODES.LOLLIPOP] = "5.0"
        apiLevelArrays[Build.VERSION_CODES.KITKAT_WATCH] = "4.4W"
        apiLevelArrays[Build.VERSION_CODES.KITKAT] = "4.4"
        apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN_MR2] = "4.3"
        apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN_MR1] = "4.2"
        apiLevelArrays[Build.VERSION_CODES.JELLY_BEAN] = "4.1"
        apiLevelArrays[Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1] = "4.0.3"
        apiLevelArrays[Build.VERSION_CODES.ICE_CREAM_SANDWICH] = "4.0"
        apiLevelArrays[Build.VERSION_CODES.HONEYCOMB_MR2] = "3.2"
        apiLevelArrays[Build.VERSION_CODES.HONEYCOMB_MR1] = "3.1"
        apiLevelArrays[Build.VERSION_CODES.HONEYCOMB] = "3.0"
        apiLevelArrays[Build.VERSION_CODES.GINGERBREAD_MR1] = "2.3.3"
        apiLevelArrays[Build.VERSION_CODES.GINGERBREAD] = "2.3"
        apiLevelArrays[Build.VERSION_CODES.FROYO] = "2.2"
        apiLevelArrays[Build.VERSION_CODES.ECLAIR_MR1] = "2.1"
        apiLevelArrays[Build.VERSION_CODES.ECLAIR] = "2.0"
        apiLevelArrays[Build.VERSION_CODES.DONUT] = "1.6"
        apiLevelArrays[Build.VERSION_CODES.CUPCAKE] = "1.5"
        apiLevelArrays[Build.VERSION_CODES.BASE_1_1] = "1.1"
        apiLevelArrays[Build.VERSION_CODES.BASE] = "1.0"
    }

}
