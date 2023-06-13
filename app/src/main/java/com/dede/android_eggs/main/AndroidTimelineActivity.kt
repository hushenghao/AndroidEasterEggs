package com.dede.android_eggs.main

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ActivityAndroidTimelineBinding
import com.dede.android_eggs.main.entity.TimelineEvent
import com.dede.android_eggs.main.entity.TimelineEvent.Companion.isLast
import com.dede.android_eggs.main.entity.TimelineEvent.Companion.isNewGroup
import com.dede.android_eggs.settings.EdgePref
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.views.onApplyWindowEdge
import com.dede.android_eggs.util.LocalEvent
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.R as M3R

/**
 * Split Placeholder
 *
 * @author shhu
 * @since 2023/5/22
 */
class AndroidTimelineActivity : AppCompatActivity(R.layout.activity_android_timeline) {

    private val binding by viewBinding(ActivityAndroidTimelineBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        EdgePref.applyEdge(this, window)
        super.onCreate(savedInstanceState)
        LocalEvent.get(this as LifecycleOwner).register(EdgePref.ACTION_CHANGED) {
            recreate()
        }
        binding.recyclerView.adapter = VAdapter(
            R.layout.item_android_timeline,
            TimelineEvent.timelines
        ) { holder, timelineEvent ->
            with(holder.findViewById<TextView>(R.id.tv_year)) {
                isVisible = timelineEvent.isNewGroup()
                text = timelineEvent.year
            }
            with(holder.findViewById<TextView>(R.id.tv_event)) {
                val builder = ShapeAppearanceModel.builder(
                    context,
                    M3R.style.ShapeAppearance_Material3_Corner_Medium,
                    0,
                ).build()
                background = MaterialShapeDrawable(builder).apply {
                    fillColor = ColorStateList.valueOf(
                        MaterialColors.getColor(this@with, M3R.attr.colorPrimaryContainer)
                    )
                }
                text = timelineEvent.eventSpan
            }
            holder.findViewById<ImageView>(R.id.iv_arrow_left).setImageDrawable(
                FontIconsDrawable(
                    holder.itemView.context,
                    Icons.Outlined.arrow_left,
                    M3R.attr.colorPrimaryContainer
                ).apply {
                    isAutoMirrored = true
                }
            )
            holder.findViewById<TextView>(R.id.tv_month).text = timelineEvent.localMonth
            holder.findViewById<ImageView>(R.id.iv_logo).load(timelineEvent.logoRes)
            holder.findViewById<View>(R.id.line_bottom).isGone = timelineEvent.isLast()
        }
        var last: RecyclerView.ItemDecoration = EggListFragment.EggListDivider(0, 0, 0)
        binding.recyclerView.addItemDecoration(last)
        binding.recyclerView.onApplyWindowEdge {
            removeItemDecoration(last)
            last = EggListFragment.EggListDivider(0, it.top, it.bottom)
            addItemDecoration(last)
        }
    }

}