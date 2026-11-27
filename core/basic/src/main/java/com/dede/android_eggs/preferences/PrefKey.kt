package com.dede.android_eggs.preferences

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dede.android_eggs.preferences.PrefKey.Companion.boolean
import com.dede.android_eggs.preferences.PrefKey.Companion.int
import com.dede.android_eggs.preferences.PrefKey.Companion.long
import com.dede.android_eggs.preferences.PrefKey.Companion.string
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * A persisted setting: the storage key, the value read back when nothing has
 * been written yet, and the typed accessors for its DataStore entry.
 *
 * Build one through [boolean], [int], [long] or [string] instead of the constructor, so
 * the stored type is declared by the factory rather than guessed from [default].
 * That is what keeps a nullable default such as [AppSettings.savedVcsRevision]
 * expressible.
 *
 * [name] is part of the on-disk format. Renaming it makes every existing
 * install fall back to [default], so a rename has to ship together with a
 * migration that copies the old value across.
 */
class PrefKey<T> private constructor(
    val name: String,
    val default: T,
    private val dataStoreKey: Preferences.Key<*>,
    private val reader: (Preferences) -> T,
    private val writer: (MutablePreferences, T) -> Unit,
) {

    fun get(context: Context): T {
        return reader(AppSettingsStore.snapshot(context))
    }

    /**
     * Whether a value has been written, i.e. [default] is the effective value
     * rather than a value the user chose. Needed by settings whose default
     * depends on the device capabilities, such as `colorSource`.
     */
    fun isSet(context: Context): Boolean {
        return AppSettingsStore.snapshot(context).contains(dataStoreKey)
    }

    fun set(context: Context, value: T) {
        AppSettingsStore.enqueueEdit(context) { prefs ->
            writeTo(prefs, value)
        }
    }

    fun flow(context: Context, fallback: T = default): Flow<T> {
        return AppSettingsStore.dataFlow(context)
            .map { prefs -> readFrom(prefs, fallback) }
            .distinctUntilChanged()
    }

    internal fun readFrom(prefs: Preferences, fallback: T = default): T {
        return if (prefs.contains(dataStoreKey)) reader(prefs) else fallback
    }

    internal fun writeTo(prefs: MutablePreferences, value: T) {
        writer(prefs, value)
    }

    internal fun removeFrom(prefs: MutablePreferences) {
        prefs.remove(dataStoreKey)
    }

    companion object {

        fun boolean(name: String, default: Boolean): PrefKey<Boolean> {
            val key = booleanPreferencesKey(name)
            return PrefKey(
                name = name,
                default = default,
                dataStoreKey = key,
                reader = { prefs -> prefs[key] ?: default },
                writer = { prefs, value -> prefs[key] = value },
            )
        }

        fun int(name: String, default: Int): PrefKey<Int> {
            val key = intPreferencesKey(name)
            return PrefKey(
                name = name,
                default = default,
                dataStoreKey = key,
                reader = { prefs -> prefs[key] ?: default },
                writer = { prefs, value -> prefs[key] = value },
            )
        }

        fun long(name: String, default: Long): PrefKey<Long> {
            val key = longPreferencesKey(name)
            return PrefKey(
                name = name,
                default = default,
                dataStoreKey = key,
                reader = { prefs -> prefs[key] ?: default },
                writer = { prefs, value -> prefs[key] = value },
            )
        }

        fun string(name: String, default: String?): PrefKey<String?> {
            val key = stringPreferencesKey(name)
            return PrefKey(
                name = name,
                default = default,
                dataStoreKey = key,
                reader = { prefs -> prefs[key] ?: default },
                writer = { prefs, value ->
                    if (value == null) {
                        prefs.remove(key)
                    } else {
                        prefs[key] = value
                    }
                },
            )
        }
    }
}

class AppSettingsEditor internal constructor(
    private val prefs: MutablePreferences,
) {
    fun <T> set(key: PrefKey<T>, value: T) {
        key.writeTo(prefs, value)
    }

    fun reset(key: PrefKey<*>) {
        key.removeFrom(prefs)
    }
}

class AppSettingsSnapshot internal constructor(
    private val prefs: Preferences,
) {
    fun <T> get(key: PrefKey<T>, fallback: T = key.default): T {
        return key.readFrom(prefs, fallback)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (other is AppSettingsSnapshot && prefs == other.prefs)
    }

    override fun hashCode(): Int {
        return prefs.hashCode()
    }
}
