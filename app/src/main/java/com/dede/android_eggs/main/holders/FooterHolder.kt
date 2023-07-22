package com.dede.android_eggs.main.holders

import android.content.Context
import android.graphics.Paint
import android.text.Spannable.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ItemEasterEggFooterBinding
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Footer
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.views.text.ClickSpan
import com.dede.android_eggs.ui.views.text.SpaceSpan.Companion.appendSpace
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.applyIf
import com.dede.android_eggs.util.createRepeatWavyDrawable
import com.dede.android_eggs.util.getActivity
import com.dede.android_eggs.util.isRtl
import com.dede.android_eggs.views.timeline.AndroidTimelineFragment
import com.dede.basic.dp

@VHType(viewType = Egg.VIEW_TYPE_FOOTER)
class FooterHolder(view: View) : VHolder<Footer>(view), View.OnClickListener {

    private var appended = false

    private val binding = ItemEasterEggFooterBinding.bind(view)
    override fun onBindViewHolder(t: Footer) {
        binding.tvVersion.text = binding.root.context.getString(
            R.string.label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        binding.tvGitHash.text = BuildConfig.GIT_HASH
        binding.tvGitHash.paintFlags = binding.tvGitHash.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        val views = arrayOf(
            binding.tvGitHash,
            binding.tvPrivacy, binding.tvLicense, binding.tvFeedback,
        )
        for (view in views) {
            view.setOnClickListener(this)
        }
        handleFlowLayoutChild()

        binding.ivLine.setImageDrawable(
            createRepeatWavyDrawable(
                itemView.context,
                R.drawable.ic_wavy_line_1
            )
        )
    }

    private fun handleFlowLayoutChild() {
        if (appended) return
        for (i in 0 until binding.flowLayout.childCount) {
            val textView = binding.flowLayout[i] as TextView
            val unLast = i < binding.flowLayout.childCount - 1
            val span = SpannableStringBuilder()
            if (isRtl) {
                span.applyIf(unLast) {
                    append("/")
                    appendSpace(8.dp)
                }.append(textView.text, ClickSpan(this), SPAN_INCLUSIVE_EXCLUSIVE)
            } else {
                span.append(textView.text, ClickSpan(this), SPAN_INCLUSIVE_EXCLUSIVE)
                    .applyIf(unLast) {
                        appendSpace(8.dp)
                        append("/")
                    }
            }
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.text = span
        }
        appended = true
    }

    override fun onClick(v: View) {
        val context = v.context
        when (v.id) {
            R.id.tv_git_hash -> {
                val commitId = context.getString(R.string.url_github_commit, BuildConfig.GIT_HASH)
                CustomTabsBrowser.launchUrl(context, commitId.toUri())
            }

            R.id.tv_timeline -> {
                val activity = context.getActivity<FragmentActivity>() ?: return
                AndroidTimelineFragment.show(activity.supportFragmentManager)
            }

            R.id.tv_github, R.id.tv_frameworks, R.id.tv_translation,
            R.id.tv_beta, R.id.tv_dino_3d,
            R.id.tv_privacy, R.id.tv_license,
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