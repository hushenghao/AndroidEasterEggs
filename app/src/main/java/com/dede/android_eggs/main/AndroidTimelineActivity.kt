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
import com.dede.android_eggs.settings.EdgePref
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.views.onApplyWindowEdge
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.updateCompoundDrawablesRelative
import com.dede.basic.dpf
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

    companion object {
        private val timelines = listOf(
            TimelineEvent("2023", "September", R.drawable.ic_android_udc, "Upside Down Cake.🎉"),
            TimelineEvent("2022", "September", R.drawable.ic_android_tiramisu, "Tiramisu."),
            TimelineEvent(
                "2021", "December", R.drawable.ic_android_s,
                "S V2. Once more unto the breach, dear friends, once more."
            ),
            TimelineEvent("2021", "September", R.drawable.ic_android_s, "S."),
            TimelineEvent(
                "2020",
                "September",
                com.android_r.egg.R.drawable.r_icon,
                "R.\nReleased publicly as Android 11 in September 2020."
            ),
            TimelineEvent(
                "2019", "September", com.android_q.egg.R.drawable.q_icon,
                "Q.\nReleased publicly as Android 10 in September 2019."
            ),
            TimelineEvent(
                "2018", "August", com.android_p.egg.R.drawable.p_icon,
                "P.\nReleased publicly as Android 9 in August 2018."
            ),
            TimelineEvent(
                "2017", "December", R.drawable.ic_android_oreo,
                "O MR1.\nReleased publicly as Android 8.1 in December 2017."
            ),
            TimelineEvent(
                "2017", "August", R.drawable.ic_android_oreo,
                "O.\nReleased publicly as Android 8.0 in August 2017."
            ),
            TimelineEvent(
                "2016", "October", R.drawable.ic_android_nougat,
                "N MR1.\nReleased publicly as Android 7.1 in October 2016."
            ),
            TimelineEvent(
                "2016", "August", R.drawable.ic_android_nougat,
                "N.\nReleased publicly as Android 7.0 in August 2016."
            ),
            TimelineEvent(
                "2015", "October", R.drawable.ic_android_marshmallow,
                "M.\nReleased publicly as Android 6.0 in October 2015."
            ),
            TimelineEvent(
                "2015", "March", R.drawable.ic_android_lollipop,
                "L MR1.\nReleased publicly as Android 5.1 in March 2015."
            ),
            TimelineEvent(
                "2014", "November", R.drawable.ic_android_lollipop,
                "L.\nReleased publicly as Android 5.0 in November 2014."
            ),
            TimelineEvent(
                "2014", "June", R.drawable.ic_android_kitkat,
                "K for watches.\nReleased publicly as Android 4.4W in June 2014."
            ),
            TimelineEvent(
                "2013", "October", R.drawable.ic_android_kitkat,
                "K.\nReleased publicly as Android 4.4 in October 2013."
            ),
            TimelineEvent(
                "2013", "July", R.drawable.ic_android_jelly_bean,
                "J MR2.\nReleased publicly as Android 4.3 in July 2013."
            ),
            TimelineEvent(
                "2012", "November", R.drawable.ic_android_jelly_bean,
                "J MR1.\nReleased publicly as Android 4.2 in November 2012."
            ),
            TimelineEvent(
                "2012", "July", R.drawable.ic_android_jelly_bean,
                "J.\nReleased publicly as Android 4.1 in July 2012."
            ),
            TimelineEvent(
                "2011", "December", R.drawable.ic_android_ics,
                "I MR1.\nReleased publicly as Android 4.03 in December 2011."
            ),
            TimelineEvent(
                "2011", "October", R.drawable.ic_android_ics,
                "I.\nReleased publicly as Android 4.0 in October 2011."
            ),
            TimelineEvent(
                "2011", "July", R.drawable.ic_android_honeycomb,
                "H MR2.\nReleased publicly as Android 3.2 in July 2011."
            ),
            TimelineEvent(
                "2011", "May", R.drawable.ic_android_honeycomb,
                "H MR1.\nReleased publicly as Android 3.1 in May 2011."
            ),
            TimelineEvent(
                "2011", "February", R.drawable.ic_android_honeycomb,
                "H.\nReleased publicly as Android 3.0 in February 2011."
            ),
            TimelineEvent(
                "2011", "February", R.drawable.ic_android_gingerbread,
                "G MR1.\nReleased publicly as Android 2.3.3 in February 2011."
            ),
            TimelineEvent(
                "2010", "December", R.drawable.ic_android_gingerbread,
                "G.\nReleased publicly as Android 2.3 in December 2010."
            ),
            TimelineEvent(
                "2010", "May", R.drawable.ic_android_froyo,
                "F.\nReleased publicly as Android 2.2 in May 2010."
            ),
            TimelineEvent(
                "2010", "January", R.drawable.ic_android_eclair,
                "E MR1.\nReleased publicly as Android 2.1 in January 2010."
            ),
            TimelineEvent(
                "2009", "December", R.drawable.ic_android_eclair,
                "E incremental update.\nReleased publicly as Android 2.0.1 in December 2009."
            ),
            TimelineEvent(
                "2009", "October", R.drawable.ic_android_eclair,
                "E.\nReleased publicly as Android 2.0 in October 2009."
            ),
            TimelineEvent(
                "2009", "September", R.drawable.ic_android_donut,
                "D.\nReleased publicly as Android 1.6 in September 2009."
            ),
            TimelineEvent(
                "2009", "April", R.drawable.ic_android_cupcake,
                "C.\nReleased publicly as Android 1.5 in April 2009."
            ),
            TimelineEvent(
                "2009", "February", R.drawable.ic_android_classic,
                "First Android update.\nReleased publicly as Android 1.1 in February 2009."
            ),
            TimelineEvent(
                "2008", "September", R.drawable.ic_android_classic,
                "The original, first, version of Android. Yay!\nReleased publicly as Android 1.0 in September 2008."
            )
        )

        private fun isNewGroup(current: TimelineEvent): Boolean {
            val index = timelines.indexOf(current)
            if (index == -1) return true
            if (index == 0) return true
            val last = timelines[index - 1]
            return last.year != current.year
        }

        private fun isLast(current: TimelineEvent): Boolean {
            return timelines.last() === current
        }
    }

    private val binding by viewBinding(ActivityAndroidTimelineBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        EdgePref.applyEdge(this, window)
        super.onCreate(savedInstanceState)
        LocalEvent.get(this as LifecycleOwner).register(EdgePref.ACTION_CHANGED) {
            recreate()
        }
        binding.recyclerView.adapter =
            VAdapter(R.layout.item_android_timeline, timelines) { holder, timelineEvent ->
                with(holder.findViewById<TextView>(R.id.tv_year)) {
                    isVisible = isNewGroup(timelineEvent)
                    text = timelineEvent.year
                    val draw = FontIconsDrawable(
                        context,
                        Icons.Rounded.arrow_drop_down,
                        M3R.attr.colorOnSecondaryContainer,
                        8.dpf
                    )
                    updateCompoundDrawablesRelative(end = draw)
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
                    text = timelineEvent.event
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
                holder.findViewById<TextView>(R.id.tv_month).text = timelineEvent.month
                holder.findViewById<ImageView>(R.id.iv_logo).load(timelineEvent.logoRes)
                holder.findViewById<View>(R.id.line_bottom).isGone = isLast(timelineEvent)
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