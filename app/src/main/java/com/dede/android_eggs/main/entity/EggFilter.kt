package com.dede.android_eggs.main.entity

import android.content.Context
import android.widget.Filter
import com.dede.android_eggs.ui.adapter.VType
import java.util.Locale

/**
 * Egg Search Filter
 *
 * @author shhu
 * @since 2023/8/9
 */
class EggFilter(val context: Context) : Filter() {

    private val allSearchEggList = EggDatas.eggList.let {
        val newList = ArrayList<VType>()
        for (vType in it) {
            if (vType is EggGroup) {
                newList.addAll(vType.child)
            } else {
                newList.add(vType)
            }
        }
        return@let newList
    }
    private val allEggList = EggDatas.eggList

    val eggList = ArrayList(allEggList)

    var onFilterResults: OnFilterResults? = null

    interface OnFilterResults {
        fun publishResults(constraint: CharSequence, newList: List<VType>)
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        val resultList = ArrayList<VType>()
        if (constraint.isEmpty()) {
            resultList.addAll(allEggList)
        } else {
            for (vType in allSearchEggList) {
                if (vType !is Egg) continue
                val filter = constraint.toString().lowercase(Locale.ROOT)
                if (context.getString(vType.eggNameRes)
                        .lowercase(Locale.ROOT)
                        .contains(filter) ||
                    vType.versionFormatter.format(context).toString()
                        .lowercase(Locale.ROOT)
                        .contains(filter)
                ) {
                    resultList.add(vType)
                }
            }
        }
        return FilterResults().apply { values = resultList }
    }

    override fun publishResults(constraint: CharSequence, filterResults: FilterResults) {
        @Suppress("UNCHECKED_CAST")
        val newList = filterResults.values as Collection<VType>
        this.eggList.clear()
        this.eggList.addAll(newList)
        onFilterResults?.publishResults(constraint, this.eggList)
    }

}