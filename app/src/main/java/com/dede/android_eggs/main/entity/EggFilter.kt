package com.dede.android_eggs.main.entity

import android.content.Context
import android.widget.Filter
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Egg.Companion.toVTypeEgg
import com.dede.android_eggs.ui.adapter.VType
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEggGroup
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.Locale
import javax.inject.Inject

/**
 * Egg Search Filter
 *
 * @author shhu
 * @since 2023/8/9
 */
class EggFilter @Inject constructor(
    @ActivityContext private val context: Context,
    easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>
) : Filter() {

    private fun convertEggList(easterEggs: List<BaseEasterEgg>): List<VType> {
        val list = ArrayList<VType>()
        list.add(Wavy(R.drawable.ic_wavy_line))
        for (easterEgg in easterEggs) {
            list.add(easterEgg.toVTypeEgg())
        }
        list.add(Wavy(R.drawable.ic_wavy_line))
        return list
    }

    private val allEggList: List<VType> = convertEggList(easterEggs)

    private val allSearchEggList: List<VType> = allEggList.let {
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

    val eggList: MutableList<VType> = ArrayList(allEggList)

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