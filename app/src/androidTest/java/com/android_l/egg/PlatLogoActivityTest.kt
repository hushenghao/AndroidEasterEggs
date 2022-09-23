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
import com.dede.android_eggs.ViewActionsExt
import com.dede.android_eggs.ViewActionsExt.delay
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

        onView(
            allOf(
                withId(android.R.id.content),
                withChild(withChild(`is`(instanceOf(ImageView::class.java))))
            )
        ).check(matches(isDisplayed()))
            .perform(
                click(), pressKey(KeyEvent.KEYCODE_A),
                click(),
                click(),
                click(),
                click(),// mTapCount >= 5
                longClick()
            )

        onView(withId(com.android_l.egg.R.id.world))
            .check(matches(isDisplayed()))
            .perform(
                // play game
                click(),
                click(pressKey(KeyEvent.KEYCODE_SPACE)),
                delay(2000),
                // play again
                click(pressKey(KeyEvent.KEYCODE_ENTER)),
                click()
            )

        pressBack()
    }

}
