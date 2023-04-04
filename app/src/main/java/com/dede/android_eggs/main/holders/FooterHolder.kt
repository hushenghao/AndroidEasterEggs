package com.dede.android_eggs.main.holders

import android.net.Uri
import android.view.View
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ItemEasterEggFooterBinding
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Footer
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.basic.string

@VHType(viewType = Egg.VIEW_TYPE_FOOTER)
class FooterHolder(view: View) : VHolder<Footer>(view), View.OnClickListener {

    private val binding = ItemEasterEggFooterBinding.bind(view)
    override fun onBindViewHolder(t: Footer) {
        binding.tvGithub.setOnClickListener(this)
        binding.tvFrameworks.setOnClickListener(this)
        binding.tvStar.setOnClickListener(this)
        binding.tvBeta.setOnClickListener(this)
        binding.tvPrivacy.setOnClickListener(this)
        binding.tvLicense.setOnClickListener(this)
        binding.tvFeedback.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val context = v.context
        when (v.id) {
            R.id.tv_github -> {
                CustomTabsBrowser.launchUrl(context, Uri.parse(R.string.url_github.string))
            }
            R.id.tv_frameworks -> {
                CustomTabsBrowser.launchUrl(context, Uri.parse(R.string.url_source.string))
            }
            R.id.tv_star -> {
                CustomTabsBrowser.launchUrlByBrowser(
                    context,
                    Uri.parse(context.getString(R.string.url_market_detail, context.packageName))
                )
            }
            R.id.tv_beta -> {
                CustomTabsBrowser.launchUrl(context, Uri.parse(R.string.url_beta.string))
            }
            R.id.tv_privacy -> {
                CustomTabsBrowser.launchUrl(context, Uri.parse(R.string.url_privacy.string))
            }
            R.id.tv_license -> {
                CustomTabsBrowser.launchUrl(context, Uri.parse(R.string.url_license.string))
            }
            R.id.tv_feedback -> {
                CustomTabsBrowser.launchUrlByBrowser(context, Uri.parse(R.string.url_mail.string))
            }
        }
    }
}