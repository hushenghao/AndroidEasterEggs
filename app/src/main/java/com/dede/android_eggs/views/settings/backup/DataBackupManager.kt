package com.dede.android_eggs.views.settings.backup

import android.content.Context
import android.net.Uri
import com.dede.android_eggs.util.flushPendingWrites
import com.dede.android_eggs.util.makePreferencesName
import com.dede.android_eggs.views.settings.compose.prefs.AppIcon
import com.dede.android_eggs.views.settings.compose.prefs.AppIconPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.LanguagePrefUtil
import com.dede.basic.Utils
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal typealias BackupResult = Result<Unit>

internal object DataBackupManager {

    private const val BACKUP_VERSION = 1
    private const val BACKUP_JSON = "backup.json"

    private val BACKUP_DIRS = listOf("databases", "files", "shared_prefs")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun export(context: Context, uri: Uri): BackupResult = runCatching {
        val applicationContext = context.applicationContext
        val appDataDir = applicationContext.filesDir.parentFile
            ?: throw IllegalStateException("Cannot access app data directory")

        flushPendingWrites(applicationContext)

        val fileEntries = mutableListOf<Pair<String, ByteArray>>()

        for (dirName in BACKUP_DIRS) {
            val dir = File(appDataDir, dirName)
            if (!dir.exists()) continue
            dir.walkTopDown()
                .filter { file -> file.isFile && !file.name.startsWith('.') }
                .forEach { file ->
                    val relativePath = file.relativeTo(appDataDir).path
                    fileEntries.add(relativePath to file.readBytes())
                }
        }

        val timestamp = System.currentTimeMillis()
        val (versionName, versionCode) = Utils.getAppVersionPair(applicationContext)
        val currentIcon = AppIconPrefUtil.currentOrDefault(applicationContext)
        val data = BackupData(
            version = BACKUP_VERSION,
            timestamp = timestamp,
            appVersion = versionName,
            appVersionCode = versionCode,
            appIcon = currentIcon.name,
            appLanguage = LanguagePrefUtil.getApplicationLocalesValue(),
            files = fileEntries.map { (path, bytes) ->
                BackupFileEntry(path = path, sha256 = sha256(bytes))
            },
        )

        val jsonBytes = json.encodeToString(data).toByteArray(Charsets.UTF_8)

        applicationContext.contentResolver.openOutputStream(uri)?.use { os ->
            ZipOutputStream(os).use { zos ->
                zos.putNextEntry(ZipEntry(BACKUP_JSON))
                zos.write(jsonBytes)
                zos.closeEntry()

                for ((path, bytes) in fileEntries) {
                    zos.putNextEntry(ZipEntry(path))
                    zos.write(bytes)
                    zos.closeEntry()
                }
            }
        } ?: throw IllegalStateException("Unable to open output stream")
    }

    fun import(context: Context, uri: Uri): BackupResult = runCatching {
        val applicationContext = context.applicationContext
        val appDataDir = applicationContext.filesDir.parentFile
            ?: throw IllegalStateException("Cannot access app data directory")

        val zipEntries = mutableMapOf<String, ByteArray>()

        applicationContext.contentResolver.openInputStream(uri)?.use { `is` ->
            ZipInputStream(`is`).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    zipEntries[entry.name] = zis.readBytes()
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } ?: throw IllegalStateException("Unable to open input stream")

        val jsonBytes = zipEntries[BACKUP_JSON]
            ?: throw IllegalArgumentException("Invalid backup file: missing $BACKUP_JSON")
        val data = json.decodeFromString<BackupData>(jsonBytes.decodeToString())

        if (data.version != BACKUP_VERSION) {
            throw IllegalArgumentException("Unsupported backup version: ${data.version}")
        }

        for (fileEntry in data.files) {
            val actualBytes = zipEntries[fileEntry.path]
                ?: throw IllegalArgumentException("Missing file: ${fileEntry.path}")
            val actualHash = sha256(actualBytes)
            if (actualHash != fileEntry.sha256) {
                throw IllegalArgumentException("Corrupted file: ${fileEntry.path}")
            }
        }

        for (dirName in BACKUP_DIRS) {
            val dir = File(appDataDir, dirName)
            if (dir.exists()) {
                dir.deleteRecursively()
            }
        }

        for (fileEntry in data.files) {
            val bytes = zipEntries[fileEntry.path]!!
            val file = File(appDataDir, fileEntry.path)
            file.parentFile?.mkdirs()
            file.writeBytes(bytes)
        }

        if (data.appIcon != null) {
            runCatching {
                val icon = AppIcon.valueOf(data.appIcon)
                AppIconPrefUtil.switchIcon(applicationContext, icon)
            }
        }

        if (data.appLanguage != LanguagePrefUtil.SYSTEM) {
            LanguagePrefUtil.setApplicationLocalesValue(data.appLanguage)
        }
    }

    private fun flushPendingWrites(context: Context) {
        val prefNames = listOf(
            makePreferencesName(context.packageName),
            "N_mPrefs",
            "R_mPrefs",
            "S_mPrefs",
            "T_mPrefs",
        )
        for (name in prefNames) {
            val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            prefs.flushPendingWrites()
        }
    }

    private fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }
}
