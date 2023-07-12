package com.dede.android_eggs.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dede.android_eggs.R
import com.dede.android_eggs.main.EggListFragment
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

class SnapshotGroupView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val snapshotList: RecyclerView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_snapshot_group, this, true)
        snapshotList = findViewById(R.id.snapshot_list)

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
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
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