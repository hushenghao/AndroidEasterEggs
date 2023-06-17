package com.dede.android_eggs.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentSnapshotHeaderBinding
import com.dede.android_eggs.main.entity.EggDatas
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.util.findFragmentById
import com.dede.basic.PlatLogoSnapshotProvider
import com.dede.blurhash_android.BlurHashDrawable
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy


class SnapshotFragment : Fragment(R.layout.fragment_snapshot_header) {

    private val binding: FragmentSnapshotHeaderBinding by viewBinding(FragmentSnapshotHeaderBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.snapshotList.layoutManager = CarouselLayoutManager().apply {
            setCarouselStrategy(HeroCarouselStrategy())
        }
        CarouselSnapHelper(true).attachToRecyclerView(binding.snapshotList)
        binding.snapshotList.adapter = VAdapter(
            R.layout.item_snapshot_mask_layout,
            EggDatas.snapshotList, this::onBindSnapshot
        )
    }

    private fun onSnapshotClick(position: Int) {
        val fragment = requireActivity().findFragmentById<EggListFragment>(R.id.fl_eggs) ?: return
        fragment.smoothScrollToPosition(position)
    }

    private fun onBindSnapshot(holder: VHolder<*>, provider: PlatLogoSnapshotProvider) {
        holder.setIsRecyclable(false)
        val group: ViewGroup = holder.findViewById(R.id.fl_content)
        val background: ImageView = holder.findViewById(R.id.iv_background)
        background.isVisible = !provider.includeBackground
        if (!provider.includeBackground && background.drawable == null) {
            background.setImageDrawable(
                BlurHashDrawable(requireContext(), R.string.hash_snapshot_bg, 54, 32)
            )
        }
        group.removeAllViewsInLayout()
        group.addView(
            provider.create(group.context),
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        holder.itemView.setOnClickListener { onSnapshotClick(holder.layoutPosition) }
    }

}