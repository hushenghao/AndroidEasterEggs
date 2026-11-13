package com.dede.android_eggs

import android.app.Activity
import androidx.activity.ComponentActivity
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.flavor.LatestVersion
import com.dede.android_eggs.navigation.OverlayManager

class FlavorFeaturesImpl : FlavorFeatures {
    override fun promptForRating(
        activity: ComponentActivity,
        @Suppress("UNUSED_PARAMETER") overlayManager: OverlayManager,
    ) {
        GooglePlayCore.promptForRating(activity)
    }

    override suspend fun checkUpdate(activity: Activity): Result<LatestVersion> {
        GooglePlayCore.checkUpdate(activity)
        return Result.failure(UnsupportedOperationException("market flavor uses Google Play in-app update; no LatestVersion returned"))
    }
}