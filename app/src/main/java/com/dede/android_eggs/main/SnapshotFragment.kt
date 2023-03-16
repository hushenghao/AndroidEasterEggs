package com.dede.android_eggs.main

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.load
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentSnapshotHeaderBinding
import com.dede.android_eggs.settings.NightModePref
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.adapter.VType
import com.dede.android_eggs.util.blurHash
import com.dede.android_eggs.util.buildBlurHashUri
import com.dede.basic.PlatLogoSnapshotProvider
import com.google.android.material.appbar.AppBarLayout


class SnapshotFragment : Fragment(R.layout.fragment_snapshot_header) {

    companion object {
        private val snapshotList = listOf(
            com.android_t.egg.PlatLogoSnapshotProvider(),
            com.android_s.egg.PlatLogoSnapshotProvider(),
            com.android_r.egg.PlatLogoSnapshotProvider(),
            com.android_q.egg.PlatLogoSnapshotProvider(),
            com.android_p.egg.PlatLogoSnapshotProvider(),
            com.android_o.egg.PlatLogoSnapshotProvider(true),
            com.android_o.egg.PlatLogoSnapshotProvider(false),
            com.android_n.egg.PlatLogoSnapshotProvider(),
            com.android_m.egg.PlatLogoSnapshotProvider(),
            com.android_l.egg.PlatLogoSnapshotProvider(),
            com.android_k.egg.PlatLogoSnapshotProvider(),
            com.android_j.egg.PlatLogoSnapshotProvider(),
            com.android_i.egg.PlatLogoSnapshotProvider(),
            com.android_h.egg.PlatLogoSnapshotProvider(),
            com.android_g.egg.PlatLogoSnapshotProvider(),
        )
    }

    private lateinit var binding: FragmentSnapshotHeaderBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSnapshotHeaderBinding.bind(view)
        binding.snapshotList.adapter = VAdapter(
            R.layout.item_snapshot_mask_layout,
            snapshotList, this::onBindSnapshot
        )
    }

    private fun onSnapshotClick(position: Int) {
        val easterEggListFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.fl_eggs) as? EasterEggListFragment ?: return
        requireActivity().findViewById<AppBarLayout>(R.id.app_bar)
            ?.setExpanded(false, true)
        easterEggListFragment.smoothScrollToPosition(position)
    }

    private val imageLoader by lazy {
        ImageLoader.Builder(requireContext())
            .blurHash()
            .build()
    }

    private fun onBindSnapshot(holder: VHolder<VType>, provider: PlatLogoSnapshotProvider) {
        val group: ViewGroup = holder.findViewById(R.id.fl_content)
        val background: ImageView = holder.findViewById(R.id.iv_background)
        val hash = if (!NightModePref.isSystemNightMode(requireContext()))
            "blur-hash://LVPO*{9docS\$}Nn4R.oy\$]\${n\$bI/?w=200&h=150"
        else
            "blur-hash://LOFqFcNxsQS6|,oIj@ax=cxFjufk/?w=200&h=150"
        background.load(hash, imageLoader) {
            size(200, 150)
        }
        group.removeAllViewsInLayout()
        group.addView(
            provider.create(group.context),
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        holder.itemView.setOnClickListener { onSnapshotClick(holder.layoutPosition) }
    }

}