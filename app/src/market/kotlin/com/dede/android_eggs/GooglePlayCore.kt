package com.dede.android_eggs

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.dede.android_eggs.util.launchCatchable
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory

object GooglePlayCore {

    private const val TAG = "GooglePlayCore"

    private const val KEY_LAUNCH_REVIEW_COUNT = "key_launch_review_count"

    private fun isLaunchReviewTiming(context: Context): Boolean {
        val count = context.pref.getInt(KEY_LAUNCH_REVIEW_COUNT, 0)
        try {
            return count >= 5 && count % 5 == 0
        } finally {
            context.pref.edit().putInt(KEY_LAUNCH_REVIEW_COUNT, count + 1).apply()
        }
    }

    @JvmStatic
    fun isGooglePlayServicesAvailable(context: Context): Boolean {
        // https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability#isGooglePlayServicesAvailable(android.content.Context)
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        Log.i(TAG, "isGooglePlayServicesAvailable: %d".format(status))
        return status == ConnectionResult.SUCCESS
    }

    @JvmStatic
    fun launchReview(activity: FragmentActivity) {
        if (!isAgreedPrivacyPolicy(activity) ||
            !isGooglePlayServicesAvailable(activity) ||
            !isLaunchReviewTiming(activity)
        ) {
            return
        }

        activity.lifecycleScope.launchCatchable {
            // https://developer.android.com/guide/playcore/in-app-review
            val reviewManager = ReviewManagerFactory.create(activity)
            val reviewInfo = reviewManager.requestReview()
            reviewManager.launchReview(activity, reviewInfo)
        }
    }
}
