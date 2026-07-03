package com.dede.android_eggs.views.settings.backup

import com.dede.android_eggs.views.settings.compose.prefs.LanguagePrefUtil
import kotlinx.serialization.Serializable

@Serializable
internal data class BackupFileEntry(
    val path: String,
    val sha256: String,
)

@Serializable
internal data class BackupData(
    val version: Int,
    val timestamp: Long,
    val appVersion: String? = null,
    val appVersionCode: Long = -1L,
    val appIcon: String? = null,
    val appLanguage: Int = LanguagePrefUtil.SYSTEM,
    val files: List<BackupFileEntry> = emptyList(),
)
