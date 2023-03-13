package com.dede.android_eggs.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentEasterEggListBinding
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.basic.requireDrawable


class EasterEggListFragment : Fragment(R.layout.fragment_easter_egg_list) {

    private lateinit var binding: FragmentEasterEggListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEasterEggListBinding.bind(view)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = EasterEggAdapter()
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView,
            OnApplyWindowInsetsListener { v, insets ->
                val edge = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                            WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(bottom = edge.bottom)
                return@OnApplyWindowInsetsListener insets
            }
        )
    }

    private class EasterEggAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private val easterEggList = listOf(
                EasterEgg(
                    R.drawable.ic_android_udc,
                    R.string.title_android_u,
                    R.string.title_android_u,
                    R.string.version_comment_android_u,
                    R.string.target_class_android_t,
                    true
                ),
                EasterEgg(
                    R.drawable.ic_android_tiramisu,
                    R.string.title_android_t,
                    com.android_t.egg.R.string.t_egg_name,
                    R.string.version_comment_android_t,
                    R.string.target_class_android_t,
                    true
                ),
                EasterEgg(
                    R.drawable.ic_android_s,
                    R.string.title_android_s,
                    com.android_s.egg.R.string.s_egg_name,
                    R.string.version_comment_android_s,
                    R.string.target_class_android_s,
                    true
                ),
                EasterEgg(
                    com.android_r.egg.R.drawable.r_icon,
                    R.string.title_android_s,
                    com.android_r.egg.R.string.r_egg_name,
                    R.string.version_comment_android_r,
                    R.string.target_class_android_r,
                    true
                ),
                EasterEgg(
                    com.android_q.egg.R.drawable.q_icon,
                    R.string.title_android_q,
                    com.android_q.egg.R.string.q_egg_name,
                    R.string.version_comment_android_q,
                    R.string.target_class_android_q,
                    true
                ),
                EasterEgg(
                    com.android_p.egg.R.drawable.p_icon,
                    R.string.title_android_p,
                    com.android_p.egg.R.string.p_app_name,
                    R.string.version_comment_android_p,
                    R.string.target_class_android_p,
                    true
                ),
                EasterEgg(
                    R.drawable.ic_android_oreo,
                    R.string.title_android_o_1,
                    com.android_o.egg.R.string.o_app_name,
                    R.string.version_comment_android_o,
                    R.string.target_class_android_o,
                    true
                ),
                EasterEgg(
                    R.drawable.ic_android_oreo,
                    R.string.title_android_o,
                    com.android_o.egg.R.string.o_app_name,
                    R.string.version_comment_android_o,
                    R.string.target_class_android_o,
                    true
                ),
                EasterEgg(
                    R.drawable.ic_android_nougat,
                    R.string.title_android_n,
                    com.android_n.egg.R.string.n_app_name,
                    R.string.version_comment_android_n,
                    R.string.target_class_android_n,
                    true
                ),
                EasterEgg(
                    R.drawable.ic_android_marshmallow,
                    R.string.title_android_m,
                    com.android_m.egg.R.string.m_mland,
                    R.string.version_comment_android_m,
                    R.string.target_class_android_m,
                    true
                ),
                EasterEgg(
                    R.drawable.ic_android_lollipop,
                    R.string.title_android_l,
                    com.android_l.egg.R.string.l_lland,
                    R.string.version_comment_android_l,
                    R.string.target_class_android_l,
                    true
                ),
                EasterEgg(
                    R.drawable.ic_android_kitkat,
                    R.string.title_android_k,
                    com.android_k.egg.R.string.k_dessert_case,
                    R.string.version_comment_android_k,
                    R.string.target_class_android_k,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_jelly_bean,
                    R.string.title_android_j,
                    com.android_j.egg.R.string.j_egg_name,
                    R.string.version_comment_android_j,
                    R.string.target_class_android_j,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_ics,
                    R.string.title_android_i,
                    com.android_i.egg.R.string.i_egg_name,
                    R.string.version_comment_android_i,
                    R.string.target_class_android_i,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_honeycomb,
                    R.string.title_android_h,
                    com.android_h.egg.R.string.h_egg_name,
                    R.string.version_comment_android_h,
                    R.string.target_class_android_h,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_gingerbread,
                    R.string.title_android_g,
                    com.android_g.egg.R.string.g_egg_name,
                    R.string.version_comment_android_g,
                    R.string.target_class_android_g,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_froyo,
                    R.string.title_android_froyo,
                    R.string.summary_android_froyo,
                    R.string.version_comment_android_froyo,
                    -1,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_eclair,
                    R.string.title_android_eclair,
                    R.string.summary_android_eclair,
                    R.string.version_comment_android_eclair,
                    -1,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_donut,
                    R.string.title_android_donut,
                    R.string.summary_android_donut,
                    R.string.version_comment_android_donut,
                    -1,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_cupcake,
                    R.string.title_android_cupcake,
                    R.string.summary_android_cupcake,
                    R.string.version_comment_android_cupcake,
                    -1,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_classic,
                    R.string.title_android_petit_four,
                    R.string.summary_android_petit_four,
                    R.string.version_comment_android_petit_four,
                    -1,
                    false
                ),
                EasterEgg(
                    R.drawable.ic_android_classic,
                    R.string.title_android_base,
                    -1,
                    R.string.version_comment_android_base,
                    -1,
                    false
                )
            )
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                layoutInflater.inflate(R.layout.item_easter_egg_layout, parent, false)
            return EasterEggHolder(view)
        }

        override fun getItemCount(): Int {
            return easterEggList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as EasterEggHolder).bindView(easterEggList[position])
        }
    }

    private data class EasterEgg(
        @DrawableRes val iconRes: Int,
        @StringRes val titleRes: Int,
        @StringRes val summaryRes: Int,
        @StringRes val versionCommentRes: Int,
        @StringRes val targetClassRes: Int,
        val supportAdaptiveIcon: Boolean = true,
    )

    private class EasterEggHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ItemEasterEggLayoutBinding = ItemEasterEggLayoutBinding.bind(view)

        fun bindView(egg: EasterEgg) {
            if (egg.summaryRes != -1) {
                binding.tvTitle.setText(egg.summaryRes)
            } else {
                binding.tvTitle.text = null
            }
            binding.tvSummary.setText(egg.titleRes)
            binding.ivIcon.setImageDrawable(binding.ivIcon.context.requireDrawable(egg.iconRes))
            itemView.setOnClickListener {
                if (egg.targetClassRes == -1) return@setOnClickListener
                val intent = Intent(Intent.ACTION_VIEW)
                    .setClass(it.context, Class.forName(it.context.getString(egg.targetClassRes)))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS)
                it.context.startActivity(intent)
            }
        }
    }


}