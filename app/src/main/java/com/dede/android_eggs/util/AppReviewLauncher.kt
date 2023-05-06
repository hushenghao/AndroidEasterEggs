package com.dede.android_eggs.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.basic.androidLifecycle
import com.dede.basic.launch
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.material.internal.ContextUtils
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Play store in app review
 *
 * @author shhu
 * @since 2023/5/5
 */
object AppReviewLauncher {

    fun launchMarket(context: Context) {
        val uri = context.getString(R.string.url_market_detail, context.packageName).toUri()
        CustomTabsBrowser.launchUrlByBrowser(context, uri)
    }

    fun launchReview(context: Context) {
        if (!context.isGPSAvailable()) {
            return
        }

        @SuppressLint("RestrictedApi")
        val activity = ContextUtils.getActivity(context) ?: return

        // https://developer.android.com/guide/playcore/in-app-review?hl=zh-cn
        val manager = ReviewManagerFactory.create(context)
        activity.androidLifecycle.launch {
            try {
                val reviewInfo = manager.requestReview()
                manager.launchReview(activity, reviewInfo)
            } catch (e: ReviewException) {
            }
        }
    }

    private fun Context.isGPSAvailable(): Boolean {
        return GoogleApiAvailabilityLight.getInstance()
            .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
    }

}