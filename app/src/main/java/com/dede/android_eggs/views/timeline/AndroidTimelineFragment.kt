package com.dede.android_eggs.views.timeline

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentAndroidTimelineBinding
import com.dede.android_eggs.main.entity.TimelineEvent
import com.dede.android_eggs.main.entity.TimelineEvent.Companion.isLast
import com.dede.android_eggs.main.entity.TimelineEvent.Companion.isNewGroup
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.views.applyVerticalWindowInsetsPadding
import com.dede.android_eggs.util.EdgeUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.android.material.R as M3R

@AndroidEntryPoint
class AndroidTimelineFragment : BottomSheetDialogFragment(R.layout.fragment_android_timeline) {

    companion object {

        fun show(fm: FragmentManager) {
            val fragment = AndroidTimelineFragment()
            fragment.show(fm, "AndroidTimeline")
        }
    }

    private val binding by viewBinding(FragmentAndroidTimelineBinding::bind)

    @Inject
    lateinit var logoMatcher: AndroidLogoMatcher

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =
            BottomSheetDialog(requireContext(), R.style.ThemeOverlay_BottomSheetDialog_Scrollable)
        EdgeUtils.applyEdge(dialog.window)
        val bottomSheetBehavior = dialog.behavior
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.skipCollapsed = true
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = VAdapter(
            R.layout.item_android_timeline,
            TimelineEvent.timelines
        ) { holder, timelineEvent ->
            with(holder.findViewById<TextView>(R.id.tv_year)) {
                isVisible = timelineEvent.isNewGroup()
                text = timelineEvent.localYear
            }
            with(holder.findViewById<TextView>(R.id.tv_event)) {
                val builder = ShapeAppearanceModel.builder(
                    context,
                    M3R.style.ShapeAppearance_Material3_Corner_Medium,
                    0,
                ).build()
                background = MaterialShapeDrawable(builder).apply {
                    fillColor = ColorStateList.valueOf(
                        MaterialColors.getColor(this@with, M3R.attr.colorSecondaryContainer)
                    )
                }
                text = timelineEvent.eventSpan
            }
            holder.findViewById<ImageView>(R.id.iv_arrow_left).setImageDrawable(
                FontIconsDrawable(
                    holder.itemView.context,
                    Icons.Outlined.arrow_left,
                    M3R.attr.colorSecondaryContainer
                ).apply {
                    isAutoMirrored = true
                }
            )
            holder.findViewById<TextView>(R.id.tv_month).text = timelineEvent.localMonth
            holder.findViewById<ImageView>(R.id.iv_logo)
                .load(logoMatcher.findAndroidLogo(timelineEvent.apiLevel))
            holder.findViewById<View>(R.id.line_bottom).isGone = timelineEvent.isLast()
        }
        binding.recyclerView.applyVerticalWindowInsetsPadding(applyBottom = true)
    }

}