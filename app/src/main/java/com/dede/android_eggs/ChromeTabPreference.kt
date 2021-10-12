package com.dede.android_eggs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import androidx.preference.Preference
import com.google.androidbrowserhelper.trusted.TwaLauncher

/**
 * Chrome Custom Tabs Preference
 *
 * @author hsh
 * @since 2020/10/29 10:02 AM
 */
open class ChromeTabPreference : Preference, Preference.OnPreferenceClickListener {

    private var uri: Uri? = null
    private val useTwa: Boolean

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val arrays = context.obtainStyledAttributes(attrs, R.styleable.ChromeTabPreference)
        val uriString = arrays.getString(R.styleable.ChromeTabPreference_customUrl)
        if (!TextUtils.isEmpty(uriString)) {
            uri = Uri.parse(uriString)
        }
        useTwa = arrays.getBoolean(R.styleable.ChromeTabPreference_useTwa, true)
        arrays.recycle()

        if (uri != null) {
            onPreferenceClickListener = this
        }
    }

    fun setCustomUri(uri: Uri) {
        this.uri = uri
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val uri = this.uri
        if (uri != null) {
            if (useTwa) {
                openTwaWeb(uri)
            } else {
                openBrowser(uri)
            }
        }
        return uri != null
    }

    private fun openBrowser(uri: Uri) {
        val target = Intent(Intent.ACTION_VIEW, uri)
        val intent =
            Intent.createChooser(target, context.getString(R.string.title_open_with))
        context.startActivity(intent)
    }

    private fun openTwaWeb(uri: Uri) {
        val twaLauncher = TwaLauncher(context)
        val builder = TrustedWebActivityIntentBuilder(uri)
        twaLauncher.launch(builder, null, null, null)
    }

}