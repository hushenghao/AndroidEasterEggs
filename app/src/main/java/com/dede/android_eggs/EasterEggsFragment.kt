package com.dede.android_eggs

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroupAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EasterEggsFragment : PreferenceFragmentCompat() {

    companion object {
        const val KEY_COLLECTION = "key_collection"
    }

    private lateinit var eggCollection: EggCollection

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        eggCollection = checkNotNull(findPreference(KEY_COLLECTION))
        eggCollection.setOnPreferenceChangeListener { _, newValue ->
            val isGrid = newValue as? Boolean ?: false
            if (isGrid) {
                Toast.makeText(requireContext(), "Wow!!!", Toast.LENGTH_SHORT).show()
            }
            listView?.layoutManager = createLayoutManager(isGrid)
            return@setOnPreferenceChangeListener true
        }
    }

    private inner class SpanSizeLookup : GridLayoutManager.SpanSizeLookup() {
        @SuppressLint("RestrictedApi")
        override fun getSpanSize(position: Int): Int {
            val adapter = listView.adapter as? PreferenceGroupAdapter
            val item = adapter?.getItem(position)
            if (item is EggPreference) {
                return 1
            }
            return 2
        }
    }

    private fun createLayoutManager(isGrid: Boolean): RecyclerView.LayoutManager {
        EggPreference.showSuffix = !isGrid
        return if (isGrid) {
            GridLayoutManager(requireContext(), 2).apply { spanSizeLookup = SpanSizeLookup() }
        } else {
            super.onCreateLayoutManager()
        }
    }

    override fun onCreateLayoutManager(): RecyclerView.LayoutManager {
        return createLayoutManager(eggCollection.isChecked())
    }
}