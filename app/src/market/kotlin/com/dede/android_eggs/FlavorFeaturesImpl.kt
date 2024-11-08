package com.dede.android_eggs

import androidx.fragment.app.FragmentActivity
import com.dede.android_eggs.inject.FlavorFeatures

class FlavorFeaturesImpl : FlavorFeatures {
    override fun call(activity: FragmentActivity) {
        GooglePlayCore.launchReview(activity)
    }
}