package com.dede.basic

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.TransformationMethod
import androidx.appcompat.text.AllCapsTransformationMethod


object TransformationMethodUtils {

    @JvmStatic
    @SuppressLint("RestrictedApi")
    fun createAllCapsTransformationMethod(context: Context): TransformationMethod {
        return AllCapsTransformationMethod(context)
    }
}