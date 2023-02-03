package com.dede.android_eggs.ui

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * View 圆角
 * @author hsh
 * @since 2021/10/21 5:03 下午
 */
class OvalOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        outline.setOval(
            view.paddingLeft, view.paddingTop,
            view.width - view.paddingRight,
            view.height - view.paddingBottom
        )
    }
}

class CornersOutlineProvider(val radius: Float) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(
            view.paddingLeft, view.paddingTop,
            view.width - view.paddingRight,
            view.height - view.paddingBottom, radius
        )
    }
}