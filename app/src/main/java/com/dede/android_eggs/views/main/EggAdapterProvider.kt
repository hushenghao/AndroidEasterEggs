package com.dede.android_eggs.views.main

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.EggFilter
import com.dede.android_eggs.main.entity.EggGroup
import com.dede.android_eggs.main.holders.EggHolder
import com.dede.android_eggs.main.holders.GroupHolder
import com.dede.android_eggs.main.holders.PreviewHolder
import com.dede.android_eggs.main.holders.WavyHolder
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.VType
import com.dede.android_eggs.ui.adapter.addFooter
import com.dede.android_eggs.ui.adapter.addHeader
import com.dede.android_eggs.ui.adapter.addViewType
import com.dede.android_eggs.ui.adapter.removeFooter
import com.dede.android_eggs.ui.adapter.removeHeader
import com.dede.android_eggs.ui.views.EasterEggFooterView
import com.dede.android_eggs.ui.views.SnapshotGroupView
import com.dede.android_eggs.views.settings.SettingsPageController

class EggAdapterProvider(val context: Context) : EggFilter.OnFilterResults,
    SettingsPageController.OnSearchTextChangeListener {

    val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
        get() = vAdapter

    private val vAdapter: VAdapter
    private val eggFilter: EggFilter = EggFilter(context)

    private val snapshotView = SnapshotGroupView(context)
    private val footerView = EasterEggFooterView(context)

    init {
        eggFilter.onFilterResults = this
        vAdapter = VAdapter(eggFilter.eggList) {
            addHeader(snapshotView)
            addViewType<EggHolder>(R.layout.item_easter_egg_layout)
            addViewType<GroupHolder>(R.layout.item_easter_egg_layout)
            addViewType<PreviewHolder>(R.layout.item_easter_egg_layout)
            addViewType<WavyHolder>(R.layout.item_easter_egg_wavy)
            addFooter(footerView)
        }
    }

    fun indexOfByEggKey(eggKey: String): Int {
        val position = eggFilter.eggList.indexOfFirst {
            when (it) {
                is Egg -> it.key == eggKey
                is EggGroup -> it.child.find { c -> c.key == eggKey } != null
                else -> false
            }
        }
        if (position == -1) {
            return -1
        }
        return position + vAdapter.headerFooterExt.headerCount
    }

    override fun publishResults(constraint: CharSequence, newList: List<VType>) {
        if (constraint.isEmpty()) {
            vAdapter.addHeader(snapshotView)
            vAdapter.addFooter(footerView)
        } else {
            vAdapter.removeHeader(snapshotView)
            vAdapter.removeFooter(footerView)
        }
        vAdapter.notifyDataSetChanged()
    }

    override fun onSearchTextChange(newText: String) {
        eggFilter.filter(newText)
    }

}