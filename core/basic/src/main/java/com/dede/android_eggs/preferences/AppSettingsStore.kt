package com.dede.android_eggs.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.dede.android_eggs.util.makePreferencesName
import com.dede.basic.ioScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val DATA_STORE_NAME = "app_settings"

private val Context.appSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    produceMigrations = { context ->
        listOf(
            // Migrate only AppSettings keys so the legacy XML file keeps AOSP egg
            // unlock timestamps (SpUtils) intact.
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = makePreferencesName(context.packageName),
                keysToMigrate = AppSettings.all.mapTo(LinkedHashSet()) { it.name },
            )
        )
    },
)

/**
 * Backing Preferences DataStore for [AppSettings], with an in-memory snapshot cache
 * for non-blocking synchronous reads and serialized disk writes.
 */
internal object AppSettingsStore {

    private val stateFlow = MutableStateFlow<Preferences?>(null)

    private val initLock = Any()
    private val writeLock = Any()
    private val writeMutex = Mutex()
    private var pendingWrites = 0
    private var lastWriteJob: Job? = null

    fun snapshot(context: Context): Preferences {
        stateFlow.value?.let { return it }
        return synchronized(initLock) {
            stateFlow.value ?: runBlocking {
                context.applicationContext.appSettingsDataStore.data.first()
            }.also { loaded ->
                stateFlow.value = loaded
            }
        }
    }

    fun dataFlow(context: Context): Flow<Preferences> {
        snapshot(context)
        return stateFlow.filterNotNull()
    }

    fun enqueueEdit(context: Context, transform: (MutablePreferences) -> Unit) {
        submitEdit(context, transform)
    }

    suspend fun editAndAwait(context: Context, transform: (MutablePreferences) -> Unit) {
        submitEdit(context, transform).join()
    }

    suspend fun flush() {
        val job = synchronized(writeLock) { lastWriteJob }
        job?.join()
        writeMutex.withLock { }
    }

    private fun submitEdit(
        context: Context,
        transform: (MutablePreferences) -> Unit,
    ): Job {
        val appContext = context.applicationContext
        snapshot(appContext)
        val newJob: Job
        synchronized(writeLock) {
            pendingWrites++
            stateFlow.value = (stateFlow.value ?: emptyPreferences())
                .toMutablePreferences()
                .apply(transform)
                .toPreferences()
            val previousJob = lastWriteJob
            newJob = ioScope.launch(start = CoroutineStart.LAZY) {
                var persisted: Preferences? = null
                try {
                    previousJob?.join()
                    persisted = writeMutex.withLock {
                        appContext.appSettingsDataStore.edit(transform)
                    }
                } finally {
                    synchronized(writeLock) {
                        pendingWrites--
                        if (persisted != null && pendingWrites == 0) {
                            stateFlow.value = persisted
                        }
                    }
                }
            }
            lastWriteJob = newJob
        }
        newJob.start()
        return newJob
    }
}
