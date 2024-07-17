package com.dede.android_eggs.startup

import android.content.Context
import androidx.startup.Initializer
import androidx.window.embedding.RuleController
import com.dede.android_eggs.R

class SplitInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        try {
            val ruleController = RuleController.getInstance(context)
            val rules = RuleController.parseRules(context, R.xml.split_configuration)
            ruleController.setRules(rules)
        } catch (ignore: RuntimeException) {
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}