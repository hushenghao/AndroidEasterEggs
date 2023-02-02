package com.dede.android_eggs

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(listView) { v, insets ->
            val recyclerView = v as RecyclerView
            val old = recyclerView.tag as? EdgeItemDecoration
            if (old != null) {
                recyclerView.removeItemDecoration(old)
            }
            val edge = insets.getInsets(Type.displayCutout() or Type.systemBars())
            val top = if ((requireActivity() as EasterEggsActivity).isWideSize()) edge.top else 0
            val itemDecoration = EdgeItemDecoration(top, edge.bottom)
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.tag = itemDecoration
            return@setOnApplyWindowInsetsListener insets
        }
    }

    /**
     * RecyclerView 安全距离
     */
    private class EdgeItemDecoration(val top: Int, val bottom: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            val itemCount = parent.adapter?.itemCount ?: return
            val position = parent.getChildAdapterPosition(view)
            if (itemCount - 1 == position) {
                outRect.bottom = bottom
            } else if (position == 0) {
                outRect.top = top
            }
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