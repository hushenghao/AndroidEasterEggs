package com.dede.android_eggs.ui.views

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.ViewEasterEggFooterBinding
import com.dede.android_eggs.ui.views.text.ClickSpan
import com.dede.android_eggs.ui.views.text.SpaceSpan.Companion.appendSpace
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.applyIf
import com.dede.android_eggs.util.createChooser
import com.dede.android_eggs.util.createRepeatWavyDrawable
import com.dede.android_eggs.util.getActivity
import com.dede.android_eggs.util.toast
import com.dede.android_eggs.views.timeline.AndroidTimelineFragment
import com.dede.basic.dp

class EasterEggFooterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val binding = ViewEasterEggFooterBinding.inflate(LayoutInflater.from(context), this)

    private var appended = false

    init {
        setPadding(24.dp, 0, 24.dp, 30.dp)

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
            createRepeatWavyDrawable(context, R.drawable.ic_wavy_line_1)
        )
    }

    private fun handleFlowLayoutChild() {
        if (appended) return
        for (i in 0 until binding.flowLayout.childCount) {
            val textView = binding.flowLayout[i] as TextView
            val unLast = i < binding.flowLayout.childCount - 1
            val span = SpannableStringBuilder()
//            if (textView.isRtl) {
//                span.applyIf(unLast) {
//                    append("/")
//                    appendSpace(8.dp)
//                }.append(textView.text, ClickSpan(this), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//            } else {
            span.append(textView.text, ClickSpan(this), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                .applyIf(unLast) {
                    appendSpace(8.dp)
                    append("/")
                }
//            }
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

            R.id.tv_donate -> {
                CustomTabsBrowser.launchUrl(context, v.tagString.toUri())
                context.toast(R.string.toast_github_sponsor, Toast.LENGTH_LONG)
            }

            R.id.tv_github, R.id.tv_translation, R.id.tv_beta, R.id.tv_dino_3d,
            R.id.tv_privacy, R.id.tv_license,
            -> {
                CustomTabsBrowser.launchUrl(context, v.tagString.toUri())
            }

            R.id.tv_share -> {
                val target = Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, v.tagString)
                    .setType("text/plain")
                val intent = context.createChooser(target)
                context.startActivity(intent)
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