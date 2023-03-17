package com.dede.android_eggs.main.holders

import android.net.Uri
import android.view.View
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ItemEasterEggFooterBinding
import com.dede.android_eggs.main.entity.Footer
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.util.ChromeTabsBrowser
import com.dede.basic.string


class FooterHolder(view: View) : VHolder<Footer>(view), View.OnClickListener {

    private val binding = ItemEasterEggFooterBinding.bind(view)
    override fun onBindViewHolder(t: Footer) {
        binding.tvGithub.setOnClickListener(this)
        binding.tvFrameworks.setOnClickListener(this)
        binding.tvStar.setOnClickListener(this)
        binding.tvBeta.setOnClickListener(this)
        binding.tvPrivacyAgreement.setOnClickListener(this)
        binding.tvFeedback.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val context = v.context
        when (v.id) {
            R.id.tv_github -> {
                ChromeTabsBrowser.launchUrl(context, Uri.parse(R.string.url_github.string))
            }
            R.id.tv_frameworks -> {
                ChromeTabsBrowser.launchUrl(context, Uri.parse(R.string.url_source.string))
            }
            R.id.tv_star -> {
                ChromeTabsBrowser.launchUrlByBrowser(
                    context, Uri.parse("market://details?id=%s".format(context.packageName))
                )
            }
            R.id.tv_beta -> {
                ChromeTabsBrowser.launchUrl(context, Uri.parse(R.string.url_beta.string))
            }
            R.id.tv_privacy_agreement -> {
                ChromeTabsBrowser.launchUrl(
                    context, Uri.parse(R.string.url_privacy_agreement.string)
                )
            }
            R.id.tv_feedback -> {
                ChromeTabsBrowser.launchUrlByBrowser(
                    context, Uri.parse("mailto:dede.hu@qq.com")
                )
            }
        }
    }
}