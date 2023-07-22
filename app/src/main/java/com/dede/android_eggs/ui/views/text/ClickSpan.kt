package com.dede.android_eggs.ui.views.text

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class ClickSpan(
    private val listener: View.OnClickListener,
    private val underline: Boolean = false
) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = underline
    }

    override fun onClick(v: View) {
        listener.onClick(v)
    }
}