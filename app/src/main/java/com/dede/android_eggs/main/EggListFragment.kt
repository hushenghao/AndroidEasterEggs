package com.dede.android_eggs.main

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentEasterEggListBinding
import com.dede.android_eggs.util.EasterUtils
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.EdgeUtils.onApplyWindowEdge
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.toast
import com.dede.android_eggs.views.main.EggAdapterProvider
import com.dede.android_eggs.views.settings.ActionBarMenuController
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.android_eggs.views.settings.prefs.IconVisualEffectsPref
import com.dede.basic.dp


class EggListFragment : Fragment(R.layout.fragment_easter_egg_list),
    OrientationAngleSensor.OnOrientationAnglesUpdate {

    private val binding: FragmentEasterEggListBinding by viewBinding(FragmentEasterEggListBinding::bind)

    private var isRecyclerViewIdle = true
    private var orientationAngleSensor: OrientationAngleSensor? = null
    private lateinit var eggAdapterProvider: EggAdapterProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOrientationAngleSensor(IconVisualEffectsPref.isEnable(requireContext()))

        eggAdapterProvider = EggAdapterProvider(requireContext())
        ActionBarMenuController(requireActivity()).apply {
            onCreate(savedInstanceState)
            onSearchTextChangeListener = eggAdapterProvider
        }

        if (EasterUtils.isEaster()) {
            requireContext().toast(R.string.toast_easter)
        }
    }

    private fun handleOrientationAngleSensor(enable: Boolean) {
        val orientationAngleSensor = this.orientationAngleSensor
        if (enable && orientationAngleSensor == null) {
            this.orientationAngleSensor = OrientationAngleSensor(
                requireContext(), this, this
            )
        } else if (!enable && orientationAngleSensor != null) {
            updateOrientationAngles(0f, 0f, 0f)
            orientationAngleSensor.destroy()
            this.orientationAngleSensor = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = eggAdapterProvider.adapter
        var last: ItemDecoration = EggListDivider(10.dp, 0, 0)
        binding.recyclerView.addItemDecoration(last)
        binding.recyclerView.onApplyWindowEdge(
            EdgeUtils.DEFAULT_EDGE_MASK or WindowInsetsCompat.Type.ime()
        ) {
            removeItemDecoration(last)
            last = EggListDivider(10.dp, 0, it.bottom)
            addItemDecoration(last)
        }
        binding.recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                isRecyclerViewIdle = newState == RecyclerView.SCROLL_STATE_IDLE
            }
        })
        LocalEvent.receiver(this).register(IconShapePref.ACTION_CHANGED) {
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
        LocalEvent.receiver(this).register(IconVisualEffectsPref.ACTION_CHANGED) {
            val enable = it.getBooleanExtra(IconVisualEffectsPref.EXTRA_VALUE, false)
            handleOrientationAngleSensor(enable)
        }
    }

    override fun updateOrientationAngles(zAngle: Float, xAngle: Float, yAngle: Float) {
        if (!isRecyclerViewIdle) return
        val manager = binding.recyclerView.layoutManager as LinearLayoutManager
        val first = manager.findFirstVisibleItemPosition()
        val last = manager.findLastVisibleItemPosition()
        for (i in first..last) {
            val holder = binding.recyclerView.findViewHolderForLayoutPosition(i) ?: continue
            if (holder is OrientationAngleSensor.OnOrientationAnglesUpdate) {
                holder.updateOrientationAngles(zAngle, xAngle, yAngle)
            }
        }
    }

    fun smoothScrollToEgg(eggKey: String) {
        val position = eggAdapterProvider.indexOfByEggKey(eggKey)
        if (position != RecyclerView.NO_POSITION) {
            binding.recyclerView.smoothScrollToPosition(position)
        }
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