package com.dede.android_eggs

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.dede.basic.GlobalContext
import com.dede.basic.getBoolean
import me.weishu.reflection.Reflection

class EasterEggsApp : Application() {

    override fun attachBaseContext(base: Context?) {
        Reflection.unseal(base)
        GlobalContext.init(this)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ActivityPermissionRequester())
        applyNightMode()
    }

    private fun applyNightMode() {
        val nightMode = if (getBoolean("key_night_mode", false))
            AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}