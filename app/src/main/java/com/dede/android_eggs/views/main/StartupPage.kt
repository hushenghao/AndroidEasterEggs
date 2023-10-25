package com.dede.android_eggs.views.main

import android.content.Context
import android.content.DialogInterface
import android.widget.ImageView
import androidx.core.content.edit
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.pref
import com.dede.basic.createVectorDrawableCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StartupPage(private val context: Context) : MaterialAlertDialogBuilder(context),
    DialogInterface.OnClickListener {

    companion object {

        private const val KEY = "key_welcome_status"

        fun show(context: Context) {
            val status = context.pref.getBoolean(KEY, false)
            if (status) return
            StartupPage(context)
                .show()
        }
    }

    init {
        setTitle(R.string.label_welcome)
        setMessage(R.string.summary_browse_privacy_policy)
        val view = ImageView(context).apply {
            adjustViewBounds = true
            setImageDrawable(context.createVectorDrawableCompat(R.drawable.img_welcome_poster))
        }
        setView(view)
        setNeutralButton(R.string.label_privacy_policy, this)
        setNegativeButton(android.R.string.cancel, this)
        setPositiveButton(R.string.action_agree, this)
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                context.pref.edit {
                    putBoolean(KEY, true)
                }
            }

            DialogInterface.BUTTON_NEGATIVE -> {
            }

            DialogInterface.BUTTON_NEUTRAL -> {
                CustomTabsBrowser.launchUrl(
                    context,
                    context.getString(R.string.url_privacy).toUri()
                )
            }

            else -> throw UnsupportedOperationException("Dialog which: $which")
        }
    }

}