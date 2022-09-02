package com.android_l.egg


import android.view.KeyEvent
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dede.android_eggs.EasterEggsActivityBaseTest
import com.dede.android_eggs.R
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android Lollipop PlatLogo test
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class PlatLogoActivityTest : EasterEggsActivityBaseTest() {

    @Test
    fun platLogoActivityTest() {

        testPlatLogo(R.string.title_android_l)

        onView(allOf(withId(android.R.id.content),
            withChild(`is`(instanceOf(FrameLayout::class.java)))))
            .check(matches(isDisplayed()))

        onView(withChild(`is`(instanceOf(ImageView::class.java))))
            .check(matches(isDisplayed()))
            .perform(click(), pressKey(KeyEvent.KEYCODE_A), longClick())

        onView(withId(com.android_l.egg.R.id.world))
            .check(matches(isDisplayed()))
            .perform(click(),click())

        pressBack()
    }

}
