@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import com.dede.android_eggs.navigation.BottomSheetSceneStrategy.Companion.bottomSheet

// https://github.com/android/nav3-recipes/blob/main/app/src/main/java/com/example/nav3recipes/bottomsheet/BottomSheetSceneStrategy.kt
/** An [OverlayScene] that renders an [entry] within a [ModalBottomSheet]. */
class BottomSheetScene<T : Any>(
    override val key: Any,
    private val entry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val properties: ModalBottomSheetProperties,
    private val onBack: () -> Unit,
    private val customBottomSheet: Boolean,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable () -> Unit = {
        if (!customBottomSheet) {
            ModalBottomSheet(onDismissRequest = onBack, properties = properties) {
                entry.Content()
            }
        } else {
            entry.Content()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BottomSheetScene<*>) return false

        if (key != other.key) return false
        if (entry != other.entry) return false
        if (previousEntries != other.previousEntries) return false
        if (overlaidEntries != other.overlaidEntries) return false
        if (properties != other.properties) return false
        if (customBottomSheet != other.customBottomSheet) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + entry.hashCode()
        result = 31 * result + previousEntries.hashCode()
        result = 31 * result + overlaidEntries.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + customBottomSheet.hashCode()
        return result
    }

    override fun toString(): String {
        return "ModalBottomSheetScene(properties=$properties, overlaidEntries=$overlaidEntries, previousEntries=$previousEntries, entry=$entry, key=$key, customBottomSheet=$customBottomSheet)"
    }

}

/**
 * A [SceneStrategy] that displays entries that have added [bottomSheet] to their [NavEntry.metadata]
 * within a [ModalBottomSheet] instance.
 *
 * This strategy should always be added before any non-overlay scene strategies.
 */
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        val properties =
            lastEntry?.metadata?.get(BOTTOM_SHEET_KEY) as? ModalBottomSheetProperties
                ?: return null
        val customBottomSheet = lastEntry.metadata[CUSTOM_BOTTOM_SHEET_KEY] as? Boolean ?: false
        return BottomSheetScene(
            key = lastEntry.contentKey,
            entry = lastEntry,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            properties = properties,
            onBack = onBack,
            customBottomSheet = customBottomSheet,
        )
    }

    companion object {
        /**
         * Function to be called on the [NavEntry.metadata] to mark this entry as something that
         * should be displayed within a [ModalBottomSheet].
         *
         * @param properties properties that should be passed to the containing [ModalBottomSheetProperties].
         */
        fun bottomSheet(
            properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
            customBottomSheet: Boolean = false,
        ): Map<String, Any> = mapOf(
            BOTTOM_SHEET_KEY to properties,
            CUSTOM_BOTTOM_SHEET_KEY to customBottomSheet
        )

        internal const val BOTTOM_SHEET_KEY = "bottom_sheet"
        internal const val CUSTOM_BOTTOM_SHEET_KEY = "custom_bottom_sheet"
    }
}
