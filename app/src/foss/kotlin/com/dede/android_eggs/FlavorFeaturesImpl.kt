package com.dede.android_eggs

import android.app.Activity
import androidx.activity.ComponentActivity
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.flavor.LatestVersion
import com.dede.android_eggs.navigation.OverlayManager
import com.dede.android_eggs.util.RatingPromptScheduler
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy

class FlavorFeaturesImpl : FlavorFeatures {

    override fun promptForRating(activity: ComponentActivity, overlayManager: OverlayManager) {
        if (!isAgreedPrivacyPolicy(activity)) {
            return
        }
        val scheduler = RatingPromptScheduler(activity) { markRequested ->
            overlayManager.show(GithubStarRoute)
            markRequested()
        }
        activity.lifecycle.addObserver(scheduler)
    }

    override suspend fun checkUpdate(activity: Activity): Result<LatestVersion> {
        return GithubReleaseApi.fetchLatestVersion()
    }
}
