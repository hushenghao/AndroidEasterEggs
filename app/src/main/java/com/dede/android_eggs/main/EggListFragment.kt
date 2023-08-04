package com.dede.android_eggs.main

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Filter
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
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
import com.dede.android_eggs.main.holders.PreviewHolder
import com.dede.android_eggs.main.holders.WavyHolder
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.VType
import com.dede.android_eggs.ui.adapter.addFooter
import com.dede.android_eggs.ui.adapter.addHeader
import com.dede.android_eggs.ui.adapter.addViewType
import com.dede.android_eggs.ui.adapter.removeFooter
import com.dede.android_eggs.ui.adapter.removeHeader
import com.dede.android_eggs.ui.views.EasterEggFooterView
import com.dede.android_eggs.ui.views.SnapshotGroupView
import com.dede.android_eggs.util.EasterUtils
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.EdgeUtils.onApplyWindowEdge
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.views.settings.SettingsPageController
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.android_eggs.views.settings.prefs.IconVisualEffectsPref
import com.dede.basic.dp
import java.util.*


class EggListFragment : Fragment(R.layout.fragment_easter_egg_list),
    SettingsPageController.OnSearchTextChangeListener {

    private val binding: FragmentEasterEggListBinding by viewBinding(FragmentEasterEggListBinding::bind)

    private var isRecyclerViewIdle = true
    private var orientationAngleSensor: OrientationAngleSensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOrientationAngleSensor(IconVisualEffectsPref.isEnable(requireContext()))
        SettingsPageController(requireActivity()).apply {
            onCreate(savedInstanceState)
            onSearchTextChangeListener = this@EggListFragment
        }

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

    override fun onSearchTextChange(newText: String) {
        eggFilter.filter(newText)
    }

    private val eggFilter = object : Filter() {

        private val allEggList = EggDatas.eggList
        val eggList = ArrayList(allEggList)

        override fun performFiltering(constraint: CharSequence): FilterResults {
            val resultList = ArrayList<VType>()
            if (constraint.isEmpty()) {
                resultList.addAll(allEggList)
            } else {
                val context = requireContext()
                for (vType in allEggList) {
                    if (vType !is Egg) continue
                    val filter = constraint.toString().lowercase(Locale.ROOT)
                    if (context.getString(vType.eggNameRes)
                            .lowercase(Locale.ROOT)
                            .contains(filter) ||
                        context.getString(vType.androidRes)
                            .lowercase(Locale.ROOT)
                            .contains(filter)
                    ) {
                        resultList.add(vType)
                    }
                }
            }
            return FilterResults().apply { values = resultList }
        }

        override fun publishResults(constraint: CharSequence, filterResults: FilterResults) {
            @Suppress("UNCHECKED_CAST")
            val newList = filterResults.values as Collection<VType>
            eggList.clear()
            eggList.addAll(newList)
            val vAdapter = binding.recyclerView.adapter as VAdapter
            if (constraint.isEmpty()) {
                vAdapter.addHeader(snapshotView)
                vAdapter.addFooter(footerView)
            } else {
                vAdapter.removeHeader(snapshotView)
                vAdapter.removeFooter(footerView)
            }
            vAdapter.notifyDataSetChanged()
        }
    }

    private val snapshotView by lazy { SnapshotGroupView(requireContext()) }
    private val footerView by lazy { EasterEggFooterView(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = VAdapter(eggFilter.eggList) {
            addHeader(snapshotView)
            addViewType<EggHolder>(R.layout.item_easter_egg_layout)
            addViewType<PreviewHolder>(R.layout.item_easter_egg_layout)
            addViewType<WavyHolder>(R.layout.item_easter_egg_wavy)
            addFooter(footerView)
        }

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
        LocalEvent.get(this).register(IconShapePref.ACTION_CHANGED) {
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
        LocalEvent.get(this).register(IconVisualEffectsPref.ACTION_CHANGED) {
            val enable = it.getBooleanExtra(IconVisualEffectsPref.EXTRA_VALUE, false)
            handleOrientationAngleSensor(enable)
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

    fun smoothScrollToEgg(eggKey: String) {
        val fistOffset = EggDatas.eggList.indexOfFirst { it is Egg && it.key == eggKey }
        val position = fistOffset + 1// header offset
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