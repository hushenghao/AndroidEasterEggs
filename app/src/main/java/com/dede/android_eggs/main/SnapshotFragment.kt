package com.dede.android_eggs.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentSnapshotBinding
import com.dede.basic.PlatLogoSnapshotProvider
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.MaskableFrameLayout


class SnapshotFragment : Fragment(R.layout.fragment_snapshot) {

    private lateinit var binding: FragmentSnapshotBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSnapshotBinding.bind(view)
//        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerView.layoutManager = CarouselLayoutManager()
        binding.recyclerView.adapter = SnapshotAdapter()
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private class SnapshotAdapter : RecyclerView.Adapter<SnapshotHolder>() {

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
            val p = viewType// % snapshotList.size
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_snapshot_mask_layout, parent, false)
            val provider = snapshotList[p]
            return SnapshotHolder(itemView, provider).apply { bind() }
        }

        override fun getItemCount(): Int {
            return Int.MAX_VALUE
        }

        override fun onBindViewHolder(holder: SnapshotHolder, position: Int) {
        }

        override fun getItemViewType(position: Int): Int {
            // disable recycler
            return position % snapshotList.size
        }
    }

    private class SnapshotHolder(view: View, val provider: PlatLogoSnapshotProvider) :
        RecyclerView.ViewHolder(view) {
        val maskable: MaskableFrameLayout = itemView.findViewById(R.id.carousel_item_container)

        fun bind() {
            maskable.removeAllViewsInLayout()
            maskable.addView(provider.get(itemView.context))
            maskable.setOnClickListener {
                val intent = provider.getPlatLogoIntent(it.context)
                it.context.startActivity(intent)
            }
        }
    }
}