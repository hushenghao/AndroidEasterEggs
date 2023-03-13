package com.dede.android_eggs.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentSnapshotHeaderBinding
import com.dede.basic.PlatLogoSnapshotProvider


class SnapshotFragment : Fragment(R.layout.fragment_snapshot_header) {

    private lateinit var binding: FragmentSnapshotHeaderBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSnapshotHeaderBinding.bind(view)
        binding.snapshotList.adapter = SnapshotAdapter()
    }

    private class SnapshotAdapter : RecyclerView.Adapter<SnapshotHolder>() {
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
            val position = viewType// % snapshotList.size
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_snapshot_mask_layout, parent, false)
            val provider = snapshotList[position]
            return SnapshotHolder(itemView).apply { bind(provider) }
        }

        override fun getItemCount(): Int {
            return snapshotList.size
        }

        override fun onBindViewHolder(holder: SnapshotHolder, position: Int) {
        }

        override fun getItemViewType(position: Int): Int {
            return position % snapshotList.size
        }
    }

    private class SnapshotHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val group: ViewGroup = itemView.findViewById(R.id.fl_content)

        fun bind(provider: PlatLogoSnapshotProvider) {
            group.removeAllViewsInLayout()
            group.addView(provider.create(itemView.context))
//            itemView.setOnClickListener {
//                val intent = provider.getPlatLogoIntent(it.context)
//                it.context.startActivity(intent)
//            }
        }
    }
}