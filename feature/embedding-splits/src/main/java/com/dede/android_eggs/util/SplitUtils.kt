package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.window.embedding.ActivityEmbeddingController
import androidx.window.embedding.SplitController
import com.dede.basic.getActivity

/**
 * SplitController API.
 *
 * @author shhu
 * @since 2023/5/22
 */
object SplitUtils {

    @SuppressLint("RestrictedApi")
    fun isActivityEmbedded(context: Context): Boolean {
        val activity = context.getActivity() ?: return false
        return ActivityEmbeddingController.getInstance(context).isActivityEmbedded(activity)
    }

    fun isSplitSupported(context: Context): Boolean {
        return SplitController.getInstance(context).splitSupportStatus ==
                SplitController.SplitSupportStatus.SPLIT_AVAILABLE
    }

}