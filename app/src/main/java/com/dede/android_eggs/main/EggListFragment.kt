package com.dede.android_eggs.main

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentEasterEggListBinding
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.EggDatas
import com.dede.android_eggs.main.holders.EggHolder
import com.dede.android_eggs.main.holders.FooterHolder
import com.dede.android_eggs.main.holders.PreviewHolder
import com.dede.android_eggs.main.holders.WavyHolder
import com.dede.android_eggs.settings.IconShapePerf
import com.dede.android_eggs.settings.IconVisualEffectsPref
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.addHeader
import com.dede.android_eggs.ui.adapter.addViewType
import com.dede.android_eggs.ui.views.onApplyWindowEdge
import com.dede.android_eggs.util.EasterUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.basic.dp
import java.util.*


class EggListFragment : Fragment(R.layout.fragment_easter_egg_list) {

    private val binding: FragmentEasterEggListBinding by viewBinding(FragmentEasterEggListBinding::bind)

    private var isRecyclerViewIdle = true
    private var orientationAngleSensor: OrientationAngleSensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOrientationAngleSensor(IconVisualEffectsPref.isEnable(requireContext()))

        if (EasterUtils.isEaster()) {
            Toast.makeText(requireContext(), R.string.toast_easter, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun handleOrientationAngleSensor(enable: Boolean) {
        val orientationAngleSensor = this.orientationAngleSensor
        if (enable && orientationAngleSensor == null) {
            this.orientationAngleSensor = OrientationAngleSensor(
                requireContext(), this, ::onOrientationAnglesUpdate
            )
        } else if (!enable && orientationAngleSensor != null) {
            onOrientationAnglesUpdate(0f, 0f, 0f)
            orientationAngleSensor.destroy()
            this.orientationAngleSensor = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = VAdapter(EggDatas.eggList) {
            addHeader(createSnapshotView())
            addViewType<EggHolder>(R.layout.item_easter_egg_layout)
            addViewType<PreviewHolder>(R.layout.item_easter_egg_layout)
            addViewType<WavyHolder>(R.layout.item_easter_egg_wavy)
            addViewType<FooterHolder>(R.layout.item_easter_egg_footer)
        }

        var last: ItemDecoration = EggListDivider(10.dp, 0, 0)
        binding.recyclerView.addItemDecoration(last)
        binding.recyclerView.onApplyWindowEdge {
            removeItemDecoration(last)
            last = EggListDivider(10.dp, 0, it.bottom)
            addItemDecoration(last)
        }
        binding.recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                isRecyclerViewIdle = newState == RecyclerView.SCROLL_STATE_IDLE
            }
        })
        LocalEvent.get(this).register(IconShapePerf.ACTION_CHANGED) {
            @Suppress("NotifyDataSetChanged")
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
        LocalEvent.get(this).register(IconVisualEffectsPref.ACTION_CHANGED) {
            val enable = it.getBooleanExtra(IconVisualEffectsPref.EXTRA_VALUE, false)
            handleOrientationAngleSensor(enable)
        }
    }

    private fun createSnapshotView(): View {
        return FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            id = R.id.fl_snapshot
            post {
                childFragmentManager.beginTransaction()
                    .replace(R.id.fl_snapshot, SnapshotFragment())
                    .commit()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onOrientationAnglesUpdate(zAngle: Float, xAngle: Float, yAngle: Float) {
        if (!isRecyclerViewIdle) return
        val manager = binding.recyclerView.layoutManager as LinearLayoutManager
        val first = manager.findFirstVisibleItemPosition()
        val last = manager.findLastVisibleItemPosition()
        for (i in first..last) {
            val holder = binding.recyclerView.findViewHolderForLayoutPosition(i) ?: continue
            if (holder is EggHolder) {
                holder.updateOrientationAngles(xAngle, yAngle)
            }
        }
    }

    fun smoothScrollToPosition(providerIndex: Int) {
        val fistOffset = EggDatas.eggList.indexOfFirst { it is Egg && it.shortcutKey != null }
        val position = fistOffset + providerIndex + 1
        binding.recyclerView.smoothScrollToPosition(position)
    }

    class EggListDivider(
        private val divider: Int,
        private val topInset: Int,
        private val bottomInset: Int,
    ) : ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.top = divider + topInset
            }
            outRect.bottom = divider

            val adapter = parent.adapter ?: return
            if (position == adapter.itemCount - 1) {
                outRect.bottom = divider + bottomInset
            }
        }

    }
}