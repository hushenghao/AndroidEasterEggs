package com.dede.android_eggs.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.StateSet
import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import coil.dispose
import coil.load
import coil.size.Size
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentEasterEggListBinding
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.EggActionController.Companion.EXTRA_O_POINT
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_G
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_H
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_I
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_J
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_K
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_L
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_M
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_N
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_O
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_O_POINT
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_P
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_Q
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_R
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_S
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_T
import com.dede.android_eggs.main.EggActionController.Companion.applySupportAdaptiveIcon
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.adapter.VType
import com.dede.android_eggs.ui.adapter.addViewType
import com.dede.android_eggs.util.resolveColor
import com.dede.android_eggs.util.resolveColorStateList
import com.dede.basic.requireDrawable
import com.google.android.material.color.MaterialColors
import com.google.android.material.resources.MaterialAttributes
import com.google.android.material.R as M3R


class EasterEggListFragment : Fragment(R.layout.fragment_easter_egg_list) {

    companion object {
        private val eggList = listOf(
            Egg(
                R.drawable.ic_android_udc,
                R.string.title_android_u,
                R.string.title_android_u,
                R.string.version_comment_android_u,
                R.string.target_class_android_t,
                true,
                itemType = Egg.VIEW_TYPE_PREVIEW
            ),
            Wavy(R.drawable.ic_wavy_line),
            Egg(
                R.drawable.ic_android_tiramisu,
                R.string.title_android_t,
                com.android_t.egg.R.string.t_egg_name,
                R.string.version_comment_android_t,
                R.string.target_class_android_t,
                true,
                KEY_EGG_T
            ),
            Egg(
                R.drawable.ic_android_s,
                R.string.title_android_s,
                com.android_s.egg.R.string.s_egg_name,
                R.string.version_comment_android_s,
                R.string.target_class_android_s,
                true,
                KEY_EGG_S
            ),
            Egg(
                com.android_r.egg.R.drawable.r_icon,
                R.string.title_android_r,
                com.android_r.egg.R.string.r_egg_name,
                R.string.version_comment_android_r,
                R.string.target_class_android_r,
                true,
                KEY_EGG_R
            ),
            Egg(
                com.android_q.egg.R.drawable.q_icon,
                R.string.title_android_q,
                com.android_q.egg.R.string.q_egg_name,
                R.string.version_comment_android_q,
                R.string.target_class_android_q,
                true,
                KEY_EGG_Q
            ),
            Egg(
                com.android_p.egg.R.drawable.p_icon,
                R.string.title_android_p,
                com.android_p.egg.R.string.p_app_name,
                R.string.version_comment_android_p,
                R.string.target_class_android_p,
                true,
                KEY_EGG_P
            ),
            Egg(
                R.drawable.ic_android_oreo,
                R.string.title_android_o_1,
                com.android_o.egg.R.string.o_app_name,
                R.string.version_comment_android_o,
                R.string.target_class_android_o,
                true,
                KEY_EGG_O_POINT,
                bundleOf(EXTRA_O_POINT to true)
            ),
            Egg(
                R.drawable.ic_android_oreo,
                R.string.title_android_o,
                com.android_o.egg.R.string.o_app_name,
                R.string.version_comment_android_o,
                R.string.target_class_android_o,
                true,
                KEY_EGG_O
            ),
            Egg(
                R.drawable.ic_android_nougat,
                R.string.title_android_n,
                com.android_n.egg.R.string.n_app_name,
                R.string.version_comment_android_n,
                R.string.target_class_android_n,
                true,
                KEY_EGG_N
            ),
            Egg(
                R.drawable.ic_android_marshmallow,
                R.string.title_android_m,
                com.android_m.egg.R.string.m_mland,
                R.string.version_comment_android_m,
                R.string.target_class_android_m,
                true,
                KEY_EGG_M
            ),
            Egg(
                R.drawable.ic_android_lollipop,
                R.string.title_android_l,
                com.android_l.egg.R.string.l_lland,
                R.string.version_comment_android_l,
                R.string.target_class_android_l,
                true,
                KEY_EGG_L
            ),
            Egg(
                R.drawable.ic_android_kitkat,
                R.string.title_android_k,
                com.android_k.egg.R.string.k_dessert_case,
                R.string.version_comment_android_k,
                R.string.target_class_android_k,
                false,
                KEY_EGG_K
            ),
            Egg(
                R.drawable.ic_android_jelly_bean,
                R.string.title_android_j,
                com.android_j.egg.R.string.j_egg_name,
                R.string.version_comment_android_j,
                R.string.target_class_android_j,
                false,
                KEY_EGG_J
            ),
            Egg(
                R.drawable.ic_android_ics,
                R.string.title_android_i,
                com.android_i.egg.R.string.i_egg_name,
                R.string.version_comment_android_i,
                R.string.target_class_android_i,
                false,
                KEY_EGG_I
            ),
            Egg(
                R.drawable.ic_android_honeycomb,
                R.string.title_android_h,
                com.android_h.egg.R.string.h_egg_name,
                R.string.version_comment_android_h,
                R.string.target_class_android_h,
                false,
                KEY_EGG_H
            ),
            Egg(
                R.drawable.ic_android_gingerbread,
                R.string.title_android_g,
                com.android_g.egg.R.string.g_egg_name,
                R.string.version_comment_android_g,
                R.string.target_class_android_g,
                false,
                KEY_EGG_G
            ),
            Wavy(R.drawable.ic_wavy_line),
            Egg(
                R.drawable.ic_android_froyo,
                R.string.title_android_froyo,
                R.string.summary_android_froyo,
                R.string.version_comment_android_froyo
            ),
            Egg(
                R.drawable.ic_android_eclair,
                R.string.title_android_eclair,
                R.string.summary_android_eclair,
                R.string.version_comment_android_eclair
            ),
            Egg(
                R.drawable.ic_android_donut,
                R.string.title_android_donut,
                R.string.summary_android_donut,
                R.string.version_comment_android_donut
            ),
            Egg(
                R.drawable.ic_android_cupcake,
                R.string.title_android_cupcake,
                R.string.summary_android_cupcake,
                R.string.version_comment_android_cupcake
            ),
            Egg(
                R.drawable.ic_android_classic,
                R.string.title_android_petit_four,
                R.string.summary_android_petit_four,
                R.string.version_comment_android_petit_four
            ),
            Egg(
                R.drawable.ic_android_classic,
                R.string.title_android_base,
                R.string.title_android_base,
                R.string.version_comment_android_base
            ),
            Wavy(R.drawable.ic_wavy_line_1, true),
            Footer()
        )
    }

    private lateinit var binding: FragmentEasterEggListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEasterEggListBinding.bind(view)
        binding.recyclerView.adapter = VAdapter(eggList) {
            addViewType<EggHolder>(R.layout.item_easter_egg_layout, Egg.VIEW_TYPE_EGG)
            addViewType<PreviewHolder>(R.layout.item_easter_egg_layout, Egg.VIEW_TYPE_PREVIEW)
            addViewType<WavyHolder>(R.layout.item_easter_egg_wavy, Egg.VIEW_TYPE_WAVY)
            addViewType<FooterHolder>(R.layout.item_easter_egg_footer, -2)
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.recyclerView,
            OnApplyWindowInsetsListener { v, insets ->
                val edge = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(bottom = edge.bottom)
                return@OnApplyWindowInsetsListener insets
            })
    }

    fun smoothScrollToPosition(providerIndex: Int) {
        val fistOffset = eggList.indexOfFirst { it is Egg && it.shortcutKey != null }
        val position = fistOffset + providerIndex + 1
        binding.recyclerView.smoothScrollToPosition(position)
    }

    private class Footer : VType {
        override val viewType: Int = -2
    }

    private class FooterHolder(view: View) : VHolder<Footer>(view) {
        override fun onBindViewHolder(t: Footer) {

        }
    }

    private class WavyHolder(view: View) : VHolder<Wavy>(view) {
        private val imageView = itemView.findViewById<ImageView>(R.id.iv_icon)

        private fun getRepeatWavyDrawable(context: Context, wavyRes: Int): Drawable {
            val bitmap = context.requireDrawable(wavyRes).toBitmap()
            return BitmapDrawable(context.resources, bitmap).apply {
                setTileModeXY(Shader.TileMode.REPEAT, null)
                setTint(context.resolveColor(M3R.attr.colorSecondaryContainer))
            }
        }

        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override fun onBindViewHolder(wavy: Wavy) {
            imageView.dispose()
            if (!wavy.repeat) {
                imageView.load(wavy.wavyRes) {
                    size(Size.ORIGINAL)
                }
                return
            }
            imageView.background = getRepeatWavyDrawable(imageView.context, wavy.wavyRes)
        }
    }

    private class PreviewHolder(view: View) : EggHolder(view) {

        @Suppress("SameParameterValue")
        private fun createHarmonizeWithPrimaryColorStateList(
            context: Context, @ColorInt color: Int,
        ): ColorStateList {
            val stateSet = intArrayOf(android.R.attr.state_pressed)
            val defaultColor = MaterialColors.harmonizeWithPrimary(context, color)

            var pressedColor = context.resolveColorStateList(
                M3R.attr.materialCardViewFilledStyle, M3R.attr.cardBackgroundColor
            )?.getColorForState(stateSet, defaultColor) ?: defaultColor
            pressedColor = MaterialColors.harmonize(color, pressedColor)

            return ColorStateList(
                arrayOf(stateSet, StateSet.WILD_CARD),
                intArrayOf(pressedColor, defaultColor)
            )
        }

        @SuppressLint("RestrictedApi")
        private fun getLightTextColor(context: Context, @AttrRes textAppearanceAttrRes: Int): Int {
            // always use dark mode color
            val wrapper = ContextThemeWrapper(context, M3R.style.Theme_Material3_DynamicColors_Dark)
            val value = MaterialAttributes.resolve(wrapper, textAppearanceAttrRes)
            var color = Color.WHITE
            if (value != null) {
                wrapper.withStyledAttributes(
                    value.resourceId,
                    intArrayOf(android.R.attr.textColor)
                ) {
                    color = getColor(0, color)
                }
            }
            return color
        }

        override fun onBindViewHolder(egg: Egg) {
            super.onBindViewHolder(egg)
            val colorStateList =
                createHarmonizeWithPrimaryColorStateList(context, 0xFF073042.toInt())
            val titleTextColor = getLightTextColor(context, M3R.attr.textAppearanceHeadlineSmall)
            val summaryTextColor = getLightTextColor(context, M3R.attr.textAppearanceBodyMedium)
            binding.tvTitle.setTextColor(titleTextColor)
            binding.tvSummary.setTextColor(summaryTextColor)
            binding.root.setCardBackgroundColor(colorStateList)
            binding.tvSummary.text = EggActionController.getTimelineMessage(context)
            itemView.setOnClickListener {
                EggActionController.showTimelineDialog(
                    context, R.drawable.ic_android_udc, R.string.title_android_u
                )
            }
        }
    }

    private open class EggHolder(view: View) : VHolder<Egg>(view) {
        val binding: ItemEasterEggLayoutBinding = ItemEasterEggLayoutBinding.bind(view)
        val context: Context = itemView.context
        private val eggActionController = EggActionController(context)

        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override fun onBindViewHolder(egg: Egg) {
            binding.tvTitle.setText(egg.eggNameRes)
            binding.tvSummary.setText(egg.androidRes)
            binding.ivIcon.load(egg.iconRes) {
                applySupportAdaptiveIcon(context, egg.supportAdaptiveIcon)
            }
            itemView.setOnClickListener { eggActionController.openEgg(egg) }
            binding.ivIcon.setOnClickListener { eggActionController.showVersionCommentDialog(egg) }
        }
    }

    private class Wavy(val wavyRes: Int, val repeat: Boolean = false) : VType {
        override val viewType: Int = Egg.VIEW_TYPE_WAVY
    }

    data class Egg(
        @DrawableRes val iconRes: Int,
        @StringRes val androidRes: Int,
        @StringRes val eggNameRes: Int,
        @StringRes val versionCommentRes: Int,
        @StringRes val targetClassRes: Int = -1,
        val supportAdaptiveIcon: Boolean = false,
        val shortcutKey: String? = null,
        val extras: Bundle? = null,
        private val itemType: Int = VIEW_TYPE_EGG,
    ) : VType {

        companion object {
            const val VIEW_TYPE_EGG = 0
            const val VIEW_TYPE_WAVY = -1
            const val VIEW_TYPE_PREVIEW = 1
        }

        override val viewType: Int = itemType
    }

}