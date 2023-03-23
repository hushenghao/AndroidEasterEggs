package com.dede.android_eggs.main

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentEasterEggListBinding
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.EggDatas
import com.dede.android_eggs.main.holders.EggHolder
import com.dede.android_eggs.main.holders.FooterHolder
import com.dede.android_eggs.main.holders.PreviewHolder
import com.dede.android_eggs.main.holders.WavyHolder
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.addViewType
import com.dede.android_eggs.ui.views.onApplyWindowEdge
import com.dede.basic.dp


class EggListFragment : Fragment(R.layout.fragment_easter_egg_list) {

    private val binding: FragmentEasterEggListBinding by viewBinding(FragmentEasterEggListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = VAdapter(EggDatas.eggList) {
            addViewType<EggHolder>(R.layout.item_easter_egg_layout)
            addViewType<PreviewHolder>(R.layout.item_easter_egg_layout)
            addViewType<WavyHolder>(R.layout.item_easter_egg_wavy)
            addViewType<FooterHolder>(R.layout.item_easter_egg_footer)
        }

        var last: ItemDecoration? = null
        binding.recyclerView.onApplyWindowEdge {
            if (last != null) {
                removeItemDecoration(last!!)
            }
            last = EggListDivider(10.dp, it.bottom)
            addItemDecoration(last!!)
        }
        val itemTouchHelper = ItemTouchHelper(EggListItemTouchHelperCallback {
            val egg = EggDatas.eggList[it] as? Egg ?: return@EggListItemTouchHelperCallback
            EggActionHelp.addShortcut(requireContext(), egg)
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    fun smoothScrollToPosition(providerIndex: Int) {
        val fistOffset = EggDatas.eggList.indexOfFirst { it is Egg && it.shortcutKey != null }
        val position = fistOffset + providerIndex + 1
        binding.recyclerView.smoothScrollToPosition(position)
    }

    private class EggListItemTouchHelperCallback(val onItemSwiped: (position: Int) -> Unit) :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

        private fun RecyclerView.ViewHolder?.getCardView(): View? {
            return if (this is EggHolder) this.binding.cardView else null
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == ItemTouchHelper.START) {
                onItemSwiped.invoke(viewHolder.bindingAdapterPosition)
            }
            val cardView = viewHolder.getCardView()
            if (cardView != null) {
                getDefaultUIUtil().clearView(cardView)
            }
            viewHolder.bindingAdapter?.notifyItemChanged(viewHolder.bindingAdapterPosition)
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return 0.6f
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            getDefaultUIUtil().onSelected(viewHolder.getCardView() ?: return)
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
            getDefaultUIUtil().onDraw(
                c, recyclerView, viewHolder.getCardView() ?: return,
                dX, dY, actionState, isCurrentlyActive
            )
        }

        override fun onChildDrawOver(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean,
        ) {
            getDefaultUIUtil().onDrawOver(
                c, recyclerView, viewHolder.getCardView() ?: return,
                dX, dY, actionState, isCurrentlyActive
            )
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            getDefaultUIUtil().clearView(viewHolder.getCardView() ?: return)
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