package com.android_m.egg


import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dede.android_eggs.EasterEggsActivityBaseTest
import com.dede.android_eggs.R
import com.dede.android_eggs.ViewActionsExt.delay
import com.dede.android_eggs.ViewActionsExt.loopClick
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android Marshmallow PlatLogo test
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class PlatLogoActivityTest : EasterEggsActivityBaseTest() {

    @Test
    fun platLogoActivityTest() {

        testPlatLogo(R.string.title_android_m)

        onView(
            allOf(
                withId(android.R.id.content),
                withChild(`is`(instanceOf(FrameLayout::class.java))),
                withChild(`is`(instanceOf(View::class.java)))
            )
        )
            .check(matches(isDisplayed()))
            .perform(
                click(), pressKey(KeyEvent.KEYCODE_A),
                click(),
                click(),
                click(),
                click(),// mTapCount >= 5
                longClick()
            )


        val scores = onView(withId(com.android_m.egg.R.id.scores))
            .check(matches(isDisplayed()))
        onView(withId(com.android_m.egg.R.id.player_plus_button))
            .check(matches(isDisplayed()))
            .perform(*loopClick(5))
            .check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        scores.check(matches(hasChildCount(6)))

        onView(withId(com.android_m.egg.R.id.player_minus_button))
            .check(matches(isDisplayed()))
            .perform(*loopClick(5))
            .check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        scores.check(matches(hasChildCount(1)))

        // start
        onView(withId(com.android_m.egg.R.id.play_button))
            .check(matches(isDisplayed()))
            .perform(click(), delay(3000L))

        onView(withId(com.android_m.egg.R.id.world))
            .check(matches(isDisplayed()))
            .perform(
                // play game
                click(),
                click(pressKey(KeyEvent.KEYCODE_SPACE)),
                delay(2000L),
                // play again
                click(pressKey(KeyEvent.KEYCODE_ENTER)),
                click()
            )

        pressBack()
    }

}
