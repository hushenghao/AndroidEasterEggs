package com.android_j.egg


import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
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
 * Android Jelly Bean PlatLogo test
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class PlatLogoActivityTest : EasterEggsActivityBaseTest() {

    @Test
    fun platLogoActivityTest() {

        testPlatLogo(R.string.title_android_j)

        onView(allOf(withId(android.R.id.content),
            withChild(`is`(instanceOf(ImageView::class.java)))))
            .check(matches(isDisplayed()))
            .perform(click(), longClick())

        pressBack()
    }

}
