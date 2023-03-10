package com.dede.android_eggs

import android.content.Context
import com.dede.android_eggs.settings.SettingsPref
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.android_eggs.util.IconShapeOverride
import com.dede.basic.GlobalContext
import me.weishu.reflection.Reflection

class EasterEggsApp : IconShapeOverride.App() {

    override fun attachBaseContext(base: Context?) {
        Reflection.unseal(base)
        GlobalContext.init(this)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        SettingsPref.apply(this)
        ActivityActionDispatcher.register(this)
    }

}