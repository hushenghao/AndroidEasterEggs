package com.dede.android_eggs

import androidx.activity.ComponentActivity
import com.dede.android_eggs.inject.FlavorFeatures

class FlavorFeaturesImpl : FlavorFeatures {
    override fun call(activity: ComponentActivity) {
        GooglePlayCore.launchReview(activity)
    }
}