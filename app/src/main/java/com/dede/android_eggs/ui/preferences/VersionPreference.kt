package com.dede.android_eggs.ui.preferences

import android.content.Context
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.URLSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.util.*
import com.dede.basic.dp
import com.google.android.material.R as M3R


class VersionPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {

    init {
        isPersistent = false
        isCopyingEnabled = true
        title = context.getString(R.string.title_version)
        icon = FontIconsDrawable(context, Icons.Rounded.info, 36f).apply {
            setPadding(12.dp, 6.dp, 0, 0)
        }
        val versionLabel = context.getString(
            R.string.label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        summary = SpannableStringBuilder()
            .append(versionLabel, customTabURLSpan(context.getString(R.string.url_beta)))
            .append(" ")
            .append(" ", centerImageSpan(context, R.drawable.ic_git_tree))
            .append(
                BuildConfig.GIT_HASH,
                customTabURLSpan(
                    context.getString(R.string.url_github_commit, BuildConfig.GIT_HASH)
                ),
                foregroundColorSpan(context, M3R.attr.colorAccent),
                AbsoluteSizeSpan(11, true)
            )
    }

    override fun onClick() {
        CustomTabsBrowser.launchUrl(context, Uri.parse(context.getString(R.string.url_beta)))
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        (holder.findViewById(android.R.id.summary) as? TextView)?.enableClickSpan()
    }

    private class CommitURLSpan(context: Context, hash: String) :
        URLSpan(context.getString(R.string.url_github_commit, hash)) {
        override fun updateDrawState(ds: TextPaint) {
        }

        override fun onClick(widget: View) {
            CustomTabsBrowser.launchUrl(widget.context, Uri.parse(url))
        }
    }

}