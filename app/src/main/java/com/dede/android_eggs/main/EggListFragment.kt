package com.dede.android_eggs.main

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentEasterEggListBinding
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.EggDateSource
import com.dede.android_eggs.main.holders.EggHolder
import com.dede.android_eggs.main.holders.FooterHolder
import com.dede.android_eggs.main.holders.PreviewHolder
import com.dede.android_eggs.main.holders.WavyHolder
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.addViewType
import com.dede.android_eggs.ui.views.onApplyWindowEdge
import com.dede.basic.dp


class EggListFragment : Fragment(R.layout.fragment_easter_egg_list) {

    private val binding: FragmentEasterEggListBinding by viewBinding(FragmentEasterEggListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = VAdapter(EggDateSource.eggList) {
            addViewType<EggHolder>(R.layout.item_easter_egg_layout)
            addViewType<PreviewHolder>(R.layout.item_easter_egg_layout)
            addViewType<WavyHolder>(R.layout.item_easter_egg_wavy)
            addViewType<FooterHolder>(R.layout.item_easter_egg_footer)
        }

        binding.recyclerView.onApplyWindowEdge {
            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            addItemDecoration(EggListDivider(10.dp, it.bottom))
        }
    }

    fun smoothScrollToPosition(providerIndex: Int) {
        val fistOffset = EggDateSource.eggList.indexOfFirst { it is Egg && it.shortcutKey != null }
        val position = fistOffset + providerIndex + 1
        binding.recyclerView.smoothScrollToPosition(position)
    }

    private class EggListDivider(private val divider: Int, private val bottomInset: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.top = divider
            }
            outRect.bottom = divider

            val adapter = parent.adapter ?: return
            if (position == adapter.itemCount - 1) {
                outRect.bottom = divider + bottomInset
            }
        }

    }
}