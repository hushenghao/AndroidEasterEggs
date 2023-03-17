package com.dede.android_eggs.main

import android.os.Bundle
import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
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


class EggListFragment : Fragment(R.layout.fragment_easter_egg_list) {

    private lateinit var binding: FragmentEasterEggListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEasterEggListBinding.bind(view)
        binding.recyclerView.adapter = VAdapter(EggDateSource.eggList) {
            addViewType<EggHolder>(R.layout.item_easter_egg_layout, Egg.VIEW_TYPE_EGG)
            addViewType<PreviewHolder>(R.layout.item_easter_egg_layout, Egg.VIEW_TYPE_PREVIEW)
            addViewType<WavyHolder>(R.layout.item_easter_egg_wavy, Egg.VIEW_TYPE_WAVY)
            addViewType<FooterHolder>(R.layout.item_easter_egg_footer, Egg.VIEW_TYPE_FOOTER)
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.recyclerView,
            OnApplyWindowInsetsListener { v, insets ->
                val edge = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(bottom = edge.bottom)
                return@OnApplyWindowInsetsListener insets
            })
    }

    fun smoothScrollToPosition(providerIndex: Int) {
        val fistOffset = EggDateSource.eggList.indexOfFirst { it is Egg && it.shortcutKey != null }
        val position = fistOffset + providerIndex + 1
        binding.recyclerView.smoothScrollToPosition(position)
    }

}