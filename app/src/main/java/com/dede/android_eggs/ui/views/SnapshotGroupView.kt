package com.dede.android_eggs.ui.views

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import com.dede.android_eggs.R
import com.dede.android_eggs.main.entity.Snapshot
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.util.ThemeUtils
import com.dede.basic.provider.SnapshotProvider
import com.dede.blurhash_android.BlurHashDrawable
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random

@AndroidEntryPoint
class SnapshotGroupView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs), Runnable {

    private val snapshotList: RecyclerView

    private var scrollPosition: Int = RecyclerView.NO_POSITION

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards Snapshot>

    init {
        LayoutInflater.from(context).inflate(R.layout.view_snapshot_group, this, true)
        snapshotList = findViewById(R.id.snapshot_list)

        snapshotList.layoutManager = CarouselLayoutManager().apply {
            setCarouselStrategy(HeroCarouselStrategy())
        }
        CarouselSnapHelper(true).attachToRecyclerView(snapshotList)
        snapshotList.adapter = VAdapter(
            R.layout.item_snapshot_mask_layout,
            easterEggs, ::onBindSnapshot
        )
    }

    override fun run() {
        if (scrollPosition > RecyclerView.NO_POSITION) {
            snapshotList.scrollToPosition(scrollPosition)
        }
    }

    override fun onDetachedFromWindow() {
        val recyclerView = snapshotList
        val adapter = recyclerView.adapter
        if (adapter != null && recyclerView.computeHorizontalScrollRange() != 0) {
            scrollPosition = (adapter.itemCount - 1) *
                    abs(recyclerView.computeHorizontalScrollOffset()) /
                    recyclerView.computeHorizontalScrollRange()// + 1
        }
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(this)// post scroll to position
    }

    private fun onBindSnapshot(holder: VHolder<*>, snapshot: Snapshot) {
        holder.setIsRecyclable(false)
        val group: ViewGroup = holder.findViewById(R.id.fl_content)
        val background: ImageView = holder.findViewById(R.id.iv_background)
        val provider: SnapshotProvider = snapshot.provider
        background.isVisible = !provider.includeBackground
        background.dispose()
        if (!provider.includeBackground && background.drawable == null) {
            val placeholder = BlurHashDrawable(context, R.string.hash_snapshot_bg, 54, 32)
            background.load(randomBgUri()) {
                placeholder(placeholder)
                error(placeholder)
            }
            if (ThemeUtils.isSystemNightMode(context)) {
                val matrix = ColorMatrix()
                matrix.setScale(0.8f, 0.8f, 0.8f, 0.8f)
                background.colorFilter = ColorMatrixColorFilter(matrix)
            } else {
                background.colorFilter = null
            }
        }
        group.removeAllViewsInLayout()
        group.addView(provider.create(group.context), MATCH_PARENT, MATCH_PARENT)
    }

    private fun randomBgUri(): Uri? {
        val list = context.assets.list("gallery")
        if (list.isNullOrEmpty()) return null
        val index = Random.nextInt(list.size)
        return Uri.parse("file:///android_asset/gallery/%s".format(list[index]))
    }
}