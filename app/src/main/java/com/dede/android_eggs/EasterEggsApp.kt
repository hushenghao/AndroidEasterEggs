package com.dede.android_eggs

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.android_eggs.util.IconShapeOverride
import com.dede.android_eggs.util.NightModeManager
import com.dede.basic.GlobalContext
import me.weishu.reflection.Reflection

class EasterEggsApp : Application() {

    override fun attachBaseContext(base: Context?) {
        Reflection.unseal(base)
        GlobalContext.init(this)
        IconShapeOverride.apply(base)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        NightModeManager.applyNightMode(this)
        ActivityActionDispatcher.register(this)
    }

    override fun getResources(): Resources {
        return IconShapeOverride.getResources(this, super.getResources())
    }

}