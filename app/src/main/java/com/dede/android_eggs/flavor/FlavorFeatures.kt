package com.dede.android_eggs.flavor

import android.app.Activity
import androidx.activity.ComponentActivity
import com.dede.android_eggs.FlavorFeaturesImpl

interface FlavorFeatures {

    companion object {

        const val TAG = "FlavorFeatures"

        fun get(): FlavorFeatures {
            return FlavorFeaturesImpl()
        }
    }

    fun launchReview(activity: ComponentActivity)

    suspend fun checkUpdate(activity: Activity): LatestVersion?
}
