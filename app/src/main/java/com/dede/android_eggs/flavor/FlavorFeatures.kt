package com.dede.android_eggs.flavor

import android.app.Activity
import androidx.activity.ComponentActivity
import com.dede.android_eggs.FlavorFeaturesImpl
import com.dede.android_eggs.navigation.OverlayManager

interface FlavorFeatures {

    companion object {

        const val TAG = "FlavorFeatures"

        fun get(): FlavorFeatures {
            return FlavorFeaturesImpl()
        }
    }

    fun promptForRating(activity: ComponentActivity, overlayManager: OverlayManager)

    suspend fun checkUpdate(activity: Activity): Result<LatestVersion>
}
