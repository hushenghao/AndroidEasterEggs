package com.dede.android_eggs

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.dede.android_eggs.util.launchCatchable
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory

object GooglePlayCore {

    private const val TAG = "GooglePlayCore"

    private const val KEY_LAUNCH_REVIEW_COUNT = "key_launch_review_count"

    private fun isLaunchReviewTiming(context: Context): Boolean {
        val count = context.pref.getInt(KEY_LAUNCH_REVIEW_COUNT, 0)
        try {
            return count == 3 || (count >= 10 && count % 10 == 0)
        } finally {
            context.pref.edit { putInt(KEY_LAUNCH_REVIEW_COUNT, count + 1) }
        }
    }

    @JvmStatic
    fun isGooglePlayServicesAvailable(context: Context): Boolean {
        // https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability#isGooglePlayServicesAvailable(android.content.Context)
        //val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        //
        // Remove `com.google.android.gms:play-services-base:18.7.0 (GoogleApiAvailability)` dependency,
        // fix `com.google.android.gms:play-services-base:18.7.0` app must set `isCoreLibraryDesugaringEnabled = true`.
        //
        // - com.google.android.play:review-ktx:2.0.2
        //   - com.google.android.gms:play-services-basement:18.7.0 (GoogleApiAvailabilityLight)
        //
        // replace `GoogleApiAvailability` with `GoogleApiAvailabilityLight`
        //
        // https://developers.google.com/android/guides/releases?hl=zh-cn#april_14_2025
        // https://issuetracker.google.com/issues/339232491
        // https://developer.android.com/studio/write/java8-support?hl=zh-cn#library-desugaring
        val status = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context)
        Log.i(TAG, "isGooglePlayServicesAvailable: %d".format(status))
        return status == ConnectionResult.SUCCESS
    }

    @JvmStatic
    fun launchReview(activity: ComponentActivity) {
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
