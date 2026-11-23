package com.dede.basic.provider

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface EasterEggProvider {
    fun provideEasterEgg(): BaseEasterEgg

    fun provideTimelineEvents(): List<TimelineEvent>
}

interface BaseEasterEgg {

    val versions: List<AndroidVersion>

    val primaryVersion: AndroidVersion
        get() = versions.first()

    val id: String
}

class EasterEggGroup(vararg val eggs: EasterEgg) : BaseEasterEgg {

    var selectedIndex: Int = 0

    override val versions: List<AndroidVersion> =
        eggs.flatMap { it.versions }.distinct().sortedBy { it.fullApiLevel }

    override val id: String
        get() = versions.first().id

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EasterEggGroup) {
            return false
        }
        return id == other.id
    }
}

/**
 * Resolves a public api level to the egg that owns it. When several eggs share a level
 * (e.g. 33 -> Tiramisu and T Beta), prefers stable releases over beta/preview and then
 * the highest [AndroidVersion.fullApiLevel].
 */
fun Iterable<EasterEgg>.findEggByPublicApiLevel(apiLevel: Int): EasterEgg? =
    filter { egg -> egg.versions.any { it.apiLevel == apiLevel } }
        .sortedWith(
            compareByDescending<EasterEgg> { it.version.releaseType.priority() }
                .thenByDescending { it.version.fullApiLevel }
        )
        .firstOrNull()

private fun ReleaseType.priority(): Int = when (this) {
    ReleaseType.RELEASE -> 3
    ReleaseType.BETA -> 2
    ReleaseType.PREVIEW -> 1
}

open class EasterEgg @JvmOverloads constructor(
    @DrawableRes val iconRes: Int,
    @StringRes val nameRes: Int,
    @StringRes val nicknameRes: Int,
    val version: AndroidVersion,
    val actionClass: Class<out Activity>? = null,
    val additionalVersions: List<AndroidVersion> = emptyList(),
) : BaseEasterEgg {

    override val versions: List<AndroidVersion> =
        (listOf(version) + additionalVersions).distinct().sortedBy { it.fullApiLevel }

    override val primaryVersion: AndroidVersion
        get() = version

    override val id: String
        get() = version.id

    open fun onEasterEggAction(context: Context): Boolean {
        return false
    }

    open fun provideSnapshotProvider(): SnapshotProvider? {
        return null
    }

    final override fun hashCode(): Int {
        return id.hashCode()
    }

    final override fun equals(other: Any?): Boolean {
        if (other !is EasterEgg) {
            return false
        }
        return id == other.id
    }

}
