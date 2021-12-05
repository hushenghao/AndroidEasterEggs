package com.dede.android_eggs

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