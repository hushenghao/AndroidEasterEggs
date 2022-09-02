package com.android_i.egg


import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dede.android_eggs.EasterEggsActivityBaseTest
import com.dede.android_eggs.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android Ice Cream Sandwich PlatLogo test
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class PlatLogoActivityTest : EasterEggsActivityBaseTest() {

    @Test
    fun platLogoActivityTest() {

        testPlatLogo(R.string.title_android_i)

        // PlatLogoActivity
        onView(allOf(withId(android.R.id.content),
            withChild(`is`(instanceOf(ImageView::class.java)))))
            .check(matches(isDisplayed()))
            .perform(object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return isDisplayingAtLeast(90)
                }

                override fun getDescription(): String {
                    return "MotionEvent down"
                }

                override fun perform(uiController: UiController, view: View) {
                    MotionEvents.sendDown(uiController,
                        floatArrayOf(view.x + view.width / 2f, view.y + view.height / 2f),
                        floatArrayOf(0f, 0f))
                    // launch Nyandroid
                    uiController.loopMainThreadForAtLeast(ViewConfiguration.getLongPressTimeout() * 2 * 3L)
                }
            })

        // Nyandroid
        onView(allOf(withId(android.R.id.content),
            withChild(`is`(instanceOf(Nyandroid.Board::class.java)))))
            .check(matches(isDisplayed()))

        // popup
        pressBack()
    }

}
