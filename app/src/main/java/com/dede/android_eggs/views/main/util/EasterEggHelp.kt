package com.dede.android_eggs.views.main.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.dede.android_eggs.R
import com.dede.android_eggs.inject.EasterEggModules
import com.dede.basic.provider.AndroidVersion
import com.dede.basic.provider.EasterEgg
import com.dede.android_eggs.resources.R as StringsR


object EasterEggHelp {

    @Composable
    fun previewEasterEggs(): List<EasterEgg> {
        if (!LocalInspectionMode.current) {
            throw IllegalStateException("Only call from Inspection Mode")
        }
        return remember {
            EasterEggModules.providePureEasterEggList(
                EasterEggPreviewFactoryRegistry.createBaseEasterEggs()
            )
        }
    }

    class VersionFormatter private constructor(
        @StringRes val nicknameRes: Int,
        private vararg val versionNames: CharSequence,
    ) {

        companion object {
            fun create(
                versions: List<AndroidVersion>,
                @StringRes nicknameRes: Int = -1,
            ): VersionFormatter {
                return if (versions.size == 1) {
                    VersionFormatter(nicknameRes, versions.first().versionName)
                } else {
                    VersionFormatter(
                        nicknameRes,
                        versions.first().versionName,
                        versions.last().versionName,
                    )
                }
            }
        }

        fun format(context: Context): String {
            val enDash = context.getString(StringsR.string.char_en_dash)
            val sb = StringBuilder()
            versionNames.joinTo(sb, separator = enDash)
            return if (nicknameRes != -1) {
                val nickname = context.getString(nicknameRes)
                context.getString(
                    R.string.android_version_nickname_format,
                    sb.toString(), nickname,
                )
            } else {
                context.getString(R.string.android_version_format, sb.toString())
            }
        }
    }

    class ApiLevelFormatter private constructor(private val versions: List<AndroidVersion>) {

        companion object {
            fun create(versions: List<AndroidVersion>): ApiLevelFormatter {
                return ApiLevelFormatter(versions)
            }
        }

        fun format(context: Context): String {
            val first = versions.first()
            val last = versions.last()
            return if (first.apiLevelLabel == last.apiLevelLabel) {
                context.getString(R.string.api_version_format, last.apiLevelLabel)
            } else {
                val enDash = context.getString(StringsR.string.char_en_dash)
                context.getString(
                    R.string.api_version_format,
                    first.apiLevelLabel + enDash + last.apiLevelLabel,
                )
            }
        }
    }
}