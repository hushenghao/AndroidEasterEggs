package com.dede.android_eggs

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import org.hamcrest.Matcher

/**
 * Created by shhu on 2022/9/2 15:14.
 *
 * @author shhu
 * @since 2022/9/2
 */
object ViewActionsExt {

    fun delay(millisDelay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayingAtLeast(90)
            }

            override fun getDescription(): String {
                return "loopMainThread: $millisDelay"
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(millisDelay)
            }

        }
    }
}