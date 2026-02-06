@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

class ModalBottomSheetScene<T : Any>(
    override val key: Any,
    private val entry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val properties: ModalBottomSheetProperties,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)


    override val content: @Composable () -> Unit = {
        entry.Content()
//        ModalBottomSheet(onDismissRequest = onBack, properties = properties) { entry.Content() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModalBottomSheetScene<*>) return false

        if (key != other.key) return false
        if (entry != other.entry) return false
        if (previousEntries != other.previousEntries) return false
        if (overlaidEntries != other.overlaidEntries) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + entry.hashCode()
        result = 31 * result + previousEntries.hashCode()
        result = 31 * result + overlaidEntries.hashCode()
        result = 31 * result + properties.hashCode()
        return result
    }

    override fun toString(): String {
        return "ModalBottomSheetScene(properties=$properties, overlaidEntries=$overlaidEntries, previousEntries=$previousEntries, entry=$entry, key=$key)"
    }

}

class ModalBottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        val properties =
            lastEntry?.metadata?.get(MODAL_BOTTOM_SHEET_KEY) as? ModalBottomSheetProperties
                ?: return null
        return ModalBottomSheetScene(
            key = lastEntry.contentKey,
            entry = lastEntry,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            properties = properties,
            onBack = onBack,
        )
    }

    companion object {
        /**
         * Function to be called on the [NavEntry.metadata] to mark this entry as something that
         * should be displayed within a [Dialog].
         *
         * @param properties properties that should be passed to the containing [ModalBottomSheetProperties].
         */
        fun modalBottomSheet(
            properties: ModalBottomSheetProperties = ModalBottomSheetProperties()
        ): Map<String, Any> = mapOf(MODAL_BOTTOM_SHEET_KEY to properties)

        internal const val MODAL_BOTTOM_SHEET_KEY = "modal_bottom_sheet"
    }
}
