package com.dede.android_eggs

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.dede.android_eggs.views.main.EasterEggFunctions
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EasterEggsApp : Application(), AppFunctionConfiguration.Provider {

    @Inject
    lateinit var easterEggFunctions: EasterEggFunctions

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() = AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(EasterEggFunctions::class.java) { easterEggFunctions }
            .build()
}