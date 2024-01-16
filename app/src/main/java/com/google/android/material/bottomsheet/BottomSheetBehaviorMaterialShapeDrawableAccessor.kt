package com.google.android.material.bottomsheet

import android.view.View
import com.google.android.material.shape.MaterialShapeDrawable

object BottomSheetBehaviorMaterialShapeDrawableAccessor {

    fun getMaterialShapeDrawable(behavior: BottomSheetBehavior<out View>): MaterialShapeDrawable? {
        return behavior.materialShapeDrawable
    }
}