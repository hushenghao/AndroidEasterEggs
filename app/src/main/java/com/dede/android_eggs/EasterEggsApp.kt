package com.dede.android_eggs

import android.app.Application
import android.content.Context
import me.weishu.reflection.Reflection

class EasterEggsApp : Application() {

    override fun attachBaseContext(base: Context?) {
        Reflection.unseal(base)
        super.attachBaseContext(base)
    }
}