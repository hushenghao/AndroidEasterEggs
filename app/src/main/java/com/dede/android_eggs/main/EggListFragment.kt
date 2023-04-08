package com.dede.android_eggs.main

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.PathInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.dede.android_eggs.settings.IconVisualEffectsPref
import com.dede.android_eggs.settings.IconShapePerf
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.addViewType
import com.dede.android_eggs.ui.views.onApplyWindowEdge
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.isLayoutRtl
import com.dede.basic.dp
import kotlin.math.abs
import kotlin.math.sign


class EggListFragment : Fragment(R.layout.fragment_easter_egg_list) {

    private val binding: FragmentEasterEggListBinding by viewBinding(FragmentEasterEggListBinding::bind)

    private var isRecyclerViewIdle = true
    private var orientationAngleSensor: OrientationAngleSensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleOrientationAngleSensor(IconVisualEffectsPref.isEnable(requireContext()))
    }

    private fun handleOrientationAngleSensor(enable: Boolean) {
        if (enable && orientationAngleSensor == null) {
            orientationAngleSensor = OrientationAngleSensor(
                requireContext(), this, ::onOrientationAnglesUpdate
            )
        } else if (!enable) {
            orientationAngleSensor?.destroy()
            orientationAngleSensor = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = VAdapter(EggDatas.eggList) {
            addViewType<EggHolder>(R.layout.item_easter_egg_layout)
            addViewType<PreviewHolder>(R.layout.item_easter_egg_layout)
            addViewType<WavyHolder>(R.layout.item_easter_egg_wavy)
            addViewType<FooterHolder>(R.layout.item_easter_egg_footer)
        }

        var last: ItemDecoration = EggListDivider(10.dp, 0)
        binding.recyclerView.addItemDecoration(last)
        binding.recyclerView.onApplyWindowEdge {
            removeItemDecoration(last)
            last = EggListDivider(10.dp, it.bottom)
            addItemDecoration(last)
        }
        binding.recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                isRecyclerViewIdle = newState == RecyclerView.SCROLL_STATE_IDLE
            }
        })
        val itemTouchHelper = ItemTouchHelper(EggListItemTouchHelperCallback {
            val egg = EggDatas.eggList[it] as? Egg ?: return@EggListItemTouchHelperCallback
            EggActionHelp.addShortcut(requireContext(), egg)
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
        LocalEvent.get(this).register(IconShapePerf.ACTION_CHANGED) {
            @Suppress("NotifyDataSetChanged")
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
        LocalEvent.get(this).register(IconVisualEffectsPref.ACTION_CHANGED) {
            val enable = it.getBooleanExtra(IconVisualEffectsPref.EXTRA_VALUE, false)
            handleOrientationAngleSensor(enable)
        }
    }

    private fun onOrientationAnglesUpdate(xAngle: Float, yAngle: Float) {
        if (!isRecyclerViewIdle) return
        val manager = binding.recyclerView.layoutManager as LinearLayoutManager
        val first = manager.findFirstCompletelyVisibleItemPosition()
        val last = manager.findLastCompletelyVisibleItemPosition()
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

    private class EggListItemTouchHelperCallback(
        private val targetDirection: Int = ItemTouchHelper.START,
        private val onItemSwiped: (position: Int) -> Unit,
    ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

        private fun RecyclerView.ViewHolder?.getCardView(): View? {
            return if (this is EggHolder) this.binding.cardView else null
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == targetDirection) {
                onItemSwiped.invoke(viewHolder.bindingAdapterPosition)
            }
            val cardView = viewHolder.getCardView()
            if (cardView != null) {
                getDefaultUIUtil().clearView(cardView)
            }
            viewHolder.bindingAdapter?.notifyItemChanged(viewHolder.bindingAdapterPosition)
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            getDefaultUIUtil().onSelected(viewHolder.getCardView() ?: return)
        }

        private var feedback = false
        private val interpolator = PathInterpolator(.59f, .72f, .82f, .32f)

        private fun calculateX(x: Float, width: Int): Float {
            return interpolator.getInterpolation(abs(x) / width) * width * sign(x)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean,
        ) {
            val width = viewHolder.itemView.width
            getDefaultUIUtil().onDraw(
                c, recyclerView, viewHolder.getCardView() ?: return,
                calculateX(dX, width), dY, actionState, isCurrentlyActive
            )
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive && !feedback) {
                when (targetDirection) {
                    ItemTouchHelper.LEFT -> if (dX > 0) return
                    ItemTouchHelper.RIGHT -> if (dX < 0) return
                    ItemTouchHelper.START -> {
                        if (recyclerView.isLayoutRtl) {
                            if (dX < 0) return
                        } else {
                            if (dX > 0) return
                        }
                    }
                    ItemTouchHelper.END -> {
                        if (recyclerView.isLayoutRtl) {
                            if (dX < 0) return
                        } else {
                            if (dX > 0) return
                        }
                    }
                }
                val threshold = width * getSwipeThreshold(viewHolder)
                feedback = abs(dX) >= threshold
                if (feedback) {
                    viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            val cardView = viewHolder.getCardView() ?: return
            feedback = false
            getDefaultUIUtil().clearView(cardView)
        }
    }

    private class EggListDivider(private val divider: Int, private val bottomInset: Int) :
        ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.top = divider
            }
            outRect.bottom = divider

            val adapter = parent.adapter ?: return
            if (position == adapter.itemCount - 1) {
                outRect.bottom = divider + bottomInset
            }
        }

    }
}