package com.dede.android_eggs.util

import android.content.Context
import androidx.window.embedding.RuleController
import androidx.window.embedding.SplitController
import com.dede.android_eggs.R
import com.dede.basic.globalContext

/**
 * SplitController API.
 *
 * @author shhu
 * @since 2023/5/22
 */
@Suppress("OPT_IN_USAGE")
object SplitUtils {

    fun initialize(context: Context) {
        val ruleController = RuleController.getInstance(context)
        val rules = RuleController.parseRules(context, R.xml.split_configuration)
        ruleController.setRules(rules)
    }

    fun isSplitSupported(): Boolean {
        return SplitController.getInstance(globalContext).splitSupportStatus ==
                SplitController.SplitSupportStatus.SPLIT_AVAILABLE
    }

}