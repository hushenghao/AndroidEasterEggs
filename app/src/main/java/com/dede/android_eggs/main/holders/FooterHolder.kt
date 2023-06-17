package com.dede.android_eggs.main.holders

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.net.toUri
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ItemEasterEggFooterBinding
import com.dede.android_eggs.main.AndroidTimelineActivity
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Footer
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.util.CustomTabsBrowser

@VHType(viewType = Egg.VIEW_TYPE_FOOTER)
class FooterHolder(view: View) : VHolder<Footer>(view), View.OnClickListener {

    private val binding = ItemEasterEggFooterBinding.bind(view)
    override fun onBindViewHolder(t: Footer) {
        binding.tvVersion.text = binding.root.context.getString(
            R.string.label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        binding.tvGitHash.text = BuildConfig.GIT_HASH
        val views = arrayOf(
            binding.tvGitHash,
            binding.tvGithub, binding.tvLicense, binding.tvFrameworks,
            binding.tvTimeline, binding.tvStar, binding.tvBeta, binding.tvDino3d,
            binding.tvPrivacy, binding.tvFeedback
        )
        for (view in views) {
            view.setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        val context = v.context
        when (v.id) {
            R.id.tv_git_hash -> {
                val commitId = context.getString(R.string.url_github_commit, BuildConfig.GIT_HASH)
                CustomTabsBrowser.launchUrl(context, commitId.toUri())
            }
            R.id.tv_timeline -> {
                val intent = Intent(context, AndroidTimelineActivity::class.java)
                context.startActivity(intent)
            }

            R.id.tv_github, R.id.tv_license, R.id.tv_frameworks,
            R.id.tv_beta, R.id.tv_dino_3d,
            R.id.tv_privacy,
            -> {
                CustomTabsBrowser.launchUrl(context, v.tagString.toUri())
            }

            R.id.tv_star -> {
                launchMarket(context)
            }

            R.id.tv_feedback -> {
                CustomTabsBrowser.launchUrlByBrowser(context, v.tagString.toUri())
            }

            else -> throw UnsupportedOperationException()
        }
    }

    private fun launchMarket(context: Context) {
        val uri = context.getString(R.string.url_market_detail, context.packageName).toUri()
        CustomTabsBrowser.launchUrlByBrowser(context, uri)
    }

    private inline val View.tagString get() = tag as String
}