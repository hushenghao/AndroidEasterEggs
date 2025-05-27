package com.dede.android_eggs.embedding_splits

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import androidx.window.WindowSdkExtensions
import androidx.window.embedding.ActivityEmbeddingController
import androidx.window.embedding.EmbeddingConfiguration
import androidx.window.embedding.RuleController

class SplitInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        try {
            val ruleController = RuleController.getInstance(context)
            val rules = RuleController.parseRules(context, R.xml.split_configuration)
            ruleController.setRules(rules)
        } catch (ignore: RuntimeException) {
        }

        @SuppressLint("RequiresWindowSdk")
        if (WindowSdkExtensions.getInstance().extensionVersion >= 5) {
            ActivityEmbeddingController.getInstance(context)
                .setEmbeddingConfiguration(
                    EmbeddingConfiguration.Builder()
                        .setDimAreaBehavior(EmbeddingConfiguration.DimAreaBehavior.ON_TASK)
                        .build()
                )
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
