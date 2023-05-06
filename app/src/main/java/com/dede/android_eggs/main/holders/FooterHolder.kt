package com.dede.android_eggs.main.holders

import android.view.View
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ItemEasterEggFooterBinding
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Footer
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.util.AppReviewLauncher
import com.dede.android_eggs.util.CustomTabsBrowser

@VHType(viewType = Egg.VIEW_TYPE_FOOTER)
class FooterHolder(view: View) : VHolder<Footer>(view), View.OnClickListener {

    private val binding = ItemEasterEggFooterBinding.bind(view)
    override fun onBindViewHolder(t: Footer) {
        val views = arrayOf(
            binding.tvGithub, binding.tvLicense, binding.tvFrameworks,
            binding.tvStar, binding.tvBeta,
            binding.tvPrivacy, binding.tvFeedback
        )
        for (view in views) {
            view.setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        val context = v.context
        when (v.id) {
            R.id.tv_github, R.id.tv_license, R.id.tv_frameworks,
            R.id.tv_beta,
            R.id.tv_privacy,
            -> {
                CustomTabsBrowser.launchUrl(context, v.tagString.toUri())
            }
            R.id.tv_star -> {
                AppReviewLauncher.launchMarket(context)
            }
            R.id.tv_feedback -> {
                CustomTabsBrowser.launchUrlByBrowser(context, v.tagString.toUri())
            }
            else -> throw UnsupportedOperationException()
        }
    }

    private inline val View.tagString get() = tag as String
}