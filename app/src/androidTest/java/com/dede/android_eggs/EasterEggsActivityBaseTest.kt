package com.dede.android_eggs

import androidx.annotation.StringRes
import androidx.preference.R
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule

/**
 * Easter Egg main test
 *
 * @author shhu
 * @since 2022/9/2
 */
open class EasterEggsActivityBaseTest {

    companion object {

        fun testPlatLogo(@StringRes titleRes: Int) {
            // EasterEggsActivity launch Easter Egg
            onView(withId(R.id.recycler_view))
                .check(matches(ViewMatchers.isDisplayed()))
                .perform(actionOnHolderItem(EggPreferenceMatcher(titleRes), click()))
        }
    }

    @get:Rule
    open var eggsActivityScenarioRule = ActivityScenarioRule(EasterEggsActivity::class.java)
}