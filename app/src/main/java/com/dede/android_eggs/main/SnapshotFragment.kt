package com.dede.android_eggs.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentSnapshotHeaderBinding
import com.dede.android_eggs.main.entity.EggDatas
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.util.findFragmentById
import com.dede.android_eggs.util.getActivity
import com.dede.basic.PlatLogoSnapshotProvider
import com.dede.blurhash_android.BlurHashDrawable
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy


class SnapshotFragment : Fragment(R.layout.fragment_snapshot_header) {

    companion object {
        fun createSnapshotView(context: Context): View {
            val parent = FrameLayout(context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
            val inflater = LayoutInflater.from(context)
            val binding = FragmentSnapshotHeaderBinding.inflate(inflater, parent, false)
            binding.onBind()
            return binding.root
        }

        private fun FragmentSnapshotHeaderBinding.onBind() {
            snapshotList.layoutManager = CarouselLayoutManager().apply {
                setCarouselStrategy(HeroCarouselStrategy())
            }
            CarouselSnapHelper(true).attachToRecyclerView(snapshotList)
            snapshotList.adapter = VAdapter(
                R.layout.item_snapshot_mask_layout,
                EggDatas.snapshotList, ::onBindSnapshot
            )
        }

        private fun onBindSnapshot(holder: VHolder<*>, provider: PlatLogoSnapshotProvider) {
            holder.setIsRecyclable(false)
            val group: ViewGroup = holder.findViewById(R.id.fl_content)
            val background: ImageView = holder.findViewById(R.id.iv_background)
            background.isVisible = !provider.includeBackground
            if (!provider.includeBackground && background.drawable == null) {
                background.setImageDrawable(
                    BlurHashDrawable(holder.itemView.context, R.string.hash_snapshot_bg, 54, 32)
                )
            }
            group.removeAllViewsInLayout()
            group.addView(
                provider.create(group.context),
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
            )
            holder.itemView.setOnClickListener {
                val fragment = it.context.getActivity<FragmentActivity>()
                    ?.findFragmentById<EggListFragment>(R.id.fl_eggs)
                    ?: return@setOnClickListener
                val position = holder.layoutPosition
                fragment.smoothScrollToPosition(position)
            }
        }
    }

    private val binding: FragmentSnapshotHeaderBinding by viewBinding(FragmentSnapshotHeaderBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onBind()
    }

}