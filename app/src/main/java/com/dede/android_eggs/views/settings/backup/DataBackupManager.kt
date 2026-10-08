package com.dede.android_eggs.views.settings.backup

import android.content.Context
import android.net.Uri
import com.dede.android_eggs.preferences.AppSettings
import com.dede.android_eggs.util.flushPendingWrites
import com.dede.android_eggs.util.makePreferencesName
import com.dede.android_eggs.views.settings.compose.prefs.AppIcon
import com.dede.android_eggs.views.settings.compose.prefs.AppIconPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.LanguagePrefUtil
import com.dede.basic.Utils
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal typealias BackupResult = Result<Unit>

internal object DataBackupManager {

    private const val BACKUP_VERSION = 1
    private const val BACKUP_JSON = "backup.json"

    // Bounds for untrusted zips: entries are buffered fully in memory, so an
    // import must cap entry count, per-entry size and total size.
    private const val MAX_ENTRY_COUNT = 10_000
    private const val MAX_ENTRY_BYTES = 32L * 1024 * 1024
    private const val MAX_TOTAL_BYTES = 64L * 1024 * 1024

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
        var entryCount = 0
        var totalBytes = 0L

        applicationContext.contentResolver.openInputStream(uri)?.use { `is` ->
            ZipInputStream(`is`).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (++entryCount > MAX_ENTRY_COUNT) {
                        throw IllegalArgumentException("Too many backup entries")
                    }
                    val bytes = zis.readEntryBytes(MAX_ENTRY_BYTES)
                    totalBytes += bytes.size
                    if (totalBytes > MAX_TOTAL_BYTES) {
                        throw IllegalArgumentException("Backup is too large: $totalBytes bytes")
                    }
                    zipEntries[entry.name] = bytes
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

        // Every check must pass before the destructive delete below: a rejected
        // import must abort without touching the existing app data.
        for (fileEntry in data.files) {
            validateRestorePath(fileEntry.path)
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

    // The manifest path is attacker-supplied: only the exported layout is
    // restorable, so a path can never leave appDataDir (".." rejected, no
    // absolute path passes the BACKUP_DIRS prefix check).
    private fun validateRestorePath(path: String) {
        val restorable = BACKUP_DIRS.any { dir -> path.startsWith("$dir/") } &&
            path.split('/').none { it == ".." }
        if (!restorable) {
            throw IllegalArgumentException("Invalid backup file path: $path")
        }
    }

    private fun ZipInputStream.readEntryBytes(limit: Long): ByteArray {
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var total = 0L
        while (true) {
            val read = read(buffer)
            if (read == -1) break
            total += read
            if (total > limit) {
                throw IllegalArgumentException("Backup entry is too large: $total bytes")
            }
            out.write(buffer, 0, read)
        }
        return out.toByteArray()
    }

    private fun flushPendingWrites(context: Context) {
        val prefNames = buildList {
            add(makePreferencesName(context.packageName))
            addAll(AppSettings.nekoPrefFiles)
        }
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
