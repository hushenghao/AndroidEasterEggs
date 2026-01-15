package com.dede.android_eggs

import android.app.Activity
import androidx.activity.ComponentActivity
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.flavor.LatestVersion

class FlavorFeaturesImpl : FlavorFeatures {
    override fun launchReview(activity: ComponentActivity) {
        GooglePlayCore.launchReview(activity)
    }

    override suspend fun checkUpdate(activity: Activity): LatestVersion? {
        GooglePlayCore.checkUpdate(activity)
        return null
    }
}