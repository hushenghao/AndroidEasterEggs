package com.dede.android_eggs.util

import android.content.Context
import androidx.window.embedding.RuleController
import androidx.window.embedding.SplitController
import com.dede.android_eggs.R

/**
 * SplitController API.
 *
 * @author shhu
 * @since 2023/5/22
 */
object SplitUtils {

    fun initialize(context: Context) {
        val ruleController = RuleController.getInstance(context)
        val rules = RuleController.parseRules(context, R.xml.split_configuration)
        ruleController.setRules(rules)
    }

    fun isSplitSupported(context: Context): Boolean {
        return SplitController.getInstance(context).splitSupportStatus ==
                SplitController.SplitSupportStatus.SPLIT_AVAILABLE
    }

}