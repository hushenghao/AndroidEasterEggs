package com.dede.android_eggs.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dede.android_eggs.preferences.PrefKey.Companion.boolean
import com.dede.android_eggs.preferences.PrefKey.Companion.int
import com.dede.android_eggs.preferences.PrefKey.Companion.string
import com.dede.android_eggs.util.pref

/**
 * A persisted setting: the storage key, the value read back when nothing has
 * been written yet, and the typed accessors for its SharedPreferences entry.
 *
 * Build one through [boolean], [int] or [string] instead of the constructor, so
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
    private val reader: SharedPreferences.(String, T) -> T,
    private val writer: SharedPreferences.Editor.(String, T) -> Unit,
) {

    fun get(context: Context): T {
        return context.pref.reader(name, default)
    }

    fun set(context: Context, value: T) {
        context.pref.edit { this.writer(name, value) }
    }

    /**
     * Writes through a caller-owned [SharedPreferences.Editor] so that several
     * settings can be committed in one transaction instead of one write each, see
     * `Utilities.saveVcsRevision` in `:feature:crash`.
     */
    fun set(editor: SharedPreferences.Editor, value: T) {
        editor.writer(name, value)
    }

    companion object {

        fun boolean(name: String, default: Boolean): PrefKey<Boolean> {
            return PrefKey(
                name = name,
                default = default,
                reader = { key, fallback -> getBoolean(key, fallback) },
                writer = { key, value -> putBoolean(key, value) },
            )
        }

        fun int(name: String, default: Int): PrefKey<Int> {
            return PrefKey(
                name = name,
                default = default,
                reader = { key, fallback -> getInt(key, fallback) },
                writer = { key, value -> putInt(key, value) },
            )
        }

        fun string(name: String, default: String?): PrefKey<String?> {
            return PrefKey(
                name = name,
                default = default,
                reader = { key, fallback -> getString(key, fallback) },
                writer = { key, value -> putString(key, value) },
            )
        }
    }
}
